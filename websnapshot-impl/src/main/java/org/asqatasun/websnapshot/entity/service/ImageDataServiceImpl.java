/*
 * Web-Snapshot
 * Copyright (C) 2008-2017  Asqatasun.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact us by mail: asqatasun AT asqatasun DOT org
 */
package org.asqatasun.websnapshot.entity.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import javax.persistence.NoResultException;
import org.apache.log4j.Logger;
import org.asqatasun.sdk.entity.service.AbstractGenericDataService;
import org.asqatasun.websnapshot.entity.Image;
import org.asqatasun.websnapshot.entity.Image.Status;
import org.asqatasun.websnapshot.entity.dao.ImageDAO;
import org.asqatasun.websnapshot.entity.factory.ImageFactory;
import org.asqatasun.websnapshot.imageconverter.utils.ConvertImage;
import org.asqatasun.websnapshot.service.SnapshotCreationResponse;
import org.asqatasun.websnapshot.service.SnapshotCreator;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ImageDataServiceImpl extends AbstractGenericDataService<Image, Long>
        implements ImageDataService {

    private static final int DEFAULT_WINDOW_WIDTH = 1024;
    private static final int DEFAULT_WINDOW_HEIGHT = 768;
    private static final Logger LOGGER = Logger.getLogger(ImageDataServiceImpl.class);
    private static final long ONE_DAY = 86400000L;
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private long lifetime = 1;
    private UrlDataService urlDataService;
    private SnapshotCreator snapshotCreator;
    private Pattern notExpirableUrlPattern;

    public void setNotExpirableUrlPattern(String notExpirableUrlPattern) {
        this.notExpirableUrlPattern = Pattern.compile(notExpirableUrlPattern);
    }

    public SnapshotCreator getSnapshotCreator() {
        return snapshotCreator;
    }

    public void setSnapshotCreator(SnapshotCreator snapshotCreator) {
        this.snapshotCreator = snapshotCreator;
    }

    public UrlDataService getUrlDataService() {
        return urlDataService;
    }

    public void setUrlDataService(UrlDataService urlDataService) {
        this.urlDataService = urlDataService;
    }

    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        return threadPoolTaskExecutor;
    }

    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    public ImageDataServiceImpl() {
        super();
    }

    @Override
    public Image forceImageCreation(String url, int width, int height) {
        try {
            Image canonicalImage = getCanonicalImageFromUrl(url);
            Image notCanonicalImage = createTechnicalNotCanonicalObject(url, width, height);
            ImageCreationThread imageCreationThread = new ImageCreationThread(url, this, canonicalImage, notCanonicalImage, width, height);
            threadPoolTaskExecutor.submit(imageCreationThread);
            return notCanonicalImage;
        } catch (NoResultException nre1) {
            Image canonicalImage = createTechnicalCanonicalObject(url);
            Image notCanonicalImage = createTechnicalNotCanonicalObject(url, width, height);
            ImageCreationThread imageCreationThread = new ImageCreationThread(url, this, canonicalImage, notCanonicalImage, width, height);
            threadPoolTaskExecutor.submit(imageCreationThread);
            return notCanonicalImage;
        }
    }

    @Override
    public Image getNotCreatedImage(int width, int height) {
        Image fakeImage = ((ImageFactory) entityFactory).createNotCreatedImage();
        fakeImage.setHeight(height);
        fakeImage.setWidth(width);
        return fakeImage;
    }

    @Override
    public Image getImageFromWidthAndHeightAndUrl(int width, int height, String url, boolean status) {
        try {
            Image image = ((ImageDAO) entityDao).findImageByWidthAndHeightAndUrl(width, height, url);
            if (status) {
                return image;
            }
            // if an image is found but expired regarding default lifetime value,
            // a new one is created.
            if (image.getStatus().equals(Status.CREATED) && isExpired(url, image)) {
                return createCanonicalAndNoCanonicalImage(width, height, url, null, null);
            }
            return image;
        } catch (NoResultException nre) {
            if (status) {
                return null;
            }
            LOGGER.debug("The requested image with width: " + width
                    + " height: " + height
                    + " url: " + url
                    + " hasn't been found and has to be created");
            return getImage(url, width, height);
        }
    }

    @Override
    public Image getImageFromWidthAndHeightAndUrlAndDate(int width, int height, String url, Date date) {
        Object object = ((ImageDAO) entityDao).findImageFromDateAndUrlAndWidthAndHeight(url, date, width, height);
        try {
            Image image = (Image) object;
            if (image.getStatus().equals(Status.CREATED) || image.getStatus().equals(Status.IN_PROGRESS)) {
                return image;
            }
        } catch (Exception ex) {
            LOGGER.debug("Impossible to cast object returning from findImageFromDateAndUrlAndWidthAndHeight to Image");
            try {
                Status status = (Status) object;
                if (status.equals(Status.MUST_BE_CREATE)) {
                    return getImage(url, width, height);
                } else if (status.equals(Status.NOT_EXIST)) {
                    return null;
                } else {
                    LOGGER.debug("The requested image with width: " + width
                            + " height: " + height
                            + " url: " + url
                            + " date: " + date.toString()
                            + " hasn't been found and we returned null");
                    return null;
                }
            } catch (Exception ex1) {
                LOGGER.debug("Impossible to cast object returning from findImageFromDateAndUrlAndWidthAndHeight to Status");
            }
        }
        return null;
    }

    /**
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    private Image getImage(String url, int width, int height) {
        try {
            Image canonicalImage = getCanonicalImageFromUrl(url);
            if (canonicalImage.getStatus().equals(Status.CREATED)) {
                return createNotCanonicalImage(canonicalImage, width, height, url, null);
            }
            // in this case, that means that the creation of the canonical 
            // image (the snaphost) is in progress. We return this image 
            // to make use of its status
            LOGGER.debug("Returning the canonical image with status in_progress "
                    + canonicalImage.getStatus());
            return canonicalImage;
        } catch (NoResultException nre1) {
            LOGGER.debug("The requested url " + url
                    + " has no canonical image. Creating it before creating "
                    + "the thumbnail");
            return createCanonicalAndNoCanonicalImage(width, height, url, null, null);
        }
    }

    /**
     *
     * @param url
     * @return
     */
    private Image getCanonicalImageFromUrl(String url) {
        return ((ImageDAO) entityDao).findCanonicalImageByUrl(url);
    }

    @Override
    public Image createCanonicalAndNoCanonicalImage(int width, int height, String url, Image canonicalImage, Image notCanonicalImage) {
        if (canonicalImage == null) {
            canonicalImage = createTechnicalCanonicalObject(url);
            createCanonicalImage(canonicalImage, url, width, height);
            return createNotCanonicalImage(canonicalImage, width, height, url, null);
        } else {
            if (!canonicalImage.getStatus().equals(Status.CREATED) || !canonicalImage.getStatus().equals(Status.IN_PROGRESS)) {
                createCanonicalImage(canonicalImage, url, width, height);
            }
            return createNotCanonicalImage(canonicalImage, width, height, url, notCanonicalImage);
        }
    }

    /**
     *
     * @param url
     * @return
     */
    private Image createTechnicalCanonicalObject(String url) {
        Image canonicalImage = createImageWithProperties(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, url, true);
        canonicalImage.setStatus(Status.IN_PROGRESS);
        return saveOrUpdate(canonicalImage);
    }

    private Image createTechnicalNotCanonicalObject(String url, int width, int height) {
        Image notCanonicalImage = createImageWithProperties(width, height, url, false);
        notCanonicalImage.setStatus(Status.IN_PROGRESS);
        return saveOrUpdate(notCanonicalImage);
    }

    private SnapshotCreationResponse getDataToCanonicalImage(String url) {
        return snapshotCreator.requestSnapshotCreation(url);
    }

    /**
     * a canonical image is a snapshot of a given url that will be used to
     * create the thumbnails of that given url
     *
     * @param url
     * @return a canonical image (with no resizing)
     */
    @Override
    public Image createCanonicalImage(Image canonicalImage, String url, int width, int height) {
        SnapshotCreationResponse response = getDataToCanonicalImage(url);

        /*
         * Si l'image a un statut SUCCESS, on la créé puis on la sauvegarde en base.
         * Dans le cas contraire, on supprime l'image canonique,
         * puis créé une fausse image permettant de recuperer le code + le message
         * d'erreur HTTP dans l'image.
         */
        if (response.getStatus().equals(SnapshotCreationResponse.SUCCESS)) {
            canonicalImage.setWidth(response.getWidth());
            canonicalImage.setHeight(response.getHeight());
            canonicalImage.setRawData(response.getRawImage());
            canonicalImage.setStatus(Status.CREATED);
            canonicalImage.setUrl(getUrlDataService().getUrlFromStringUrl(url));
            return saveOrUpdate(canonicalImage);
        } else {
            delete(canonicalImage.getId());
            Image errorMockImage = createImageWithProperties(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, url, true);
            try {
                errorMockImage.setRawData(ConvertImage.createThumbnailFromErrorMessage(response.getStatus(), width, height));
                errorMockImage.setStatus(Status.HACK_CREATED);
                return errorMockImage;
            } catch (IOException ex) {
                errorMockImage.setStatus(Status.ERROR);
                return errorMockImage;
            }
        }
    }

    /**
     *
     * @param image
     * @param width
     * @param height
     * @param url
     * @return a no canonical image (with resizing)
     */
    @Override
    public Image createNotCanonicalImage(Image canonicalImage, int width, int height, String url, Image notCanonicalImage) {
        if (notCanonicalImage == null) {
            notCanonicalImage = createImageWithProperties(width, height, url, false);
        }
        notCanonicalImage.setStatus(Status.IN_PROGRESS);
        notCanonicalImage = saveOrUpdate(notCanonicalImage);

        try {
            notCanonicalImage.setRawData(ConvertImage.createThumbnailFromScreenshot(
                    ConvertImage.byteArrayImageToBufferedImage(canonicalImage.getRawData()),
                    width,
                    height));
        } catch (IOException ex) {
        }

        notCanonicalImage.setStatus(Status.CREATED);
        saveOrUpdate(notCanonicalImage);

        if (canonicalImage.getStatus() == Status.HACK_CREATED
                || canonicalImage.getStatus() == Status.ERROR) {
            delete(notCanonicalImage.getId());
        }

        return notCanonicalImage;
    }

    /**
     * @param width
     * @param height
     * @param url
     * @return the image with set properties
     */
    private Image createImageWithProperties(int width, int height, String url, boolean canonical) {
        Image image = create();
        image.setUrl(getUrlDataService().getUrlFromStringUrl(url));
        image.setHeight(height);
        image.setWidth(width);
        image.setDateOfCreation(Calendar.getInstance().getTime());
        image.setIsCanonical(canonical);
        return image;
    }

    /**
     *
     * @param url
     * @param image
     * @return
     */
    private boolean isExpired(String url, Image image) {
        // if the requested url matches the pattern, that means that it never
        // expires. 
        if (notExpirableUrlPattern != null
                && notExpirableUrlPattern.pattern().matches(url)) {
            return false;
        }
        // if the date of the last found snapshot is anterior than the current 
        // date minus the lifetime, we consider it as expired
        boolean isExpirated = (Calendar.getInstance().getTimeInMillis() - image.getDateOfCreation().getTime()) > (lifetime * ONE_DAY);
        LOGGER.debug("IS EXPIRATED ? " + isExpirated);
        return (Calendar.getInstance().getTimeInMillis() - image.getDateOfCreation().getTime()) > (lifetime * ONE_DAY);
    }

    private class ImageCreationThread implements Runnable {

        private String url;
        private ImageDataService imageDataService;
        private Image canonicalImage;
        private Image notCanonicalImage;
        private int width;
        private int height;
        private boolean isCreated = false;

        public ImageCreationThread(String url, ImageDataService imageDataService, Image canonicalImage, Image notCanonicalImage, int width, int height) {
            this.url = url;
            this.imageDataService = imageDataService;
            this.canonicalImage = canonicalImage;
            this.notCanonicalImage = notCanonicalImage;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            this.notCanonicalImage = imageDataService.createCanonicalAndNoCanonicalImage(
                    width,
                    height,
                    url,
                    canonicalImage,
                    notCanonicalImage);
            isCreated = true;
        }

        public Image getImage() {
            return this.notCanonicalImage;
        }

        public void setImage(Image notCanonicalImage) {
            this.notCanonicalImage = notCanonicalImage;
        }

        public boolean isCreated() {
            return isCreated;
        }
    }
}
/*
 * Web-Snapshot
 * Copyright (C) 2008-2014  Open-S Company
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
 * Contact us by mail: open-s AT open-s DOT com
 */
package org.opens.websnapshot.entity.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import javax.persistence.NoResultException;
import org.apache.log4j.Logger;
import org.opens.tanaguru.sdk.entity.service.AbstractGenericDataService;
import org.opens.websnapshot.entity.Image;
import org.opens.websnapshot.entity.Image.Status;
import org.opens.websnapshot.entity.dao.ImageDAO;
import org.opens.websnapshot.imageconverter.utils.ConvertImage;
import org.opens.websnapshot.service.SnapshotCreationResponse;
import org.opens.websnapshot.service.SnapshotCreator;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ImageDataServiceImpl extends AbstractGenericDataService<Image, Long>
        implements ImageDataService {

    private static final int DEFAULT_WINDOW_WIDTH = 1024;
    private static final int DEFAULT_WINDOW_HEIGHT = 768;
    private static final Logger LOGGER = Logger.getLogger(ImageDataServiceImpl.class);
    private static final long DEFAULT_LIFETIME = 3000000;
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private long lifetime = DEFAULT_LIFETIME;
    private UrlDataService urlDataService;
    private SnapshotCreator snapshotCreator;
    private Pattern notExpirableUrlPattern;

    public void setNotExpirableUrlRegexp(String notExpirableUrlRegexp) {
        notExpirableUrlPattern = Pattern.compile(notExpirableUrlRegexp);
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

    public ImageDataServiceImpl() {
        super();
    }

    @Override
    public Image forceImageCreation(String url) {
        Image canonicalImage = createTechnicalCanonicalObject(url);
        ImageCreationThread imageCreationThread = new ImageCreationThread(url, this, canonicalImage, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        threadPoolTaskExecutor.submit(imageCreationThread);
        return canonicalImage;
    }

    @Override
    public Image getImageFromWidthAndHeightAndUrl(int width, int height, String url) {
        try {
            Image image = ((ImageDAO) entityDao).findImageByWidthAndHeightAndUrl(width, height, url);
            // if an image is found but expired regarding default lifetime value,
            // a new one is created.
            if (image.getStatus().equals(Status.CREATED) && isExpirated(url, image)) {
                return createCanonicalAndNoCanonicalImage(width, height, url);
            }
            return image;
        } catch (NoResultException nre) {
            LOGGER.debug("The requested image with width: " + width
                    + " height: " + height
                    + " url: " + url
                    + " hasn't been found and has to created");
            return getImage(url, width, height);
        }
    }

    @Override
    public Image getImageFromWidthAndHeightAndUrlAndDate(int width, int height, String url, Date date) {
        Image image = ((ImageDAO) entityDao).findImageFromDateAndUrlAndWidthAndHeight(url, date, width, height);
        if (image.getStatus().equals(Status.CREATED) || image.getStatus().equals(Status.IN_PROGRESS)) {
            return image;
        } else if (image.getStatus() == Status.ERROR) {
            return null;
        } else {
            LOGGER.debug("The requested image with width: " + width
                    + " height: " + height
                    + " url: " + url
                    + " date: " + date.toString()
                    + " hasn't been found and has to created");
            return getImage(url, width, height);
        }
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
                return createNotCanonicalImage(canonicalImage, width, height, url);
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
            return createCanonicalAndNoCanonicalImage(width, height, url);
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

    /**
     * create a canonical and no canonical image
     *
     * @param width
     * @param height
     * @param url
     * @return the no canonical image
     */
    private Image createCanonicalAndNoCanonicalImage(int width, int height, String url) {
        Image image = createTechnicalCanonicalObject(url);
        createCanonicalImage(image, url, width, height);
        return createNotCanonicalImage(image, width, height, url);
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
    private Image createNotCanonicalImage(Image canonicalImage, int width, int height, String url) {
        Image notCanonicalImage = createImageWithProperties(width, height, url, false);
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
    private boolean isExpirated(String url, Image image) {
        // if the requested url matches the pattern, that means that it never
        // expires. 
        if (notExpirableUrlPattern != null
                && notExpirableUrlPattern.pattern().matches(url)) {
            return false;
        }
        // if the date of the last found snapshot is anterior than the current 
        // date minus the lifetime, we consider it as expired
        return (Calendar.getInstance().getTimeInMillis() - image.getDateOfCreation().getTime()) > lifetime;
    }

    private class ImageCreationThread implements Runnable {

        private String url;
        private ImageDataService imageDataService;
        private Image canonicalImage;
        private int width;
        private int height;
        private boolean isCreated = false;

        public ImageCreationThread(String url, ImageDataService imageDataService, Image canonicalImage, int width, int height) {
            this.url = url;
            this.imageDataService = imageDataService;
            this.canonicalImage = canonicalImage;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            this.canonicalImage =
                    imageDataService.createCanonicalImage(
                    canonicalImage,
                    url,
                    width,
                    height);
            isCreated = true;
        }

        public Image getImage() {
            return this.canonicalImage;
        }

        public void setImage(Image canonicalImage) {
            this.canonicalImage = canonicalImage;
        }

        public boolean isCreated() {
            return isCreated;
        }
    }
}
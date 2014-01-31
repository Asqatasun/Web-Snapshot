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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.opens.tanaguru.sdk.entity.service.AbstractGenericDataService;
import org.opens.websnapshot.entity.Image;
import org.opens.websnapshot.entity.Snapshot;
import org.opens.websnapshot.entity.Thumbnail;
import org.opens.websnapshot.entity.dao.ThumbnailDAO;
import org.opens.websnapshot.service.SnapshotCreator;

import org.opens.websnapshot.imageconverter.utils.ConvertImage;

public class ThumbnailDataServiceImpl extends AbstractGenericDataService<Thumbnail, Long>
        implements ThumbnailDataService {

    private static final long DEFAULT_LIFETIME = 300000;
    private static final long SNAPSHOT_WIDTH = 1024L;
    private static final long SNAPSHOT_HEIGHT = 768L;
    private ImageDataService imageDataService;
    private SnapshotDataService snapshotDataService;
    private SnapshotCreator snapshotCreator;
    private long lifetime = DEFAULT_LIFETIME;
    private String notExpirableUrlRegexp = "test";
    private Pattern notExpirableUrlPattern;

    public SnapshotCreator getSnapshotCreator() {
        return snapshotCreator;
    }

    public void setSnapshotCreator(SnapshotCreator snapshotCreator) {
        this.snapshotCreator = snapshotCreator;
    }

    public ImageDataService getImageDataService() {
        return imageDataService;
    }

    public void setImageDataService(ImageDataService imageDataService) {
        this.imageDataService = imageDataService;
    }

    public SnapshotDataService getSnapshotDataService() {
        return snapshotDataService;
    }

    public void setSnapshotDataService(SnapshotDataService snapshotDataService) {
        this.snapshotDataService = snapshotDataService;
    }

    public ThumbnailDataServiceImpl() {
        super();
        notExpirableUrlPattern = Pattern.compile(notExpirableUrlRegexp);
    }

    @Override
    public Thumbnail getThumbnailFromUrlAndWidthAndHeight(String url, int width, int height) {
        Thumbnail thumbnail = ((ThumbnailDAO) entityDao).findThumbnailFromUrlAndWidthAndHeight(url, width, height);

        if (thumbnail != null) {
            if (isExpirated(url, thumbnail)) {
                // if the thumbnail exists and is expired, we recreate it
                return createSnapshotAndThumbnail(url, width, height);
            }
            // if the thumbnail exists and is not expired, we return it
            return thumbnail;
        }

        Snapshot snapshot = snapshotDataService.getSnapshotFromUrl(url);
        if (snapshot != null) {
            return createThumbnail(snapshot.getImage(), snapshot, width, height);
        }
        return createSnapshotAndThumbnail(url, width, height);
    }

    /**
     *
     * @param url
     * @param width
     * @param height
     * @return a thumbnail created from a new snapshot
     */
    private Thumbnail createSnapshotAndThumbnail(String url, int width, int height) {
        Snapshot snapshot = createNewSnapshot(url);
        return createThumbnail(snapshot.getImage(), snapshot, width, height);
    }

    /**
     *
     * @param url
     * @param thumbnail
     * @return
     */
    private boolean isExpirated(String url, Thumbnail thumbnail) {
        // if the requested url matches the pattern, that means that it never
        // expires. 
        if (notExpirableUrlPattern.pattern().matches(url)) {
            return false;
        }
        // if the date of the last found snapshot is anterior than the current 
        // date minus the lifetime, we consider it as expired
        return Calendar.getInstance().getTimeInMillis() - thumbnail.getSnapshot().getDateOfCreation().getTime() > lifetime;
    }

    @Override
    public Thumbnail getThumbnailFromDateAndUrlAndWidthAndHeight(String url, Date date, int width, int height) {
        Thumbnail thumbnail = ((ThumbnailDAO) entityDao).findThumbnailFromDateAndUrlAndWidthAndHeight(url, date, width, height);

        if (thumbnail != null && thumbnail.getImage() != null) {
            return thumbnail;
        }

        Snapshot snapshot = snapshotDataService.getSnapshotFromUrl(url);
        if (snapshot != null) {
            return createThumbnail(snapshot.getImage(), snapshot, width, height);
        }
        return null;
    }

    /**
     *
     * @param url
     * @return the new snapshot object from a url
     */
    private Snapshot createNewSnapshot(String url) {
        Snapshot snapshot = snapshotDataService.create();
        snapshot.setUrl(url);
        snapshot.setDateOfCreation(Calendar.getInstance().getTime());
        snapshot.setWidth(SNAPSHOT_WIDTH);
        snapshot.setHeight(SNAPSHOT_HEIGHT);

        byte[] rawImage = snapshotCreator.getScreenshot(url);
        Image image = imageDataService.create();

        image.setRawData(rawImage);
        image = imageDataService.saveOrUpdate(image);

        snapshot.setImage(image);
        return snapshotDataService.saveOrUpdate(snapshot);
    }

    /**
     *
     * @param image
     * @param snapshot
     * @param width
     * @param height
     * @return the new thumbnail object that handles the image created with the
     * requested size
     */
    private Thumbnail createThumbnail(
            Image image,
            Snapshot snapshot,
            int width,
            int height) {
        Thumbnail thumbnail = create();

        // start by resizing the snaphost with the requested size, by 
        // instanciating a new image and setting the data and saving it in db.
        Image thumbnailData = imageDataService.create();
        try {
            thumbnailData.setRawData(ConvertImage.createThumbnailFromScreenshot(
                    ConvertImage.byteArrayImageToBufferedImage(image.getRawData()),
                    width,
                    height));
        } catch (IOException ex) {
            Logger.getLogger(ThumbnailDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        imageDataService.saveOrUpdate(thumbnailData);

        // create the thummnail object and link it to the new resized image and
        // the original snapshot
        thumbnail.setHeight(height);
        thumbnail.setWidth(width);
        thumbnail.setImage(thumbnailData);
        thumbnail.setSnapshot(snapshot);

        saveOrUpdate(thumbnail);
        return thumbnail;
    }

    @Override
    public Long getCount() {
        return ((ThumbnailDAO) entityDao).count();
    }
}
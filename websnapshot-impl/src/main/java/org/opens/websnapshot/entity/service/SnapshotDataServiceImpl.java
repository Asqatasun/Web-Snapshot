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

import java.util.Calendar;
import java.util.Date;
import org.opens.tanaguru.sdk.entity.service.AbstractGenericDataService;
import org.opens.websnapshot.entity.Image;
import org.opens.websnapshot.entity.Snapshot;
import org.opens.websnapshot.entity.dao.SnapshotDAO;
import org.opens.websnapshot.service.SnapshotCreator;


public class SnapshotDataServiceImpl extends AbstractGenericDataService<Snapshot, Long>
        implements SnapshotDataService {

    private static final long SNAPSHOT_WIDTH = 1024L;
    private static final long SNAPSHOT_HEIGHT = 768L;
    private ImageDataService imageDataService;
    private SnapshotCreator snapshotCreator;

    public ImageDataService getImageDataService() {
        return imageDataService;
    }

    public void setImageDataService(ImageDataService imageDataService) {
        this.imageDataService = imageDataService;
    }

    public SnapshotCreator getSnapshotCreator() {
        return snapshotCreator;
    }

    public void setSnapshotCreator(SnapshotCreator snapshotCreator) {
        this.snapshotCreator = snapshotCreator;
    }

    public SnapshotDataServiceImpl() {
        super();
    }

    @Override
    public Snapshot getSnapshotFromUrl(String url, boolean forceRecreate) {
        Snapshot snapshot = ((SnapshotDAO) entityDao).findSnapshotByUrl(url);
        if (snapshot != null && snapshot.getImage() != null && forceRecreate == false) {
            return snapshot;
        } else if (snapshot != null && snapshot.getImage() != null && forceRecreate == true) {
            return createNewSnapshot(url);
        }
        return createNewSnapshot(url);
    }

    @Override
    public Snapshot getSnapshotFromUrlandDate(String url, Date date) {
        Snapshot snapshot = ((SnapshotDAO) entityDao).findSnapshotByUrlAndDate(url, date);
        if (snapshot != null && snapshot.getImage() != null) {
            return snapshot;
        }
        return null;
    }

    @Override
    public Snapshot getSnapshotFromScenario(String url, String token, String identifier) {
        Snapshot snapshot = ((SnapshotDAO) entityDao).findSnapshotByUrl(url);
        if (snapshot == null || snapshot.getImage() == null) {
            return null;
        }
        return snapshot;
    }

    /**
     *
     * @param url
     * @return
     */
    private Snapshot createNewSnapshot(String url) {
        Snapshot snapshot = create();
        byte[] rawImage = snapshotCreator.getScreenshot(url);
        Image image = imageDataService.create();

        image.setData(rawImage);
        image = imageDataService.saveOrUpdate(image);

        snapshot.setImage(image);
        snapshot.setDate(Calendar.getInstance().getTime());
        snapshot.setWidth(SNAPSHOT_WIDTH);
        snapshot.setHeight(SNAPSHOT_HEIGHT);
        snapshot.setUrl(url);

        saveOrUpdate(snapshot);
        return snapshot;
    }

    @Override
    public Long getCount() {
        return ((SnapshotDAO) entityDao).count();
    }
}
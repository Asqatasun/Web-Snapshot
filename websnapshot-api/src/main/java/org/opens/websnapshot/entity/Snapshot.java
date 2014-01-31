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
package org.opens.websnapshot.entity;

import java.util.Date;
import org.opens.tanaguru.sdk.entity.Entity;

public interface Snapshot extends Entity {

    /**
     *
     * @return the id of image from Image entity
     */
    Image getImage();

    /**
     *
     * @param image
     */
    void setImage(Image image);

    /**
     *
     * @return the url of the image
     */
    String getUrl();

    /**
     *
     * @param url
     */
    void setUrl(String url);

    /**
     *
     * @return the date of creating snapshot
     */
    Date getDate();

    /**
     *
     * @param date
     */
    void setDate(Date date);

    /**
     *
     * @return the width of the snapshot
     */
    Long getWidth();

    /**
     *
     * @param width
     */
    void setWidth(Long width);

    /**
     *
     * @return the height of the snapshot
     */
    Long getHeight();

    /**
     *
     * @param height
     */
    void setHeight(Long height);
}
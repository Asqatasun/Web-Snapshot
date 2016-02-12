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
package org.asqatasun.websnapshot.entity;

import java.util.Date;
import org.asqatasun.sdk.entity.Entity;

public interface Image extends Entity {

    enum Status {
        
        CREATED("CREATED"),
        IN_PROGRESS("IN_PROGRESS"),
        ERROR("ERROR"),
        QUEUED("QUEUED"),
        HACK_CREATED("HACK_CREATED"),
        MUST_BE_CREATE("MUST_BE_CREATE"),
        NOT_EXIST("NOT_EXIST");

        private final String text;
        /**
         * @param text
         */
        private Status(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    /**
     *
     * @return the date of creating snapshot
     */
    Date getDateOfCreation();

    /**
     *
     * @param date
     */
    void setDateOfCreation(Date date);

    /**
     *
     * @return the width of the image
     */
    int getWidth();

    /**
     *
     * @param width
     */
    void setWidth(int width);

    /**
     *
     * @return the height of the image
     */
    int getHeight();

    /**
     *
     * @param height
     */
    void setHeight(int height);

    /**
     *
     * @return
     */
    byte[] getRawData();

    /**
     *
     * @param thumbnail
     */
    void setRawData(byte[] thumbnail);

    /**
     *
     * @return true if the image is the canonical format
     */
    boolean getIsCanonical();

    /**
     *
     * @param canonical
     */
    void setIsCanonical(boolean canonical);

    /**
     *
     * @return the status : CREATED, IN_PROGRESS, ERROR, QUEUED
     */
    Status getStatus();

    /**
     *
     * @param status
     */
    void setStatus(Status status);

    /**
     *
     * @return the url entity
     */
    Url getUrl();

    /**
     *
     * @param url
     */
    void setUrl(Url url);
}
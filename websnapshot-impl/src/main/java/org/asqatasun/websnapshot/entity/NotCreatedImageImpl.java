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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Implementation that handles the not_created message status
 *
 * @author alingua
 */
public class NotCreatedImageImpl implements Image, Serializable {

    private int width;
    private int height;

    /**
     *
     */
    public NotCreatedImageImpl() {
    }

    @Override
    public Date getDateOfCreation() {
        return Calendar.getInstance().getTime();
    }

    @Override
    public void setDateOfCreation(Date date) {
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public void setRawData(byte[] thumbnail) {
    }

    @Override
    public boolean getIsCanonical() {
        return false;
    }

    @Override
    public void setIsCanonical(boolean canonical) {
    }

    @Override
    public Status getStatus() {
        return Status.NOT_EXIST;
    }

    @Override
    public void setStatus(Status status) {
    }

    @Override
    public Url getUrl() {
        return new Url() {
            @Override
            public String getUrl() {
                return "";
            }

            @Override
            public void setUrl(String url) {
            }

            @Override
            public Long getId() {
                return Long.MIN_VALUE;
            }

            @Override
            public void setId(Long l) {
            }
        };
    }

    @Override
    public void setUrl(Url url) {
    }

    @Override
    public Long getId() {
        return Long.MIN_VALUE;
    }

    @Override
    public void setId(Long l) {
    }
}
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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alingua
 */
@Entity
@Table(name = "image")
@XmlRootElement
public class ImageImpl implements Image, Serializable {

    private static final int HASH_NUMBER = 7;
    private static final int HASH_COEFFICIENT = 59;
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "date_of_creation")
    private Date dateOfCreation;
    @Column(name = "width")
    private int width;
    @Column(name = "height")
    private int height;
    @Lob
    @Column(name = "raw_data")
    private byte[] rawData;
    @Column(name = "is_canonical")
    private boolean isCanonical;
    @Column(name = "status")
    private Status status;
    @ManyToOne
    @JoinColumn(name = "id_url")
    private UrlImpl url;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getDateOfCreation() {
        return (Date) dateOfCreation.clone();
    }

    @Override
    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = (Date) dateOfCreation.clone();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public byte[] getRawData() {
        return rawData.clone();
    }

    @Override
    public void setRawData(byte[] rawData) {
        this.rawData = rawData.clone();
    }

    @Override
    public boolean getIsCanonical() {
        return isCanonical;
    }

    @Override
    public void setIsCanonical(boolean isCanonical) {
        this.isCanonical = isCanonical;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Url getUrl() {
        return url;
    }

    @Override
    public void setUrl(Url url) {
        this.url = (UrlImpl) url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImageImpl other = (ImageImpl) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (this.isCanonical != other.isCanonical) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = HASH_NUMBER;
        hash = HASH_COEFFICIENT * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = HASH_COEFFICIENT * hash + this.width;
        hash = HASH_COEFFICIENT * hash + this.height;
        hash = HASH_COEFFICIENT * hash + (this.isCanonical ? 1 : 0);
        return hash;
    }
}
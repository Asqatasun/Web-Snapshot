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

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alingua
 */
@Entity
@Table(name = "thumbnail")
@XmlRootElement
public class ThumbnailImpl implements Thumbnail, Serializable {

    private static final int HASH_NUMBER = 7;
    private static final int HASH_COEFFICIENT = 61;
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_image")
    private ImageImpl image;
    @OneToOne
    @JoinColumn(name = "id_snapshot")
    private SnapshotImpl snapshot;
    @Column(name = "width")
    private int width;
    @Column(name = "height")
    private int height;

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
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void setImage(Image image) {
        this.image = (ImageImpl) image;
    }

    @Override
    public Snapshot getSnapshot() {
        return snapshot;
    }

    @Override
    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = (SnapshotImpl) snapshot;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ThumbnailImpl other = (ThumbnailImpl) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = HASH_NUMBER;
        hash = HASH_COEFFICIENT * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
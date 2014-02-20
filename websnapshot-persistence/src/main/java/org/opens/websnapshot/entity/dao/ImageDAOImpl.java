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
package org.opens.websnapshot.entity.dao;

import java.util.Date;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.opens.tanaguru.sdk.entity.dao.jpa.AbstractJPADAO;
import org.opens.websnapshot.entity.Image;
import org.opens.websnapshot.entity.Image.Status;
import org.opens.websnapshot.entity.ImageImpl;

public class ImageDAOImpl extends AbstractJPADAO<Image, Long>
        implements ImageDAO {

    public ImageDAOImpl() {
        super();
    }

    @Override
    protected Class<? extends Image> getEntityClass() {
        return ImageImpl.class;
    }

    @Override
    public Image findCanonicalImageByUrl(String url) {
        Query query = entityManager.createQuery(
                "SELECT i FROM " + getEntityClass().getName() + " as i "
                + "LEFT JOIN FETCH i.url as u "
                + "WHERE i.isCanonical = true "
                + "AND u.url like :url "
                + "ORDER BY i.dateOfCreation DESC");
        query.setParameter("url", url);
        query.setMaxResults(1);
        return (Image) query.getSingleResult();
    }

    @Override
    public Image findImageByWidthAndHeightAndUrl(int width, int height, String url) {
        Query query = entityManager.createQuery(
                "SELECT i from " + getEntityClass().getName() + " as i "
                + "LEFT JOIN FETCH i.url as u "
                + "WHERE i.width = :width "
                + "AND i.height = :height "
                + "AND u.url like :URL "
                + "ORDER BY i.dateOfCreation DESC");
        query.setParameter("width", width);
        query.setParameter("height", height);
        query.setParameter("URL", url);
        query.setMaxResults(1);
        return (Image) query.getSingleResult();
    }

    @Override
    public Object findImageFromDateAndUrlAndWidthAndHeight(String url, Date date, int width, int height) {
        Image nextImage = findNextImageFromDate(url, date, width, height);

        if (nextImage == null) {
            Image snapshot = findNextCanonicalImageFromDate(url, date, width, height);
            if (snapshot == null) {
                return Status.NOT_EXIST;
            }
            if (snapshot.getStatus().equals(Status.IN_PROGRESS)) {
                return snapshot;
            }
            return Status.MUST_BE_CREATE;
        }
        return nextImage;
    }

    /**
     *
     * @param url
     * @param date
     * @param width
     * @param height
     * @return la thumbnail la plus proche suivant la date passée en paramètre
     */
    private Image findNextImageFromDate(String url, Date date, int width, int height) {
        Query query = entityManager.createQuery(
                "SELECT i FROM " + getEntityClass().getName() + " AS i "
                + "LEFT JOIN FETCH i.url as u "
                + "WHERE u.url like :url "
                + "AND i.width = :width "
                + "AND i.height = :height "
                + "AND i.isCanonical = false "
                + "AND i.dateOfCreation >= :date "
                + "ORDER BY i.dateOfCreation ASC");
        query.setParameter("width", width);
        query.setParameter("height", height);
        query.setParameter("url", url);
        query.setParameter("date", date);
        query.setMaxResults(1);
        try {
            return (Image) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     *
     * @param url
     * @param date
     * @param width
     * @param height
     * @return
     */
    private Image findNextCanonicalImageFromDate(String url, Date date, int width, int height) {
        Query query = entityManager.createQuery(
                "SELECT i from " + getEntityClass().getName() + " as i "
                + "LEFT JOIN FETCH i.url as u "
                + "WHERE i.width = :width "
                + "AND i.height = :height "
                + "AND i.isCanonical = true "
                + "AND i.dateOfCreation >= :date "
                + "AND u.url like :URL "
                + "ORDER BY i.dateOfCreation ASC");
        query.setParameter("width", width);
        query.setParameter("height", height);
        query.setParameter("URL", url);
        query.setParameter("date", date);
        query.setMaxResults(1);
        try {
            return (Image) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public Long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(i.id) FROM " + getEntityClass().getName() + " AS i", Long.class);
        return query.getSingleResult();
    }
}
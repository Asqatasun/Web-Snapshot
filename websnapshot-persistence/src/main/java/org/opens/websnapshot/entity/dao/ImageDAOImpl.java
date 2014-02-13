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
                "SELECT i FROM " + getEntityClass().getName() + " as i "
                + "LEFT JOIN FETCH i.url as u "
                + "WHERE i.width = :width "
                + "AND i.height = :height "
                + "AND u.url like :url "
                + "ORDER BY i.dateOfCreation DESC");
        query.setParameter("width", width);
        query.setParameter("height", height);
        query.setParameter("url", url);
        query.setMaxResults(1);
        return (Image) query.getSingleResult();
    }

    @Override
    public Image findImageFromDateAndUrlAndWidthAndHeight(String url, Date date, int width, int height) {
        /* 
         * JPQL ne permettant pas de recuperer la date la plus proche de la date voulu,
         * nous effectuons deux requetes :
         * - La premiere recupere la thumbnail la plus proche precedant la date passée en parametre
         * - La seconde recupere la thumbnail la plus proche suivant la date passée en parametre
         * La date la plus proche est ensuite determinée coté objet.
         */
        return checkValidImageAndReturnTheClotest(url, date, width, height);
    }

    /**
     *
     * @param url
     * @param date
     * @param width
     * @param height
     * @return null si les requetes n'aboutissent a aucun resultat, la thumbnail
     * la thumbnail plus recente s'il n'y a pas de thumbnail ancienne, la
     * thumbnail plus ancienne s'il n'y a pas de thumbnail recente, la thumbnail
     * la plus proche de la date passée en parametre si chaque requetes ont
     * retourné une thumbnail.
     */
    private Image checkValidImageAndReturnTheClotest(String url, Date date, int width, int height) {
        Image beforeImage = findBeforeImageFromDate(url, date, width, height);
        Image nextImage = findNextImageFromDate(url, date, width, height);

        if (beforeImage == null) {
            if (nextImage == null) {
                return null;
            } else {
                return nextImage;
            }
        } else {
            if (nextImage == null) {
                return beforeImage;
            } else {
                return closestThumbnailFromDate(beforeImage, nextImage, date);
            }
        }
    }

    /**
     * @see findImageFromDateAndUrlAndWidthAndHeight
     * @param url
     * @param date
     * @param width
     * @param height
     * @return la thumbnail la plus proche précèdent la date passée en paramètre
     */
    private Image findBeforeImageFromDate(String url, Date date, int width, int height) {
        return findImageFromDate(url, date, width, height, true);
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
        return findImageFromDate(url, date, width, height, false);
    }

    private Image findImageFromDate(String url, Date date, int width, int height, boolean previous) {
        StringBuilder strb = new StringBuilder();

        strb.append("SELECT i FROM ");
        strb.append(getEntityClass().getName());
        strb.append(" AS i ");
        strb.append("LEFT JOIN FETCH i.url u ");
        strb.append("WHERE u.url like :url ");
        strb.append("AND i.width = :width ");
        strb.append("AND i.height = :height ");
        strb.append("AND i.dateOfCreation ");
        if (previous) {
            strb.append("<");
        } else {
            strb.append(">");
        }
        strb.append(" :date ");
        strb.append("ORDER BY i.dateOfCreation ");
        if (previous) {
            strb.append("DESC");
        } else {
            strb.append("ASC");
        }

        Query query = entityManager.createQuery(strb.toString());
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
     * @param beforeImage
     * @param nextImage
     * @param targetDate
     * @return the closest Thumbnail from the targetDate
     */
    private Image closestThumbnailFromDate(Image beforeImage, Image nextImage, Date targetDate) {
        Long beforeDate = beforeImage.getDateOfCreation().getTime();
        Long nextDate = nextImage.getDateOfCreation().getTime();
        Long diffBeforeDateAndTargetDate = targetDate.getTime() - beforeDate;
        Long diffNextDateAndTargetDate = nextDate - targetDate.getTime();

        return diffBeforeDateAndTargetDate > diffNextDateAndTargetDate ? nextImage : beforeImage;
    }

    @Override
    public Long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(i.id) FROM " + getEntityClass().getName() + " AS i", Long.class);
        return query.getSingleResult();
    }
}
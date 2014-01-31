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
import org.opens.tanaguru.sdk.entity.dao.jpa.AbstractJPADAO;
import org.opens.websnapshot.entity.Thumbnail;
import org.opens.websnapshot.entity.ThumbnailImpl;


public class ThumbnailDAOImpl extends AbstractJPADAO<Thumbnail, Long>
        implements ThumbnailDAO {

    public ThumbnailDAOImpl() {
        super();
    }

    @Override
    protected Class<? extends Thumbnail> getEntityClass() {
        return ThumbnailImpl.class;
    }

    @Override
    public Thumbnail findThumbnailFromUrlAndWidthAndHeight(String url, int width, int height) {
        Query query = entityManager.createQuery(
                "SELECT t FROM " + getEntityClass().getName() + " as t "
                + "LEFT JOIN t.snapshot as s "
                + "LEFT JOIN FETCH t.image as ti "
                + "WHERE t.width = :width "
                + "AND t.height = :height "
                + "AND s.url like :url "
                + "ORDER BY s.dateOfCreation DESC");
        query.setParameter("width", width);
        query.setParameter("height", height);
        query.setParameter("url", url);
        query.setMaxResults(1);
        try {
            return (Thumbnail) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public Thumbnail findThumbnailFromDateAndUrlAndWidthAndHeight(String url, Date date, int width, int height) {
        /* 
         * JPQL ne permettant pas de recuperer la date la plus proche de la date voulu,
         * nous effectuons deux requetes :
         * - La premiere recupere la thumbnail la plus proche precedant la date passée en parametre
         * - La seconde recupere la thumbnail la plus proche suivant la date passée en parametre
         * La date la plus proche est ensuite determinée coté objet.
         */
        return checkValidThumbnailAndReturnTheClotest(url, date, width, height);
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
    private Thumbnail checkValidThumbnailAndReturnTheClotest(String url, Date date, int width, int height) {
        Thumbnail beforeThumbnail = findBeforeThumbnailFromDate(url, date, width, height);
        Thumbnail nextThumbnail = findNextThumbnailFromDate(url, date, width, height);

        if (beforeThumbnail == null) {
            if (nextThumbnail == null) {
                return null;
            } else {
                return nextThumbnail;
            }
        } else {
            if (nextThumbnail == null) {
                return beforeThumbnail;
            } else {
                return closestThumbnailFromDate(beforeThumbnail, nextThumbnail, date);
            }
        }
    }

    /**
     * @see findThumbnailFromDateAndUrlAndWidthAndHeight
     * @param url
     * @param date
     * @param width
     * @param height
     * @return la thumbnail la plus proche précèdent la date passée en paramètre
     */
    private Thumbnail findBeforeThumbnailFromDate(String url, Date date, int width, int height) {
        return findThumbnailFromDate(url, date, width, height, true);
//        Query query = entityManager.createQuery(
//                "SELECT t FROM " + getEntityClass().getName() + " as t "
//                + "LEFT JOIN t.snapshot as s "
//                + "WHERE s.url like :url "
//                + "AND t.width = :width "
//                + "AND t.height = :height "
//                + "AND s.dateOfCreation < :date "
//                + "ORDER BY s.dateOfCreation DESC");
//        query.setParameter("width", width);
//        query.setParameter("height", height);
//        query.setParameter("url", url);
//        query.setParameter("date", date);
//        query.setMaxResults(1);
//        try {
//            return (Thumbnail) query.getSingleResult();
//        } catch (NoResultException nre) {
//            return null;
//        }
    }

    /**
     *
     * @param url
     * @param date
     * @param width
     * @param height
     * @return la thumbnail la plus proche suivant la date passée en paramètre
     */
    private Thumbnail findNextThumbnailFromDate(String url, Date date, int width, int height) {
        return findThumbnailFromDate(url, date, width, height, false);
//        Query query = entityManager.createQuery(
//                "SELECT t FROM " + getEntityClass().getName() + " as t "
//                + "LEFT JOIN t.snapshot as s "
//                + "WHERE s.url like :url "
//                + "AND t.width = :width "
//                + "AND t.height = :height "
//                + "AND s.dateOfCreation > :date "
//                + "ORDER BY s.dateOfCreation ASC");
//        query.setParameter("width", width);
//        query.setParameter("height", height);
//        query.setParameter("url", url);
//        query.setParameter("date", date);
//        query.setMaxResults(1);
//        try {
//            return (Thumbnail) query.getSingleResult();
//        } catch (NoResultException nre) {
//            return findThumbnailFromDate(url, date, width, height, false);
//        }
    }

    private Thumbnail findThumbnailFromDate(String url, Date date, int width, int height, boolean previous) {
        StringBuilder strb = new StringBuilder();

        strb.append("SELECT t FROM ");
        strb.append(getEntityClass().getName());
        strb.append(" as t ");
        strb.append("LEFT JOIN t.snapshot as s ");
        strb.append("WHERE s.url like :url ");
        strb.append("AND t.width = :width ");
        strb.append("AND t.height = :height ");
        strb.append("AND s.dateOfCreation ");
        if (previous) {
            strb.append("<");
        } else {
            strb.append(">");
        }
        strb.append(" :date ");
        strb.append("ORDER BY s.dateOfCreation ");
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
            return (Thumbnail) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     *
     * @param beforeThumbnail
     * @param nextThumbnail
     * @param targetDate
     * @return the closest Thumbnail from the targetDate
     */
    private Thumbnail closestThumbnailFromDate(Thumbnail beforeThumbnail, Thumbnail nextThumbnail, Date targetDate) {
        Long beforeDate = beforeThumbnail.getSnapshot().getDate().getTime();
        Long nextDate = nextThumbnail.getSnapshot().getDate().getTime();
        Long diffBeforeDateAndTargetDate = targetDate.getTime() - beforeDate;
        Long diffNextDateAndTargetDate = nextDate - targetDate.getTime();

        return diffBeforeDateAndTargetDate > diffNextDateAndTargetDate ? nextThumbnail : beforeThumbnail;
    }

    @Override
    public Long count() {
        Query query = entityManager.createQuery(
                "SELECT COUNT(t.id) FROM " + getEntityClass().getName() + " AS t");
        return (Long) query.getSingleResult();
    }
}
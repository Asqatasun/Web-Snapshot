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
import org.opens.websnapshot.entity.Snapshot;
import org.opens.websnapshot.entity.SnapshotImpl;


public class SnapshotDAOImpl extends AbstractJPADAO<Snapshot, Long>
        implements SnapshotDAO {

    public SnapshotDAOImpl() {
        super();
    }

    @Override
    protected Class<? extends Snapshot> getEntityClass() {
        return SnapshotImpl.class;
    }

    @Override
    public Snapshot findSnapshotByUrl(String url) {
        Query query = entityManager.createQuery(
                "SELECT s FROM " + getEntityClass().getName() + " AS s "
                + "LEFT JOIN FETCH s.image AS i "
                + "WHERE s.url = :url "
                + "ORDER BY s.dateOfCreation DESC");
        query.setParameter("url", url);
        query.setMaxResults(1);
        try {
            return (Snapshot) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public Snapshot findSnapshotByUrlAndDate(String url, Date date) {
        Query query = entityManager.createQuery(
                "SELECT s FROM " + getEntityClass().getName() + " AS s "
                + "LEFT JOIN FETCH s.image AS i "
                + "WHERE s.url = :url "
                + "ORDER BY ABS(DATEDIFF(s.dateOfCreation, :date )) ASC");
        query.setParameter("url", url);
        query.setParameter("date", date);
        query.setMaxResults(1);
        try {
            return (Snapshot) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Snapshot findSnapshotByUrlAndTokenAndIdentifier(String url, String token, String identifier) {
        Query query = entityManager.createQuery(
                "SELECT s FROM " + getEntityClass().getName() + " AS s "
                + "LEFT JOIN FETCH s.image AS i "
                + "WHERE s.url = :url "
                + "AND s.token = :token "
                + "AND s.identifier = :identifier "
                + "ORDER BY s.dateOfCreation DESC");
        query.setParameter("url", url);
        query.setParameter("token", token);
        query.setParameter("identifier", identifier);
        query.setMaxResults(1);
        try {
            return (Snapshot) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public Long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s.id) FROM " + getEntityClass().getName() + " AS s", Long.class);
        return query.getSingleResult();
    }
}
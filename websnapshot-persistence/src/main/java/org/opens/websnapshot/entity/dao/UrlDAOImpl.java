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

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.opens.tanaguru.sdk.entity.dao.jpa.AbstractJPADAO;
import org.opens.websnapshot.entity.Url;
import org.opens.websnapshot.entity.UrlImpl;

public class UrlDAOImpl extends AbstractJPADAO<Url, Long>
        implements UrlDAO {

    public UrlDAOImpl() {
        super();
    }

    @Override
    protected Class<? extends Url> getEntityClass() {
        return UrlImpl.class;
    }

    @Override
    public Url findUrlByStringUrl(String url) {
        Query query = entityManager.createQuery(
                "SELECT u FROM " + getEntityClass().getName() + " AS u "
                + "WHERE u.url LIKE :url");
        query.setParameter("url", url);
        query.setMaxResults(1);
        return (Url) query.getSingleResult();
    }

    @Override
    public Long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u.id) FROM " + getEntityClass().getName() + " AS u", Long.class);
        return query.getSingleResult();
    }
}
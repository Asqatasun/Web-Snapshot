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

import javax.persistence.NoResultException;
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
    public Image findImageById(int id) {
        TypedQuery<Image> query = entityManager.createQuery(
                "SELECT i FROM " + getEntityClass().getName()
                + " AS i WHERE i.id = :id", Image.class);
        query.setParameter("id", Long.valueOf(id));
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
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
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
import org.opens.tanaguru.sdk.entity.dao.GenericDAO;
import org.opens.websnapshot.entity.Thumbnail;

/**
 *
 * @author alingua
 */
public interface ThumbnailDAO extends GenericDAO<Thumbnail, Long> {

    /**
     *
     * @param url
     * @param date
     * @param width
     * @param height
     * @return
     */
    Thumbnail findThumbnailFromUrlAndWidthAndHeight(String url, int width, int height);
    
    /**
     * 
     * @param url
     * @param date
     * @param width
     * @param height
     * @return 
     */
    Thumbnail findThumbnailFromDateAndUrlAndWidthAndHeight(String url, Date date, int width, int height);

    /**
     *
     * @return the number of Thumbnail in database
     */
    Long count();
}
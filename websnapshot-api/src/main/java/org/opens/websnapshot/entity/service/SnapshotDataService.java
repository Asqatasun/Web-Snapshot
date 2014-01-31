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
package org.opens.websnapshot.entity.service;

import java.util.Date;
import org.opens.tanaguru.sdk.entity.service.GenericDataService;
import org.opens.websnapshot.entity.Snapshot;

/**
 *
 * @author alingua
 */
public interface SnapshotDataService extends GenericDataService<Snapshot, Long> {

    /**
     *
     * @param url
     * @return
     */
    Snapshot getSnapshotFromUrl(String url, boolean forceRecreate);

    /**
     *
     * @param url
     * @param date
     * @return
     */
    Snapshot getSnapshotFromUrlandDate(String url, Date date);

    /**
     * 
     * @param url
     * @param token
     * @param identifier
     * @return 
     */
    Snapshot getSnapshotFromScenario(String url, String token, String identifier);
    
    /**
     *
     * @return
     */
    Long getCount();
}
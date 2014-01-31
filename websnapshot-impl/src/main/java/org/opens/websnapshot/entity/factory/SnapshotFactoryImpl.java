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
package org.opens.websnapshot.entity.factory;

import java.util.Calendar;
import org.opens.websnapshot.entity.Snapshot;
import org.opens.websnapshot.entity.SnapshotImpl;

public class SnapshotFactoryImpl implements SnapshotFactory {

    public SnapshotFactoryImpl() {
        super();
    }

    @Override
    public Snapshot create() {
        return new SnapshotImpl();
    }

    @Override
    public Snapshot create(String url) {
        Snapshot snapshot = create();
        snapshot.setUrl(url);
        snapshot.setDateOfCreation(Calendar.getInstance().getTime());
        return snapshot;
    }
}
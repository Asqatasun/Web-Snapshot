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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import static junit.framework.Assert.assertEquals;
import org.apache.log4j.Logger;
import org.opens.websnapshot.entity.Snapshot;
import org.opens.websnapshot.entity.SnapshotImpl;
import org.opens.websnapshot.utils.AbstractDaoTestCase;


/**
 *
 * @author alingua
 */
public class SnapshotDAOImplTest extends AbstractDaoTestCase {

    public SnapshotDAOImplTest(String testName) {
        super(testName, "src/test/resources/datasets/dataset.xml");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SnapshotDAO getBean() {
        return (SnapshotDAO) springBeanFactory.getBean("snapshotDAO");
    }

    public void testGetEntityClass() {
        System.out.println("getEntityClass");
        SnapshotDAOImpl instance = new SnapshotDAOImpl();
        Class expResult = SnapshotImpl.class;
        Class result = instance.getEntityClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of findByUrl method, of class ThumbnailDAOImpl.
     */
    public void testCreateEntry() {
        /* nominal use case */
        System.out.println("Test createEntry");
        /* set-up instance */
        SnapshotDAO instance = getBean();
        Collection<Snapshot> snapshots = instance.findAll();
        assertEquals(4, snapshots.size());

        Snapshot snapshot = new SnapshotImpl();
        snapshot.setWidth(1024L);
        snapshot.setHeight(768L);
        /* run test */
        instance.create(snapshot);

        snapshots = instance.findAll();
        assertEquals(5, snapshots.size());
    }

    /**
     * Test of findSnapshotByUrl method, of class SnapshotDAOImpl.
     */
    public void testFindSnapshotByUrl() {
        System.out.println("findSnapshotByUrl");
        SnapshotDAO instance = getBean();

        Snapshot snapshot = instance.findSnapshotByUrl("http://www.epitech.eu/");
        assertEquals(Long.valueOf(3), snapshot.getId());

        snapshot = instance.findSnapshotByUrl("http://google.fr/");
        assertEquals(Long.valueOf(1), snapshot.getId());
    }

    /**
     * Test of findSnapshotByUrlAndDate method, of class SnapshotDAOImpl.
     */
    public void testFindSnapshotByUrlAndDate() {
        System.out.println("findSnapshotByUrlAndDate");
        SnapshotDAO instance = getBean();

        Calendar cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 15, 23, 59, 0);
        Date date = cl.getTime();
        Snapshot returnedThumbnail1 = instance.findSnapshotByUrlAndDate("http://www.epitech.eu/", date);
        assertEquals(Long.valueOf(4), returnedThumbnail1.getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getDateOfCreation());
        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 16, 00, 01, 0);
        date = cl.getTime();
        returnedThumbnail1 = instance.findSnapshotByUrlAndDate("http://www.epitech.eu/", date);
        assertEquals(Long.valueOf(3), returnedThumbnail1.getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getDateOfCreation());
        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 16, 00, 00, 0);
        date = cl.getTime();
        returnedThumbnail1 = instance.findSnapshotByUrlAndDate("http://www.epitech.eu/", date);
        assertEquals(Long.valueOf(3), returnedThumbnail1.getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getDateOfCreation());
        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 15, 23, 59, 59);
        date = cl.getTime();
        returnedThumbnail1 = instance.findSnapshotByUrlAndDate("http://www.epitech.eu/", date);
        assertEquals(Long.valueOf(4), returnedThumbnail1.getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getDateOfCreation());
    }
}

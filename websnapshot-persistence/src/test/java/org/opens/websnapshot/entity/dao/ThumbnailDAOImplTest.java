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
import static junit.framework.Assert.assertNotNull;
import org.apache.log4j.Logger;
import org.opens.websnapshot.entity.Thumbnail;
import org.opens.websnapshot.entity.ThumbnailImpl;
import org.opens.websnapshot.utils.AbstractDaoTestCase;

/**
 *
 * @author alingua
 */
public class ThumbnailDAOImplTest extends AbstractDaoTestCase {

    public ThumbnailDAOImplTest(String testName) {
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

    private ThumbnailDAO getBean() {
        return (ThumbnailDAO) springBeanFactory.getBean("thumbnailDAO");
    }

    public void testGetEntityClass() {
        System.out.println("getEntityClass");
        ThumbnailDAOImpl instance = new ThumbnailDAOImpl();
        Class expResult = ThumbnailImpl.class;
        Class result = instance.getEntityClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of findByUrl method, of class ThumbnailDAOImpl.
     */
    public void testDeleteEntry() {
        /* nominal use case */
        System.out.println("Test deleteEntry");
        /* set-up instance */
        ThumbnailDAO instance = getBean();

        Collection<Thumbnail> thumnails = instance.findAll();
        assertEquals(4, thumnails.size());
        /* run test */
        instance.delete(Long.valueOf(1));
        thumnails = instance.findAll();
        assertEquals(3, thumnails.size());
    }

    /**
     * Test of findByUrl method, of class ThumbnailDAOImpl.
     */
    public void testCreateEntry() {
        /* nominal use case */
        System.out.println("Test createEntry");
        /* set-up instance */
        ThumbnailDAO instance = getBean();
        Collection<Thumbnail> thumbnails = instance.findAll();
        assertEquals(4, thumbnails.size());

        Thumbnail thumbnail = new ThumbnailImpl();
        thumbnail.setWidth(500);
        thumbnail.setHeight(500);
        /* run test */
        instance.create(thumbnail);

        thumbnails = instance.findAll();
        assertEquals(5, thumbnails.size());
    }

    /**
     * Test of findThumbnailFromUrlAndWidthAndHeight method, of class
     * ThumbnailDAOImpl
     */
    public void testFindThumbnailFromUrlAndWidthAndHeight() {
        System.out.println("Test findThumbnailFromUrlAndWidthAndHeight");

        ThumbnailDAO instance = getBean();

        Thumbnail returnedThumbnail1 = instance.findThumbnailFromUrlAndWidthAndHeight("http://www.epitech.eu/", 1024, 768);
        assertNotNull(returnedThumbnail1);

        Thumbnail returnedThumbnail2 = instance.read(Long.valueOf(3));
        assertNotNull(returnedThumbnail2);

        assertEquals(returnedThumbnail1, returnedThumbnail2);
    }

    /**
     * Test of findThumbnailFromDateAndUrlAndWidthAndHeight method, of class
     * ThumbnailDAOImpl.
     */
    public void testFindThumbnailFromDateAndUrlAndWidthAndHeight() {
        /* nominal use case */
        System.out.println("Test findThumbnailFromDateAndUrlAndWidthAndHeight");
        /* set-up instance */
        ThumbnailDAO instance = getBean();
        Calendar cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 15, 23, 59, 0);
        Date date = cl.getTime();
        Thumbnail returnedThumbnail1 = instance.findThumbnailFromDateAndUrlAndWidthAndHeight("http://www.epitech.eu/", date, 1024, 768);
        assertEquals(Long.valueOf(4), returnedThumbnail1.getSnapshot().getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getSnapshot().getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getSnapshot().getDate());
        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 16, 00, 01, 0);
        date = cl.getTime();
        returnedThumbnail1 = instance.findThumbnailFromDateAndUrlAndWidthAndHeight("http://www.epitech.eu/", date, 1024, 768);
        assertEquals(Long.valueOf(3), returnedThumbnail1.getSnapshot().getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getSnapshot().getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getSnapshot().getDate());
        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 16, 00, 00, 0);
        date = cl.getTime();
        returnedThumbnail1 = instance.findThumbnailFromDateAndUrlAndWidthAndHeight("http://www.epitech.eu/", date, 1024, 768);
        assertEquals(Long.valueOf(3), returnedThumbnail1.getSnapshot().getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getSnapshot().getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getSnapshot().getDate());
        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 15, 23, 59, 59);
        date = cl.getTime();
        returnedThumbnail1 = instance.findThumbnailFromDateAndUrlAndWidthAndHeight("http://www.epitech.eu/", date, 1024, 768);
        assertEquals(Long.valueOf(4), returnedThumbnail1.getSnapshot().getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getSnapshot().getId());
        Logger.getLogger(this.getClass()).debug(returnedThumbnail1.getSnapshot().getDate());
    }

    /**
     * Test of count method, of class ThumbnailDAOImpl
     */
    public void testCount() {
        /* */
        System.out.println("count");
        /* */
        ThumbnailDAO instance = getBean();
        /* */
        Long expectedResult = 4L;
        /* */
        Long result = instance.count();
        /* */
        assertEquals(expectedResult, result);
    }
}

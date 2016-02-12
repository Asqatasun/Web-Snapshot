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
package org.asqatasun.websnapshot.entity.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import static junit.framework.Assert.assertEquals;
import org.asqatasun.websnapshot.entity.Image;
import org.asqatasun.websnapshot.entity.ImageImpl;
import org.asqatasun.websnapshot.utils.AbstractDaoTestCase;

/**
 *
 * @author alingua
 */
public class ImageDAOImplTest extends AbstractDaoTestCase {

    public ImageDAOImplTest(String testName) {
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

    private ImageDAO getBean() {
        return (ImageDAO) springBeanFactory.getBean("imageDAO");
    }

    public void testGetEntityClass() {
        System.out.println("getEntityClass");
        ImageDAOImpl instance = new ImageDAOImpl();
        Class expResult = ImageImpl.class;
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
        ImageDAO instance = getBean();
        Collection<Image> images = instance.findAll();
        assertEquals(13, images.size());

        Image image = new ImageImpl();
        /* run test */
        instance.create(image);

        images = instance.findAll();
        assertEquals(14, images.size());
    }

    /**
     * Test of findImageById method, of class ImageDAOImpl.
     */
    public void testFindImageByWidthAndHeightAndUrl() {
        System.out.println("Test findImageByWidthAndHeightAndUrl");
        ImageDAO instance = getBean();

        Image image = instance.findImageByWidthAndHeightAndUrl(270, 170, "www.google.fr");
        assertEquals(Long.valueOf(5), image.getId());
        image = instance.findImageByWidthAndHeightAndUrl(270, 170, "www.epitech.eu");
        assertEquals(Long.valueOf(6), image.getId());
        image = instance.findImageByWidthAndHeightAndUrl(270, 170, "www.facebook.com");
        assertEquals(Long.valueOf(7), image.getId());
        image = instance.findImageByWidthAndHeightAndUrl(270, 170, "www.asqatasun.org");
        assertEquals(Long.valueOf(8), image.getId());

    }

    /**
     * Test of findCanonicalImageByUrl method, of class ImageDAOImpl.
     */
    public void testFindCanonicalImageByUrl() {
        System.out.println("Test findCanonicalImageByUrl");
        ImageDAO instance = getBean();

        Image image = instance.findCanonicalImageByUrl("www.google.fr");
        assertEquals(Long.valueOf(1), image.getId());
    }

    public void testFindImageByWidthAndHeightAndUrlAndDate() {
        System.out.println("Test findImageByWidthAndHeightAndUrlAndDate");
        ImageDAO instance = getBean();

        Calendar cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 15, 23, 59, 0);
        Date date = cl.getTime();
        Object object = instance.findImageFromDateAndUrlAndWidthAndHeight("www.epitech.eu", date, 1024, 768);
        Image returnedImage = (Image) object;
        assertEquals(Long.valueOf(9), returnedImage.getId());

        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 16, 00, 01, 30);
        date = cl.getTime();
        object = instance.findImageFromDateAndUrlAndWidthAndHeight("www.epitech.eu", date, 1024, 768);
        returnedImage = (Image) object;
        assertEquals(Long.valueOf(12), returnedImage.getId());

        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 16, 00, 00, 0);
        date = cl.getTime();
        object = instance.findImageFromDateAndUrlAndWidthAndHeight("www.epitech.eu", date, 1024, 768);
        returnedImage = (Image) object;
        assertEquals(Long.valueOf(11), returnedImage.getId());

        cl = Calendar.getInstance();
        cl.set(2013, Calendar.AUGUST, 15, 23, 59, 59);
        date = cl.getTime();
        object = instance.findImageFromDateAndUrlAndWidthAndHeight("www.epitech.eu", date, 1024, 768);
        returnedImage = (Image) object;
        assertEquals(Long.valueOf(11), returnedImage.getId());
    }
}

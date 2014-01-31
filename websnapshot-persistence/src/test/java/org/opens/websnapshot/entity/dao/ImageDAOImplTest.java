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

import java.util.Collection;
import static junit.framework.Assert.assertEquals;
import org.opens.websnapshot.entity.Image;
import org.opens.websnapshot.entity.ImageImpl;
import org.opens.websnapshot.utils.AbstractDaoTestCase;

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
        assertEquals(8, images.size());

        Image image = new ImageImpl();
        /* run test */
        instance.create(image);

        images = instance.findAll();
        assertEquals(9, images.size());
    }

    /**
     * Test of findImageById method, of class ImageDAOImpl.
     */
    public void testFindImageById() {
        System.out.println("findImageById");
        ImageDAO instance = getBean();

        Image image = instance.findImageById(Integer.valueOf(1).intValue());
        assertEquals(Long.valueOf(1), image.getId());

        image = instance.findImageById(Integer.valueOf(2).intValue());
        assertEquals(Long.valueOf(2), image.getId());
    }
}

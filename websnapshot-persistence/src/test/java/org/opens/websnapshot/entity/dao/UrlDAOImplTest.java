/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opens.websnapshot.entity.dao;

import static junit.framework.Assert.assertEquals;
import org.opens.websnapshot.entity.Url;
import org.opens.websnapshot.entity.UrlImpl;
import org.opens.websnapshot.utils.AbstractDaoTestCase;

/**
 *
 * @author alingua
 */
public class UrlDAOImplTest extends AbstractDaoTestCase {

    public UrlDAOImplTest(String testName) {
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

    private UrlDAO getBean() {
        return (UrlDAO) springBeanFactory.getBean("urlDAO");
    }

    public void testGetEntityClass() {
        System.out.println("getEntityClass");
        UrlDAOImpl instance = new UrlDAOImpl();
        Class expResult = UrlImpl.class;
        Class result = instance.getEntityClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of findUrlByStringUrl method, of class UrlDAOImpl.
     */
    public void testFindUrlByStringUrl() {
        System.out.println("findUrlByStringUrl");
        String url = "www.google.fr";
        UrlDAO instance = getBean();
        Url result = instance.findUrlByStringUrl(url);
        assertEquals("www.google.fr", result.getUrl());
    }
}

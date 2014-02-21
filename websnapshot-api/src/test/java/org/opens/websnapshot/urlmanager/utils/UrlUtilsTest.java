/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opens.websnapshot.urlmanager.utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 *
 * @author alingua
 */
public class UrlUtilsTest extends TestCase {
    
    public UrlUtilsTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of checkIfURLIsValid method, of class UrlUtils.
     */
    public void testCheckIfURLIsValid() throws URISyntaxException {
        System.out.println("checkIfURLIsValid");
        String url = "http://www.amazon.fr/T%C3%A9l%C3%A9phones-portables-d%C3%A9bloqu%C3%A9s-T%C3%A9l%C3%A9phonie-PDA/b?ie=UTF8%26node=218193031";
        Logger.getLogger(this.getClass()).debug("URL = " + url);
        boolean expResult = true;
        boolean result = UrlUtils.checkIfURLIsValid(url);
        assertEquals(expResult, result);
        url = "http://www.amazon.fr/Téléphones-portables-débloqués-Téléphonie-PDA";
        try {
            URL u = new URL(url);
            System.out.println(u.toURI().toASCIIString());
            System.out.println(url);
        //        System.out.println(u.);
            expResult = true;
            result = UrlUtils.checkIfURLIsValid(u.toURI().toASCIIString());
//            result = UrlUtils.checkIfURLIsValid(url);
            assertEquals(expResult, result);
        } catch (MalformedURLException ex) {
            
            System.out.println("MalformedURLException");
        }
    }
}

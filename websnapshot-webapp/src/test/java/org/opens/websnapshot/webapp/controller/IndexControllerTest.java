/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opens.websnapshot.webapp.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author alingua
 */
public class IndexControllerTest {

    public IndexControllerTest() {
    }

    public void setUp() {
    }

    public void tearDown() {
    }

    /**
     * Test of convertStringDateToDate method, of class IndexController.
     */
    public void testConvertStringUrlToDate() {
        System.out.println("convertStringUrlToDate");
        String date = "2014-01-23T17:20:00";
        IndexController instance = new IndexController();
        Date result = instance.convertStringDateToDate(date);
        Logger.getLogger(this.getClass()).debug("Convert Date = " + result);
    }
}
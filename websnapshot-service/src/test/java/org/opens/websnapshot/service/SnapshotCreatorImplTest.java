/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opens.websnapshot.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 *
 * @author alingua
 */
public class SnapshotCreatorImplTest extends TestCase {

    public SnapshotCreatorImplTest(String testName) {
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

    public void testGetThumbnail() {
        System.out.println("getThumbnail");
        SnapshotCreator instance = new SnapshotCreatorImpl();
        String csvFileToRead = "/home/alingua/Documents/Sources/Web-snapshot/websnapshot-webapp/src/test/resources/LIST-online-all-sites.txt";
        BufferedReader br = null;
        String line = "";
        String[] url = null;
        int i = 0;
        try {
            br = new BufferedReader(new FileReader(csvFileToRead));
            while ((line = br.readLine()) != null && i < 2) {
                url = line.split(";");
                Logger.getLogger(this.getClass()).debug("Url = " + url[1]);
                instance.requestSnapshotCreation(url[1]);
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Done with reading CSV");
    }
}

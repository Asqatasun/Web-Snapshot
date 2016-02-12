/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.asqatasun.websnapshot.service;

import java.net.MalformedURLException;
import java.net.URL;
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

//    public void testGetThumbnail() {
//        System.out.println("getThumbnail");
//        SnapshotCreator instance = new SnapshotCreatorImpl();
//        String csvFileToRead = "src/test/resources/LIST-online-all-sites.txt";
//        BufferedReader br = null;
//        String line = "";
//        String[] url = null;
//        int i = 0;
//        try {
//            br = new BufferedReader(new FileReader(csvFileToRead));
//            while ((line = br.readLine()) != null && i < 2) {
//                url = line.split(";");
//                Logger.getLogger(this.getClass()).debug("Url = " + url[1]);
//                instance.requestSnapshotCreation(url[1]);
//                i++;
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        System.out.println("Done with reading CSV");
//    }
//    
    public void testGetThumbnail() {
        System.out.println("getThumbnail");
        SnapshotCreator instance = new SnapshotCreatorImpl();
        SnapshotCreationResponse scr = instance.requestSnapshotCreation("http://www.amazon.fr/Sony-NWZ-W273B-CEW-Baladeur-m%C3%A9moire-IPX8-Noir/dp/B00AYAFW0O");
        Logger.getLogger(this.getClass()).debug("Status = " + scr.getStatus());
        scr = instance.requestSnapshotCreation("http://repos.kbaccess.org/static/fr-FR-news/gala-20100323/index.html");
        Logger.getLogger(this.getClass()).debug("Status = " + scr.getStatus());
        scr = instance.requestSnapshotCreation("http://www.vaucluse.fr/");
        Logger.getLogger(this.getClass()).debug("Status = " + scr.getStatus());
    }
}

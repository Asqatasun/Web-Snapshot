/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opens.websnapshot.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author alingua
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SnapshotCreator instance = new SnapshotCreatorImpl();
        String csvFileToRead = "src/test/resources/LIST-online-all-sites.txt";
        BufferedReader br = null;
        String line = "";
        String[] url = null;
        int i = 0;
        try {
            br = new BufferedReader(new FileReader(csvFileToRead));
            while ((line = br.readLine()) != null) {
                url = line.split(";");
                System.out.println("Url = " + url[1]);
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

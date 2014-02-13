/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opens.websnapshot.service;

import junit.framework.TestCase;

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

    /**
     * Test of requestSnapshotCreation method, of class SnapshotCreatorImpl.
     */
    public void testRequestSnapshotCreation() {
        System.out.println("requestSnapshotCreation");
        String url = "http://www.google.fr";
        SnapshotCreatorImpl instance = new SnapshotCreatorImpl();
        SnapshotCreationResponse result = instance.requestSnapshotCreation(url);
        assertEquals("OK", result.getStatus());
    }
}

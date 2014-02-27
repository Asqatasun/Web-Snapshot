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
package org.opens.websnapshot.entity.service;

import java.util.Date;
import org.opens.tanaguru.sdk.entity.service.GenericDataService;
import org.opens.websnapshot.entity.Image;

/**
 *
 * @author alingua
 */
public interface ImageDataService extends GenericDataService<Image, Long> {

    /**
     * 
     * @param url
     * @param width
     * @param height
     * @return 
     */
     Image forceImageCreation(String url, int width, int height);
    
     /**
      * 
      * @param width
      * @param height
      * @return 
      */
     Image getNotCreatedImage(int width, int height);
     
    /**
     *
     * @param width
     * @param height
     * @param url
     * @return
     */
    Image getImageFromWidthAndHeightAndUrl(int width, int height, String url, boolean status);

    /**
     *
     * @param width
     * @param height
     * @param url
     * @param date
     * @return
     */
    Image getImageFromWidthAndHeightAndUrlAndDate(int width, int height, String url, Date date);
    
    /**
     * 
     * @param width
     * @param height
     * @param url
     * @param canonicalImage
     * @return 
     */
    Image createCanonicalAndNoCanonicalImage(int width, int height, String url, Image canonicalImage, Image notCanonicalImage);
    
    /**
     * 
     * @param canonicalImage
     * @param url
     * @param width
     * @param height
     * @return 
     */
    Image createCanonicalImage(Image canonicalImage, String url, int width, int height);
    
    /**
     * 
     * @param canonicalImage
     * @param width
     * @param height
     * @param url
     * @return 
     */
    Image createNotCanonicalImage(Image canonicalImage, int width, int height, String url, Image notCanonicalImage);
}
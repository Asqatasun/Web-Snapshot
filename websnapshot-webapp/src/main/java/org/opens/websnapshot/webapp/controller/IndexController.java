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
package org.opens.websnapshot.webapp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.opens.websnapshot.entity.Image;
import org.opens.websnapshot.entity.Image.Status;
import org.opens.websnapshot.entity.service.ImageDataService;
import org.opens.websnapshot.imageconverter.utils.ConvertImage;
import org.opens.websnapshot.urlmanager.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author alingua
 */
@Controller
public class IndexController {

    private static final String SUCCESS_HTTP_REQUEST = "OK";
    @Autowired
    private ImageDataService imageDataService;

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public HttpEntity<?> getThumbnail(
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "width", required = true) String width,
            @RequestParam(value = "height", required = true) String height,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "status", required = false) boolean status,
            HttpServletRequest request) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        String asciiUrl;
        
        try {
            asciiUrl = new URL(url).toURI().toASCIIString();
        } catch (URISyntaxException ex) {
            return new HttpEntity<String>("malformed url");
        }

        String requestStatus = getRequestStatus(asciiUrl, width, height, date);
        // if the parameters are not valid, we return an auto-generated image
        // with a text that handles the error type
        if (!requestStatus.equalsIgnoreCase(SUCCESS_HTTP_REQUEST)) {
            if (request.getMethod().equals("GET")) {
                return createErrorMessage(requestStatus, headers, Integer.valueOf(width), Integer.valueOf(height));
            } else {
                return new HttpEntity<String>(requestStatus);
            }
        }

        if (request.getMethod().equals("POST")) {
            Image image = imageDataService.forceImageCreation(asciiUrl, Integer.valueOf(width), Integer.valueOf(height));
            return new HttpEntity<Status>(image.getStatus());
        }

        // regarding the presence of the date parameter, we retrieve the closest
        // thumbnail or the latest
        if (date != null) {
            Date convertDate = convertDate(date);
            Image image = imageDataService.getImageFromWidthAndHeightAndUrlAndDate(
                    Integer.valueOf(width),
                    Integer.valueOf(height),
                    asciiUrl,
                    convertDate);
            return testImageAndReturnedIt(image, headers, Integer.valueOf(width), Integer.valueOf(height), status);
        } else {
            Image image = imageDataService.getImageFromWidthAndHeightAndUrl(
                    Integer.valueOf(width),
                    Integer.valueOf(height),
                    asciiUrl,
                    status);
            return testImageAndReturnedIt(image, headers, Integer.valueOf(width), Integer.valueOf(height), status);
        }
    }

    /**
     *
     * @param errorMessage
     * @param headers
     * @return an auto-generated image that handles the error message
     */
    private HttpEntity<byte[]> createErrorMessage(String errorMessage,
            HttpHeaders headers,
            int width,
            int height) throws IOException {
        return new HttpEntity<byte[]>(
                ConvertImage.createThumbnailFromErrorMessage(errorMessage, width, height),
                headers);
    }

    /**
     *
     * @param date
     * @return
     */
    private Date convertDate(String date) {
        if (isLong(date)) {
            return convertLongDateToDate(date);
        } else {
            return convertStringDateToDate(date);
        }
    }

    /**
     *
     * @param image
     * @param headers
     * @return
     * @throws IOException
     */
    private HttpEntity<?> testImageAndReturnedIt(Image image, HttpHeaders headers, int width, int height, boolean status) throws IOException {
        if (image == null && status) {
            JsonImage jsonImage = new JsonImage(imageDataService.getNotCreatedImage(width, height));
            return new HttpEntity<JsonImage>(jsonImage);
        }
        if (image == null) {
            return createErrorMessage("NOT_EXIST", headers, width, height);
        }
        if (status) {
            JsonImage jsonImage = new JsonImage(image);
            return new HttpEntity<JsonImage>(jsonImage);
        }
        if (image.getStatus().equals(Status.CREATED)) {
            return new HttpEntity<byte[]>(image.getRawData(), headers);
        } else {
            return createErrorMessage(image.getStatus().toString(), headers, width, height);
        }
    }

    private String getRequestStatus(String url, String width, String height, String date) {
        if (!isInteger(width) || !isInteger(height)) {
            return "invalid parameters format";
        }
        if (date != null && !isLong(date) && convertStringDateToDate(date) == null) {
            return "invalid date format";
        }
        return UrlUtils.checkURLAvailable(url);
    }

    /**
     *
     * @param str
     * @return
     */
    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     *
     * @param str
     * @return
     */
    private boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     *
     * @param date
     * @return
     */
    private Date convertLongDateToDate(String date) {
        Long longDate = Long.valueOf(date);
        return new Date(longDate);
    }

    /**
     *
     * @param date
     * @return
     */
    public Date convertStringDateToDate(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return df.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }
}

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.opens.websnapshot.entity.Thumbnail;
import org.opens.websnapshot.entity.service.ThumbnailDataService;
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
    private ThumbnailDataService thumbnailDataService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> getThumbnail(
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "width", required = true) String width,
            @RequestParam(value = "height", required = true) String height,
            @RequestParam(value = "date", required = false) String date) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        String requestStatus = getRequestStatus(url, width, height, date);
        // if the parameters are not valid, we return an auto-generated image
        // with a text that handles the error type
        if (!requestStatus.equalsIgnoreCase(SUCCESS_HTTP_REQUEST)) {
            byte[] errorMessage = ConvertImage.createThumbnailFromErrorMessage(requestStatus);
            return new HttpEntity<byte[]>(errorMessage, headers);
        }

        // regarding the presence of the date parameter, we retrieve the closest
        // thumbnail or the latest
        if (date != null) {
            Date convertDate;
            if (isLong(date)) {
                convertDate = convertLongDateToDate(date);
            } else {
                convertDate = convertStringDateToDate(date);
            }
            Thumbnail thumbnail = thumbnailDataService.getThumbnailFromDateAndUrlAndWidthAndHeight(
                    url,
                    convertDate,
                    Integer.valueOf(width),
                    Integer.valueOf(height));
            return new HttpEntity<byte[]>(thumbnail.getSnapshot().getImage().getRawData(), headers);
        } else {
            Thumbnail thumbnail = thumbnailDataService.getThumbnailFromUrlAndWidthAndHeight(
                    url,
                    Integer.valueOf(width),
                    Integer.valueOf(height));
            return new HttpEntity<byte[]>(thumbnail.getImage().getRawData(), headers);
        }

    }

    private String getRequestStatus(String url, String width, String height, String date) {
        if (!isInteger(width) || !isInteger(height)) {
            return "invalid parameters format";
        }
        if (!UrlUtils.checkIfURLIsValid(url)) {
            return "malformed url";
        }
        if (date != null && !isLong(date) && convertStringDateToDate(date) == null) {
            return "invalid data format";
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
    //    @RequestMapping(value = "/data.html", method = RequestMethod.GET)
    //    @ResponseBody
    //    public JsonThumbnail getData(
    //            ThumbnailImpl thumbnail,
    //            @RequestParam(value = "url", required = true) String url,
    //            @RequestParam(value = "size", required = true) int size) {
    //        MappingJackson2HttpMessageConverter http = new MappingJackson2HttpMessageConverter();
    //        thumbnail.setCreationDate(Calendar.getInstance().getTime());
    //        thumbnail.setStatus(thumbnailDataService.getThumbnailFromUrl(url, size).getStatus());
    //        return new JsonThumbnail(thumbnail);
    //    }
}

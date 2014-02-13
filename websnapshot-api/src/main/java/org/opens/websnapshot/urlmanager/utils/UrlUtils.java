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
package org.opens.websnapshot.urlmanager.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.validator.routines.UrlValidator;

public final class UrlUtils {

    public static final String URL_AVAILABLE = "OK";
    public static final String URL_NOT_AVAILABLE = "This url is not available";
    private static final int MIN_HTTP_VALID_CODE = 200;
    private static final int MAX_HTTP_VALID_CODE = 400;

    /**
     * private constructor
     */
    private UrlUtils() {
    }

    /**
     *
     * @param url
     * @return
     */
    public static boolean checkIfURLIsValid(String url) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_2_SLASHES);
        return urlValidator.isValid(url);
    }

    /**
     *
     * @param targetUrl
     * @return
     */
    public static String checkURLAvailable(String targetUrl) {
        try {
            URL url = new URL(targetUrl);
            URLConnection urlConnection = url.openConnection();

            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("HEAD");

            if (httpURLConnection.getResponseCode() >= MIN_HTTP_VALID_CODE
                    && httpURLConnection.getResponseCode() < MAX_HTTP_VALID_CODE) {
                return URL_AVAILABLE;
            }
            urlConnection = url.openConnection();
            httpURLConnection.disconnect();
            HttpURLConnection.setFollowRedirects(true);
            httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("GET");

            if (httpURLConnection.getResponseCode()
                    >= MIN_HTTP_VALID_CODE
                    && httpURLConnection.getResponseCode() < MAX_HTTP_VALID_CODE) {
                return URL_AVAILABLE;
            } else {
                return Integer.toString(httpURLConnection.getResponseCode()) + " " + httpURLConnection.getResponseMessage();
            }
        } catch (Exception e) {
            return URL_NOT_AVAILABLE;
        }
    }
}
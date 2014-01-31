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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public final class UrlUtils {

    private static final int MIN_HTTP_VALID_CODE = 200;
    private static final int MAX_HTTP_VALID_CODE = 400;
    
    public static boolean checkIfURLExists(String targetUrl) {
        try {
            URL url = new URL(targetUrl);
            return true;
        } catch (MalformedURLException ex) {
            return false;
        }
    }

    public static String checkURLAvailable(String targetUrl) {
        try {
            URL url = new URL(targetUrl);
            URLConnection urlConnection = url.openConnection();

            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("HEAD");

            if (httpURLConnection.getResponseCode() >= MIN_HTTP_VALID_CODE
                    && httpURLConnection.getResponseCode() < MAX_HTTP_VALID_CODE) {
                return "OK";
            } else {
                return Integer.toString(httpURLConnection.getResponseCode()) + " " + httpURLConnection.getResponseMessage();
            }
        } catch (Exception e) {
            return "This url is not available";
        }
    }
}
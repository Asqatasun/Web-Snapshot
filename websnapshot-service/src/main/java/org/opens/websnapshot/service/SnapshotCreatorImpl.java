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
package org.opens.websnapshot.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SnapshotCreatorImpl implements SnapshotCreator {

    private static final Logger LOGGER = Logger.getLogger(SnapshotCreatorImpl.class);
    public static final int DEFAULT_WINDOW_WIDTH = 1024;
    public static final int DEFAULT_WINDOW_HEIGHT = 768;
    public static final String FIREFOX_BROWSER_NAME = "firefox";
    public static final String PHANTOMJS_BROWSER_NAME = "phantomJs";
    private String phantomJsBinaryPath = "/opt/phantomjs/bin/phantomjs";
    private String firefoxBinaryPath = "/opt/firefox/firefox";
    public int windowWidth = DEFAULT_WINDOW_WIDTH;
    public int windowHeight = DEFAULT_WINDOW_HEIGHT;
    public String webDriver = PHANTOMJS_BROWSER_NAME;

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public String getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(String webDriver) {
        this.webDriver = webDriver;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void setPhantomJsBinaryPath(String phantomJsBinaryPath) {
        this.phantomJsBinaryPath = phantomJsBinaryPath;
    }

    @Override
    public byte[] getScreenshot(String url) {
        RemoteWebDriver driver = getWebDriver(windowWidth, windowHeight, webDriver);
        loadPage(driver, url);
        try {
            BufferedImage thumbnail = takeScreenshot(driver);
            closeDriver(driver);
            return convertThumbnailToByteArray(thumbnail);
        } catch (IOException ex) {
        }
        closeDriver(driver);
        return null;
    }

    /**
     *
     * @return
     */
    private RemoteWebDriver getWebDriver(int windowWidth, int windowHeight, String webDriver) {
        RemoteWebDriver driver;
        if (webDriver.equals(FIREFOX_BROWSER_NAME)) {
            driver = new FirefoxDriver(new FirefoxBinary(new File(firefoxBinaryPath)), new FirefoxProfile());
        } else {
            DesiredCapabilities caps = DesiredCapabilities.phantomjs();
            caps.setCapability(
                    PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    phantomJsBinaryPath);
            driver = new PhantomJSDriver(caps);
        }
        driver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
        return driver;
    }

    /**
     *
     * @param driver
     * @param url
     */
    private void loadPage(RemoteWebDriver driver, String url) {
        driver.get(url);
        driver.executeScript("if (getComputedStyle(document.body, null).backgroundColor === 'rgba(0, 0, 0, 0)'"
                + "|| getComputedStyle(document.body, null).backgroundColor === 'transparent') {"
                + "document.body.style.backgroundColor = 'white';"
                + "}");
    }

    /**
     *
     * @param driver
     */
    private void closeDriver(RemoteWebDriver driver) {
        driver.quit();
    }

    /**
     *
     * @param driver
     * @return
     * @throws IOException
     */
    private BufferedImage takeScreenshot(WebDriver driver) throws IOException {
        File rawImage = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        return ImageIO.read(rawImage);
    }

    /**
     *
     * @param thumbnail
     * @return
     * @throws IOException
     */
    private byte[] convertThumbnailToByteArray(BufferedImage thumbnail) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }
}

/*
 * Web-Snapshot
 * Copyright (C) 2008-2016  Asqatasun.org
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
 * Contact us by mail: asqatasun AT asqatasun DOT org
 */
package org.asqatasun.websnapshot.imageconverter.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public final class ConvertImage {

    private static final int TEXT_SIZE = 20;
    private static final int IMAGE_TEXT_POSITION = 20;

    /**
     * private constructor
     */
    private ConvertImage() {
    }

    /**
     *
     * @param httpResponse
     * @return
     */
    public static byte[] createThumbnailFromErrorMessage(String httpResponse, int width ,int height) throws IOException {
        Font f = new Font(Font.MONOSPACED, Font.PLAIN, TEXT_SIZE);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = img.createGraphics();
        g.setPaint(new Color(0, 0, 0));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.WHITE);
        g.setFont(f);
        g.drawString(httpResponse, IMAGE_TEXT_POSITION, IMAGE_TEXT_POSITION);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }

    /**
     *
     * @param img
     * @param width
     * @param height
     * @return
     */
    public static byte[] createThumbnailFromScreenshot(BufferedImage img, int width, int height) {
        try {
            // img is the screenshot without modification

            float ratio = Float.valueOf(width) / img.getWidth();
            Float proportionalHeight = img.getHeight() * ratio;

            //the new image is smaller than the original
            BufferedImage image = Thumbnails.of(img)
                    .size(width, proportionalHeight.intValue())
                    .asBufferedImage();

            // this is the final image, the thumbnail, with a crop for better visualisation
            if (height > image.getHeight()) {
                bufferedImageToByteArrayImage(image);
            }

            image = Thumbnails.of(image).size(image.getWidth(), height)
                    .crop(Positions.TOP_LEFT)
                    .asBufferedImage();

            return bufferedImageToByteArrayImage(image);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     *
     * @param image
     * @return
     * @throws IOException
     */
    public static BufferedImage byteArrayImageToBufferedImage(byte[] image) throws IOException {
        InputStream in = new ByteArrayInputStream(image);
        return ImageIO.read(in);
    }

    /**
     *
     * @param image
     * @return
     * @throws IOException
     */
    public static byte[] bufferedImageToByteArrayImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }
}
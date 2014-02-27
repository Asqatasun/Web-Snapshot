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
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author alingua
 */
public class SnapshotCreationResponseImpl implements SnapshotCreationResponse {

    private String status;
    private BufferedImage image;

    /**
     * Default constructor
     * @param image
     * @param status
     */
    public SnapshotCreationResponseImpl(
            BufferedImage image,
            String status) {
        this.status = status;
        this.image = image;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public byte[] getRawImage() {
        return convertThumbnailToByteArray(image);
    }

    /**
     *
     * @param thumbnail
     * @return
     * @throws IOException
     */
    private byte[] convertThumbnailToByteArray(BufferedImage thumbnail) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(thumbnail, "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ex) {
            return null;
        }
    }

}
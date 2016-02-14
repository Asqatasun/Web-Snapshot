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
package org.asqatasun.websnapshot.entity.factory;

import org.asqatasun.websnapshot.entity.Image;
import org.asqatasun.websnapshot.entity.ImageImpl;
import org.asqatasun.websnapshot.entity.NotCreatedImageImpl;

public class ImageFactoryImpl implements ImageFactory {

    public ImageFactoryImpl() {
        super();
    }

    @Override
    public Image create() {
        return new ImageImpl();
    }
    
    @Override
    public Image createNotCreatedImage() {
        return new NotCreatedImageImpl();
    }
}
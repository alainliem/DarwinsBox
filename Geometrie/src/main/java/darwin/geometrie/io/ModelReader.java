/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.io;

import java.io.*;

import darwin.geometrie.unpacked.Model;


/**
 * interface for 3D model file format parsers
 * <p/>
 * @author Daniel Heinrich
 */
public interface ModelReader
{
    public static final String POSITION_ATTRIBUTE = "Position";
    public static final String NORMAL_ATTRIBUTE = "Normal";
    public static final String TANGENT_ATTRIBUTE = "Tangent";
    public static final String TEXTURE_ATTRIBUTE = "TexCoord";

    /**
     *
     * @param source
     * @return
     * @throws IOException
     * if any standard IOException occurs or the file is corrupt
     * @throws WrongFileTypeException
     * is thrown when the read file is not of the supported file format of the reader
     */
    public Model[] readModel(InputStream source) throws IOException, WrongFileTypeException;

    /**
     * Checks if the reader supports a given file extension
     * @param fileExtension
     * @return
     * true if the Reader things that he can read the named file extension
     */
    public boolean isSupported(String fileExtension);
}

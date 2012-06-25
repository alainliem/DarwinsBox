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
package darwin.util.math.base.tupel;

import darwin.util.math.base.tupel.Tupel;

/**
 *
 * @author daniel
 */
public class GenericTupel implements Tupel
{
    private final float[] data;

    public GenericTupel(float[] data)
    {
        this.data = data;
    }

    @Override
    public float[] getCoords()
    {
        return data;
    }
}
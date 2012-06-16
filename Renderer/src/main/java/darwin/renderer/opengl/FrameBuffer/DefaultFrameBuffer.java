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
package darwin.renderer.opengl.FrameBuffer;

import javax.inject.*;

import darwin.renderer.GraphicContext;

/**
 *
 * @author daniel
 */
@Singleton
public class DefaultFrameBuffer extends FrameBufferObject
{

    @Inject
    public DefaultFrameBuffer(GraphicContext gc)
    {
        super(gc, 0);
    }

    @Override
    public int getWidth()
    {
        return gc.getGLWindow().getWidth();
    }

    @Override
    public int getHeight()
    {
        return gc.getGLWindow().getHeight();
    }
}

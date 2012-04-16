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
package darwin.renderer.geometrie.packed;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import darwin.renderer.geometrie.attributs.VertexAttributs;
import darwin.renderer.opengl.BufferObject;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.shader.Shader;

import static darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO Mesh organisation verbessern das verschiedenen shader genutzt werden können
public class RenderMesh implements Cloneable
{

    private VertexAttributs attributs;
    private final int indextype, vertexcount;
    private final BufferObject indice;
    private int primitivtype;
    private final boolean asarray;

    public RenderMesh(Shader shader, int primitivtype,
            BufferObject indice, VertexBO... vertexdata)
    {
        this.primitivtype = primitivtype;
        asarray = indice == null;
        this.indice = indice;
        vertexcount = vertexdata[0].getVertexCount();
        attributs = new VertexAttributs(shader, vertexdata, indice);
        indextype = GL2ES2.GL_UNSIGNED_INT;
    }

    public RenderMesh(Shader shader, BufferObject indice, VertexBO... vertexdata)
    {
        this(shader, GL.GL_TRIANGLES, indice, vertexdata);
    }

    public int getIndexcount()
    {
        return asarray ? vertexcount : indice.getSize() / 4; // Integer 4 byte;
    }

    //TODO subsets auch erlauben, nicht nur genaue übereinstimmungen
    public boolean isCompatible(Shader shader)
    {
        return attributs.isCompatible(shader);
    }

    public void render()
    {
        renderRange(0, getIndexcount());
    }

    public void renderRange(int offset, int length)
    {
        attributs.bind();
        if (asarray) {
            getGL().glDrawArrays(primitivtype, offset, length);
        } else {
            getGL().glDrawElements(primitivtype, length, indextype, offset * 4);
        }
        attributs.disable();
    }

    @Override
    public RenderMesh clone()
    {
        RenderMesh rm = null;
        try {
            rm =
                    (RenderMesh) super.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return rm;
    }
}
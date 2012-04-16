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
package darwin.renderer.geometrie.factorys;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import darwin.geometrie.data.*;
import darwin.geometrie.data.DataLayout.Format;
import darwin.geometrie.data.Element;
import darwin.renderer.geometrie.packed.RenderMesh;
import darwin.renderer.opengl.BufferObject.Target;
import darwin.renderer.opengl.BufferObject.Type;
import darwin.renderer.opengl.BufferObject.Usage;
import darwin.renderer.opengl.*;
import darwin.renderer.shader.Shader;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Rahmen implements GeometryFactory
{
    private static final Map<Float, GeometryFactory> instances =
                                                     new HashMap<>();

    public static GeometryFactory getInstance(float inset) {
        GeometryFactory gf = instances.get(inset);
        if (gf == null) {
            gf = new Rahmen(inset);
            instances.put(inset, gf);
        }
        return gf;
    }
    private final VertexBO attr;
    private final BufferObject indice;

    private Rahmen(float inset) {
        Element pos = new Element(GLSLType.VEC2, "Position");
        Element alpha = new Element(GLSLType.FLOAT, "Alpha");
        DataLayout dl = new DataLayout(Format.INTERLEAVE, pos, alpha);
        VertexBuffer vb = new VertexBuffer(dl, 8);
        indice = new BufferObject(
                Target.ELEMENT_ARRAY);
        Vertex v;
        for (int i = 0; i < 4; ++i) {
            v = vb.newVertex();
            float x = i > 1 ? 1f : -1f;
            float y = i == 1 || i == 2 ? 1f : -1f;
//                System.out.println("id:"+i+" x:"+x+" y:"+y);
            v.setAttribute(pos, x, y);
            v.setAttribute(alpha, 1f);// 0.7372549f);
        }
        for (int i = 0; i < 4; ++i) {
            v = vb.getVertex(vb.addVertex());
            float x = i > 1 ? 1f - inset : -1f + inset;
            float y = i == 1 || i == 2 ? 1f - inset : -1f + inset;
            v.setAttribute(pos, x, y);
//                System.out.println("id:"+(4+i)+" x:"+x+" y:"+y);
            v.setAttribute(alpha, 0f);
        }

        int[] ind = new int[]{0, 4, 1,
                              4, 5, 1,//left
                              5, 2, 1,
                              5, 6, 2,//top
                              6, 3, 2,
                              6, 7, 3,//right
                              7, 0, 3,
                              7, 4, 0 //bottom
        };
        indice.bind();
        {
            indice.bufferData(IntBuffer.wrap(ind), Type.STATIC, Usage.DRAW);
        }
        indice.disable();
        attr = new VertexBO(vb);
    }

    public RenderMesh buildRenderable(Shader shader) {
        return new RenderMesh(shader, indice, attr);
    }
}

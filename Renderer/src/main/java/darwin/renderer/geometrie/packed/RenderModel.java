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

import java.nio.IntBuffer;
import java.util.*;

import darwin.geometrie.unpacked.*;
import darwin.renderer.opengl.BufferObject;
import darwin.renderer.opengl.BufferObject.Target;
import darwin.renderer.opengl.BufferObject.Type;
import darwin.renderer.opengl.BufferObject.Usage;
import darwin.renderer.opengl.VertexBO;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.uniform.*;
import darwin.resourcehandling.wrapper.TextureContainer;

/**
 * Haelt alle Render relevanten Attribute eines 3D Modelles. Rendert ein Modell
 * nach diesen Attributen
 * <p/>
 * @author Daniel Heinrich
 */
public final class RenderModel implements Shaded, Cloneable
{

    private Material material;
    private RenderMesh rbuffer;
    private Shader shader;
    private final Set<UniformSetter> uniforms = new HashSet<>();
    private AsyncIni initiator = null;

    public RenderModel(RenderMesh rbuffer, Shader shader,
            Material mat)
    {
        this.rbuffer = rbuffer;
        material = mat;
        setShader(shader);
    }

    public RenderModel(Model model, final Shader shader)
    {
        material = model.getMat();

        final Mesh m = model.getMesh();
        initiator = new AsyncIni()
        {

            @Override
            public void ini()
            {
                VertexBO vbo = new VertexBO(m.getVertices());
                int[] i = m.getIndicies();
                BufferObject indice = null;
                if (i != null) {
                    indice = new BufferObject(Target.ELEMENT_ARRAY);
                    indice.bind();
                    {
                        indice.bufferData(IntBuffer.wrap(i), Type.STATIC, Usage.DRAW);
                    }
                    indice.disable();
                }
                rbuffer = new RenderMesh(shader, m.getPrimitiv_typ(), indice, vbo);
            }
        };
        setShader(shader);
    }

    @Override
    public void render()
    {
        if (shader.isInitialized()) {
            init();
            for (UniformSetter us : uniforms) {
                us.set();
            }
            shader.updateUniformData();
            rbuffer.render();
        }
    }

    private void init()
    {
        if (initiator != null) {
            initiator.ini();
            initiator = null;
        }
    }

    public void setShader(Shader shader)
    {
        this.shader = shader;
        if (material != null) {
            ShaderMaterial smaterial = new ShaderMaterial(shader, material);
            uniforms.addAll(Arrays.asList(smaterial.getSetter()));
        }
    }

    public void addSamplerSetter(String s, TextureContainer tc)
    {
        uniforms.add(new SamplerSetter(shader.getSampler(s), tc));
    }

    public void addUniformSetter(UniformSetter us)
    {
        uniforms.add(us);
    }

    @Override
    public Shader getShader()
    {
        return shader;
    }

    @Override
    public RenderModel clone()
    {
        return new RenderModel(rbuffer.clone(), shader, material);
    }
}

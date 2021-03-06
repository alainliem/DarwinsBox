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
package darwin.renderer.shader;

import java.util.*;

import darwin.geometrie.data.Element;
import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.Sampler.SamplerFactory;
import darwin.renderer.shader.uniform.*;
import darwin.resourcehandling.shader.ShaderFile;
import darwin.util.math.util.*;

import com.google.common.base.Optional;
import com.google.inject.assistedinject.*;
import javax.media.opengl.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Shader implements GenListener<MatrixEvent> {

    public interface ShaderFactory {

        public Shader create(ShaderFile sf);

        public Shader create(List<ShaderAttribute> attributes,
                             List<ShaderUniform> uniforms, List<String> samplerNames);
    }
    private final GraphicContext gc;
    private final Map<Element, ShaderAttribute> attributrMap;
    private final Map<String, ShaderUniform> uniformMap;
    private final Map<String, Sampler> samplerMap;
    private final MatrixSetter matricen;
     private ShaderProgramm programm;
    private int attrhash;
    private List<UniformSetter> usetter = new LinkedList<>();

    @AssistedInject
    public Shader(GraphicContext gcont, SamplerFactory factory,
                  @Assisted ShaderFile sf) {
        this(gcont, factory, sf.getAttributs(), sf.getUniforms(), sf.getSampler());
    }

    @AssistedInject
    public Shader(GraphicContext gcont, SamplerFactory factory,
                  @Assisted List<ShaderAttribute> attributes,
                  @Assisted List<ShaderUniform> uniforms,
                  @Assisted List<String> samplerNames) {
        gc = gcont;
        matricen = new MatrixSetter();
        attributrMap = new HashMap<>(attributes.size());
        for (ShaderAttribute sa : attributes) {
            attributrMap.put(sa.element, sa);
        }

        uniformMap = new HashMap<>(uniforms.size());
        for (ShaderUniform su : uniforms) {
            uniformMap.put(su.getName(), su);
            String b = su.getElement().getBezeichnung();
            if (b != null && b.startsWith("MAT_")) {
                matricen.addUniform(su);
            }
        }

        this.samplerMap = new HashMap<>(samplerNames.size());
        int nummber = 0;
        for (String name : samplerNames) {
            Sampler sampler = factory.create(name, GL.GL_TEXTURE0 + nummber++);
            samplerMap.put(name, sampler);
        }
    }

    public Shader ini(ShaderProgramm prog) {
        programm = prog;

        ini(attributrMap);
        ini(uniformMap);

        attrhash = buildAttrHash();

        for (Sampler s : samplerMap.values()) {
            s.setShader(prog);
        }

        return this;
    }

    @SuppressWarnings("unchecked, nullness")
    private void ini(Map<?, ? extends ShaderElement> map) {
        for (ShaderElement se : map.values()) {
            se.ini(programm);
        }
    }

    private int buildAttrHash() {
        int hash = 9;
        for (ShaderAttribute sa : attributrMap.values()) {
            if (sa.getIndex() != -1) {
                hash = 97 * hash + sa.hashCode();
            }
        }
        return hash;
    }

    public void bind() {
        assert isInitialized();
        programm.use();
    }

    public void updateUniformData() {
        bind();

        matricen.set();

        for (UniformSetter us : usetter) {
            us.set();
        }

        GL2ES2 gl = gc.getGL().getGL2ES2();
        for (ShaderUniform su : uniformMap.values()) {
            if (su.wasChanged()) {
                gl.glUniform(su.getData());
            }
        }
    }

    public void addUSetter(UniformSetter uss) {
        usetter.add(uss);
    }

    public boolean isInitialized() {
        return programm != null;
    }

    
    public ShaderProgramm getProgramm() {
        return programm;
    }

    public Optional</**/ ShaderUniform> getUniform(String name) {
        return Optional.fromNullable(uniformMap.get(name));
    }

    public Optional</**/ ShaderAttribute> getAttribut(Element ele) {
        return Optional.fromNullable(attributrMap.get(ele));
    }

    public Collection<Element> getAttributElements() {
        return attributrMap.keySet();
    }

    public Optional</**/ Sampler> getSampler(String name) {
        return Optional.fromNullable(samplerMap.get(name));
    }

    public int getAttributsHash() {
        return attrhash;
    }

    @Override
    public void changeOccured(MatrixEvent t) {
        matricen.changeOccured(t);
    }
}

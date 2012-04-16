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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;

import darwin.geometrie.data.*;
import darwin.geometrie.unpacked.Mesh;
import darwin.geometrie.unpacked.Model;
import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.errorhandling.BadFormatException;
import darwin.jopenctm.errorhandling.InvalidDataException;
import darwin.jopenctm.io.CtmFileReader;

import static darwin.geometrie.data.DataLayout.Format.*;
import static darwin.geometrie.data.DataType.*;

/**
 *
 * @author daniel
 */
public class CtmModelReader implements ModelReader
{

    private static final Element position, texcoord, normal;

    static {
        position = new Element(new GenericVector(FLOAT, 3), "Position");
        texcoord = new Element(new GenericVector(FLOAT, 2), "TexCoord");
        normal = new Element(new GenericVector(FLOAT, 3), "Normal");
    }

    @Override
    public Model[] readModel(InputStream source) throws WrongFileTypeException, IOException
    {
        CtmFileReader cr = new CtmFileReader(source);
        try {
            cr.decode();
        } catch (BadFormatException ex) {
            throw new WrongFileTypeException("The model has some bad format: "
                    + ex.getMessage());
        } catch (InvalidDataException ex) {
            throw new IOException("The model has some invalide data: " + ex.getMessage());
        }
        darwin.jopenctm.data.Mesh rm = cr.getMesh();

        return new Model[]{convertMesh(rm)};
    }

    @Override
    public boolean isSupported(String fileExtension)
    {
        return fileExtension.toLowerCase().equals("cmt");
    }

    private Model convertMesh(darwin.jopenctm.data.Mesh mesh)
    {
        Collection<Element> elements = new ArrayList<>();
        elements.add(position);

        if (mesh.hasNormals()) {
            elements.add(normal);
        }

        Element[] uvEle = new Element[mesh.getUVCount() - 1];
        if (mesh.getUVCount() > 0) {
            elements.add(texcoord);
            if (mesh.getUVCount() > 1) {
                VectorType float2 = new GenericVector(FLOAT, 2);
                for (int i = 1; i < mesh.texcoordinates.length; i++) {
                    AttributeData ad = mesh.texcoordinates[i];
                    uvEle[i - 1] = new Element(float2, ad.name);
                    elements.add(uvEle[i - 1]);
                }
            }
        }

        VectorType float4 = new GenericVector(FLOAT, 4);
        Element[] attEle = new Element[mesh.getAttrCount()];
        for (int i = 0; i < mesh.attributs.length; ++i) {
            attEle[i] = new Element(float4, mesh.attributs[i].name);
            elements.add(attEle[i]);
        }

        Element[] elar = new Element[elements.size()];

        elements.toArray(elar);
        DataLayout layout = new DataLayout(AUTO, elar);
        int vcount = mesh.getVertexCount();
        VertexBuffer vb = new VertexBuffer(layout, vcount);

        vb.fullyInitialize();

        fillElement(vb, position, mesh.vertices);

        if (mesh.hasNormals()) {
            fillElement(vb, normal, mesh.normals);
        }

        if (mesh.getUVCount() > 0) {
            fillElement(vb, texcoord, mesh.texcoordinates[0].values);
            if (mesh.getUVCount() > 1) {
                VectorType float2 = new GenericVector(FLOAT, 2);
                for (int i = 1; i < mesh.texcoordinates.length; i++) {
                    AttributeData ad = mesh.texcoordinates[i];
                    elements.add(new Element(float2, ad.name));
                    fillElement(vb, uvEle[i - 1], mesh.texcoordinates[i].values);
                }
            }
        }

        for (int i = 0; i < mesh.attributs.length; ++i) {
            fillElement(vb, attEle[i], mesh.attributs[i].values);
        }

        Mesh m = new Mesh(mesh.indices, vb, GL.GL_TRIANGLES);

        //TODO Material
        return new Model(m, null);
    }

    public void fillElement(VertexBuffer vb, Element e, float[] data)
    {
        ByteBuffer b = ByteBuffer.allocateDirect(4 * data.length);
        FloatBuffer buffer = b.asFloatBuffer();
        buffer.put(data);
        buffer.rewind();

        for (Vertex v : vb) {
            v.setAttribute(e, b);
        }
    }
}
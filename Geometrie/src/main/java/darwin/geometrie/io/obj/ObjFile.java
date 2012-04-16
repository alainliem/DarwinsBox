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
package darwin.geometrie.io.obj;

import java.io.*;
import java.util.*;

import darwin.util.math.base.Vec3;
import darwin.util.math.base.Vector;

/**
 * DatenModell einer OBJ Datei
 * <p/>
 * @author Daniel Heinrich
 */
public class ObjFile implements Externalizable
{

    private List<Vec3> verticies = new ArrayList<>();
    private List<Vec3> normals = new ArrayList<>();
    private List<Vector> texcoords = new ArrayList<>();
    transient private Vec3 min = new Vec3(), max = new Vec3();
    private Map<ObjMaterial, List<Face>> subobjekts = new HashMap<>();
    transient private List<Face> accfaces;

    public void addVertex(Vec3 pos)
    {
        if (verticies.isEmpty()) {
            min = pos.clone();
            max = pos.clone();
        } else {
            for (int i = 0; i < 3; i++) {
                double[] p = pos.getCoords();
                if (p[i] < min.getCoords()[i]) {
                    min.getCoords()[i] = p[i];
                } else if (p[i] > max.getCoords()[i]) {
                    max.getCoords()[i] = p[i];
                }
            }
        }

        verticies.add(pos);
    }

    public void addFace(Face face)
    {
        if (accfaces == null) {
            accfaces = new LinkedList<>();
            subobjekts.put(new ObjMaterial("__empty"), accfaces);
        }

        accfaces.add(face);
    }

    public void setAccMaterial(ObjMaterial mat)
    {
        accfaces = subobjekts.get(mat);
        if (accfaces == null) {
            accfaces = new LinkedList<>();
            subobjekts.put(mat, accfaces);
        }
    }

    public void center()
    {
        if (verticies.isEmpty()) {
            return;
        }

        Vec3 shift = min.add(max);
        shift.mult(-0.5f, shift);

        for (Vec3 v : verticies) {
            v.add(shift, v);
        }

        min.add(shift, min);
        max.add(shift, max);
    }

    public void rescale(double scale)
    {
        if (scale == 1) {
            return;
        }

        for (Vec3 v : verticies) {
            v.mult(scale, v);
        }


        max.mult(scale, max);
        min.mult(scale, min);
    }

    public List<Face> getFaces(ObjMaterial mat)
    {
        return subobjekts.get(mat);
    }

    public Set<ObjMaterial> getMaterials()
    {
        return subobjekts.keySet();
    }

    public List<Vec3> getNormals()
    {
        return normals;
    }

    public List<Vector> getTexcoords()
    {
        return texcoords;
    }

    public List<Vec3> getVerticies()
    {
        return verticies;
    }

    public double getWidth()
    {
        return max.getX() - min.getX();
    }

    public double getHeight()
    {
        return max.getY() - min.getY();
    }

    public double getDepth()
    {
        return max.getZ() - min.getZ();
    }

    public Vec3 getMin()
    {
        return min;
    }

    public Vec3 getMax()
    {
        return max;
    }

    public boolean hasNormals()
    {
        return !normals.isEmpty();
    }

    public boolean hasTexCoords()
    {
        return !texcoords.isEmpty();
    }

    public boolean checkIntegrity(boolean attributeIntegrity)
    {
        for (List<Face> subs : subobjekts.values()) {
            for (Face f : subs) {
                for (VertexIDs vid : f.getVertice()) {
                    if (attributeIntegrity && hasNormals()) {
                        if (vid.normal == 0) {
                            return false;
                        }
                    }
                    if (attributeIntegrity && hasTexCoords()) {
                        if (vid.texcoord == 0) {
                            return false;
                        }
                    }
                    if (!inRange(vid.normal, normals.size())) {
                        return false;
                    }
                    if (!inRange(vid.position, verticies.size())) {
                        return false;
                    }
                    if (!inRange(vid.texcoord, texcoords.size())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean inRange(int index, int max)
    {
        if (index < 0) {
            index = max + index + 1;
        }
        return index <= max;
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        writeVec3List(out, verticies);
        writeVec3List(out, normals);
        writeVecList(out, texcoords);
        out.writeObject(subobjekts);
    }

    private void writeVec3List(ObjectOutput out, List<Vec3> l)
            throws IOException
    {
        out.writeInt(l.size());
        for (Vec3 v : l) {
            double[] d = v.getCoords();
            out.writeFloat((float) d[0]);
            out.writeFloat((float) d[1]);
            out.writeFloat((float) d[2]);
        }
    }

    private void writeVecList(ObjectOutput out, List<Vector> l)
            throws IOException
    {
        int dim = l.get(0).getDimension();
        out.writeInt(l.size());
        out.writeInt(dim);
        for (Vector v : l) {
            for (double dd : v.getCoords()) {
                out.writeFloat((float) dd);
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        verticies = readVec3List(in);
        buildMinMax();
        normals = readVec3List(in);
        texcoords = readVecList(in);
        subobjekts = (Map<ObjMaterial, List<Face>>) in.readObject();
    }

    private List<Vec3> readVec3List(ObjectInput in) throws IOException
    {
        List<Vec3> ret = new ArrayList<>();
        int len = in.readInt();
        for (int i = 0; i < len; ++i) {
            ret.add(new Vec3(in.readFloat(),
                    in.readFloat(),
                    in.readFloat()));
        }
        return ret;
    }

    private List<Vector> readVecList(ObjectInput in)
            throws IOException
    {
        List<Vector> ret = new ArrayList<>();
        int len = in.readInt();
        int dim = in.readInt();
        for (int i = 0; i < len; ++i) {
            double[] d = new double[dim];
            for (int j = 0; j < dim; ++j) {
                d[j] = in.readFloat();
            }
            ret.add(new Vector(d));
        }

        return ret;
    }

    private void buildMinMax()
    {
        if (verticies.isEmpty()) {
            return;
        }

        min = verticies.get(0).clone();
        max = verticies.get(0).clone();

        for (int j = 1; j < verticies.size(); j++) {
            Vec3 vec3 = verticies.get(j);
            for (int i = 0; i < 3; i++) {
                double[] p = vec3.getCoords();
                if (p[i] < min.getCoords()[i]) {
                    min.getCoords()[i] = p[i];
                } else if (p[i] > max.getCoords()[i]) {
                    max.getCoords()[i] = p[i];
                }
            }

        }
    }
}

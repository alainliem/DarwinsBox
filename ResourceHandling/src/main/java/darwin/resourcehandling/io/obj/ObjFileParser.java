/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.io.obj;

import java.io.*;
import java.util.Hashtable;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

import darwin.renderer.geometrie.unpacked.ObjMaterial;
import darwin.resourcehandling.resmanagment.RessourcesLoader;
import darwin.util.math.base.Vec3;
import darwin.util.math.base.Vector;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ObjFileParser
{
    private static class Log
    {
        private static Logger ger = Logger.getLogger(ObjFileParser.class);
    }
    private String file;
    private Pattern leer, slash;
    private Hashtable<String, ObjMaterial> materials;

    public ObjFileParser(String file) {
        this.file = file;
        materials = new Hashtable<>();
        leer = Pattern.compile(" ");
        slash = Pattern.compile("/");
    }

    public ObjFile loadOBJ() {
        ObjFile obj = new ObjFile();
        InputStream is = RessourcesLoader.getRessource(file);
        if (is == null) {
            Log.ger.fatal("Model \"" + file
                    + "\" konnte nicht gefunden werden.");
            return obj;
        }

        Reader fr = null;
        try {
            fr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = leer.split(line, 2);
                if (s.length == 2)
                    parseValue(obj, s[0], leer.split(s[1].trim()));
            }
            // <editor-fold defaultstate="collapsed" desc="catching stuff">
        } catch (IOException ex) {
            Log.ger.fatal(ex.getMessage(), ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }// </editor-fold>


        return obj;
    }

    public void parseValue(ObjFile obj, String type, String[] values) {
        switch (type) {
            case "v":
                obj.addVertex(new Vec3(parseDoubless(values)));
                break;
            case "vn":
                obj.getNormals().add(new Vec3(parseDoubless(values)));
                break;
            case "vt":
                obj.getTexcoords().add(new Vector(parseDoubless(values)));
                break;
            case "f":
                obj.addFace(parseFace(values));
                break;
            case "usemtl":
                useMaterial(obj, values);
                break;
            case "mtllib":
                parseMtlLib(values);
                break;
        }
    }

    private double[] parseDoubless(String[] values) {
        double[] vals = new double[values.length];
        for (int i = 0; i < values.length; i++)
            if (!values[i].isEmpty())
                vals[i] = Double.parseDouble(values[i]);
        return vals;
    }

    private Face parseFace(String[] values) {
        VertexIDs[] vertice = new VertexIDs[values.length];

        for (int i = 0; i < vertice.length; i++) {
            String[] ids = slash.split(values[i]);
            int[] vertex = new int[3];
            for (int j = 0; j < ids.length; j++)
                if (!ids[j].isEmpty())
                    vertex[j] = Integer.parseInt(ids[j]);
                else
                    vertex[j] = 0;
            vertice[i] = new VertexIDs(vertex);
        }

        return new Face(vertice);
    }

    private void parseMtlLib(String[] mtllib) {
        try {

            MtlFormatReader mtl = new MtlFormatReader(mtllib[0]);
            materials = mtl.loadMaterials();
        } catch (FileNotFoundException ex) {
            Log.ger.error("Eine Material Bibilothek konnte"
                    + " nicht gefunden werde."
                    + "\n" + ex.getLocalizedMessage());
        }
    }

    private void useMaterial(ObjFile obj, String[] values) {
        String name = values[0];
        ObjMaterial mat = materials.get(name);
        if (mat == null) {
            mat = new ObjMaterial(name);
            materials.put(name, mat);
        }

        obj.setAccMaterial(mat);
    }
}



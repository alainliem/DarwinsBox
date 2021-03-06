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
import java.nio.file.Paths;
import java.util.*;

import darwin.resourcehandling.handle.ClasspathFileHandler;
import darwin.util.logging.InjectLogger;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 * Parser f�r das MTL Material Format
 * <p/>
 * @author Daniel Heinrich
 */
public class MtlFormatReader
{

    private static final String modelfolder = "resources/Models/";
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private Map<String, ObjMaterial> materials;
    private ObjMaterial accmat;
    private String path;

    public MtlFormatReader(String path)
    {
        this.path = path;
    }

    public Map<String, ObjMaterial> loadMaterials() throws IOException
    {
        if (materials == null) {
            materials = new HashMap<>();
            InputStream is = new ClasspathFileHandler(Paths.get(modelfolder + path)).getStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] s = line.split(" ", 2);
                    if (s.length == 2) {
                        parseValue(s[0], s[1].trim().split(" "));
                    }
                }
            }
        }
        return materials;
    }

    public void parseValue(String type, String[] values)
    {
        switch (type.trim()) {
            case "Ns":
                accmat.setSpecular_exponent(parseFloats(values)[0]);
                break;
            case "Ka":
                accmat.setAmbient(parseFloats(values));
                break;
            case "Kd":
                accmat.setDiffuse(parseFloats(values));
                break;
            case "Ks":
                accmat.setSepcular(parseFloats(values));
                break;
            case "Ke":
                accmat.setEmission(parseFloats(values));
                break;
            case "map_Ka":
                accmat.setAmbientTex(mergestrings(values));
                break;
            case "map_Kd":
                accmat.setDiffuseTex(mergestrings(values));
                break;
            case "map_Ks":
                accmat.setSpecularTex(mergestrings(values));
                break;
            case "map_bump":
                accmat.setNormalTex(mergestrings(values));
                break;
            case "map_d":
                accmat.setAlphaTex(mergestrings(values));
                break;
            case "bump":
                accmat.setBumbTex(values[0]);
                break;
            case "newmtl":
                newMaterial(values[0]);
                break;
        }
    }

    private String mergestrings(String[] values)
    {
        StringBuilder s = new StringBuilder(values[0]);
        for (int i = 1; i < values.length; i++) {
            s.append(' ');
            s.append(values[i]);
        }
        return s.toString();
    }

    private void newMaterial(String name)
    {
        ObjMaterial mat;
        mat = materials.get(name);
        if (mat == null) {
            mat = new ObjMaterial(name);
            materials.put(name, mat);
        }
        accmat = mat;
    }

    private float[] parseFloats(String[] values)
    {
        float[] vals = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty()) {
                vals[i] = Float.parseFloat(values[i]);
            }
        }
        return vals;
    }
}

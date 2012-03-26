/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.shader.uniform;

import java.io.Serializable;
import java.util.Arrays;


/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class GameMaterial implements Serializable
{
    private final float[] diffuse, ambient, specular;
    public final float specular_exponet;
    public final String diffuseTex;
    public final String specularTex;
    public final String normalTex;
    public final String alphaTex;

    public GameMaterial(float[] diffuse, float[] ambient, float[] specular, float specular_exponet, String diffuseTex, String specularTex, String normalTex, String alphaTex)
    {
        this.diffuse = diffuse;
        this.ambient = ambient;
        this.specular = specular;
        this.specular_exponet = specular_exponet;
        this.diffuseTex = diffuseTex;
        this.specularTex = specularTex;
        this.normalTex = normalTex;
        this.alphaTex = alphaTex;
    }

    public float[] getDiffuse() {
        return diffuse;
    }

    public float[] getAmbient() {
        return ambient;
    }

    public float[] getSpecular() {
        return specular;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GameMaterial other = (GameMaterial) obj;
        if (!Arrays.equals(this.diffuse, other.diffuse))
            return false;
        if (!Arrays.equals(this.ambient, other.ambient))
            return false;
        if (!Arrays.equals(this.specular, other.specular))
            return false;
        if (this.specular_exponet != other.specular_exponet)
            return false;
        if ((this.diffuseTex == null) ? (other.diffuseTex != null) : !this.diffuseTex.equals(other.diffuseTex))
            return false;
        if ((this.specularTex == null) ? (other.specularTex != null) : !this.specularTex.equals(other.specularTex))
            return false;
        if ((this.normalTex == null) ? (other.normalTex != null) : !this.normalTex.equals(other.normalTex))
            return false;
        if ((this.alphaTex == null) ? (other.alphaTex != null) : !this.alphaTex.equals(other.alphaTex))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.hashCode(this.diffuse);
        hash = 97 * hash + Arrays.hashCode(this.ambient);
        hash = 97 * hash + Arrays.hashCode(this.specular);
        hash = 97 * hash + Float.floatToIntBits(this.specular_exponet);
        hash =
        97 * hash + (this.diffuseTex != null ? this.diffuseTex.hashCode() : 0);
        hash =
        97 * hash + (this.specularTex != null ? this.specularTex.hashCode() : 0);
        hash =
        97 * hash + (this.normalTex != null ? this.normalTex.hashCode() : 0);
        hash =
        97 * hash + (this.alphaTex != null ? this.alphaTex.hashCode() : 0);
        return hash;
    }

}

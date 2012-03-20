/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.resmanagment.texture;

import com.jogamp.opengl.util.texture.*;
import java.io.IOException;
import java.io.InputStream;
import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import darwin.resourcehandling.io.TextureUtil;

import static darwin.renderer.GraphicContext.*;
import static darwin.resourcehandling.resmanagment.ResourcesLoader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class CubeMapJob extends TextureLoadJob
{
    private static class Log
    {
        private static Logger ger = Logger.getLogger(CubeMapJob.class);
    }
    private static final String texturepath = "resources/Textures/";

    public CubeMapJob(String path) {
        super(path, -1, -1);
    }

    @Override
    public Texture load() {
        Texture re = null;
        try {
            re = TextureUtil.loadCubeMap(getPath());
            tcontainer.setTexture(re);
        } catch (IOException ex) {
            Log.ger.warn("CubeMap " + getPath() + " konnte nicht geladen werden.\n("
                    + ex.getLocalizedMessage() + ")", ex);

            GL gl = getGL();
            re = TextureIO.newTexture(GL.GL_TEXTURE_CUBE_MAP);
            re.bind(gl);
            try {
                InputStream iss = getRessource(texturepath + "error.dds");
                TextureData data = TextureIO.newTextureData(gl.getGLProfile(),
                                                            iss, false,
                                                            TextureIO.DDS);
                for (int i = 0; i < 6; ++i)
                    re.updateImage(gl, data, GL.GL_TEXTURE_CUBE_MAP + 2 + i);
            } catch (IOException ex1) {
                Log.ger.error("Keine Error Texturen gefunden.", ex1);
                return null;
            }

            TextureUtil.setTexturePara(re, GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE);
        }

        return re;
    }
}
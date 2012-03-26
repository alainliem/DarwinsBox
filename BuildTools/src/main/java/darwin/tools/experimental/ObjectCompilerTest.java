package darwin.tools.experimental;

import java.io.*;

import darwin.renderer.geometrie.unpacked.ObjMaterial;
import darwin.resourcehandling.io.obj.ObjFile;
import darwin.resourcehandling.io.obj.ObjFileParser;
import darwin.resourcehandling.resmanagment.ResourcesLoader;

/**
 *
 * @author some
 */
public class ObjectCompilerTest
{

    public void compile()
    {
        System.out.println("----- Compiling Object Files -----");
        File folder = new File("src/resources/Models");
        File[] files = folder.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith("obj");
            }
        });

        long parse = 0, save = 0, total = 0, comp = 0;
        int fileCount = files.length;
        for (File obj : files) {
            long time = System.currentTimeMillis();
            try {
                InputStream in = ResourcesLoader.getRessource("resources/Models/" + obj.getName());
                ObjFileParser ofr = new ObjFileParser(in);
                ObjFile f = ofr.loadOBJ();
                parse += System.currentTimeMillis() - time;
                int tris = 0;
                for (ObjMaterial m : f.getMaterials()) {
                    tris += f.getFaces(m).size();
                }
                System.out.println(obj.getName() + " verts:" + f.getVerticies().size() + " faces:" + tris);

                FileOutputStream fos = new FileOutputStream(
                        obj.getAbsolutePath() + ".bin");
                try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    time = System.currentTimeMillis();
                    oos.writeObject(f);
                    save += System.currentTimeMillis() - time;

                    total += obj.length();
                    comp += fos.getChannel().size();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                fileCount--;
            }
        }
        System.out.println("Objects Compressed: " + fileCount);
        System.out.println("Parse time: " + parse
                + "ms;  Write/Compress time: " + save + "ms");
        System.out.println("Compression-Ratio: " + (float) comp / total);
        System.out.println();
    }

    public void load()
    {
        System.out.println("----- Load Compressed Object Files -----");
        File folder = new File("src/resources/Models");
        File[] files = folder.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith("obj.bin");
            }
        });
        long load = 0;
        for (File obj : files) {
            try {
                FileInputStream fos = new FileInputStream(
                        obj.getAbsolutePath());
                try (ObjectInputStream oos = new ObjectInputStream(fos)) {
                    try {
                        long time = System.currentTimeMillis();
                        Object o = oos.readObject();
                        load += System.currentTimeMillis() - time;
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Loading time: " + load + "ms");
        System.out.println();
    }

    public static void main(String[] args) throws IOException
    {
        new ObjectCompilerTest().compile();
    }
}

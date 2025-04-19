package MCContentConvertor;

//import materialsCreatorNew.getJsonData;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class makeUVMap implements Paths {

    public static final String PNGs =
            UserDir + "\\textures\\pngs";
    public static final String PNGs2 =
            "\\assets\\minecraft\\textures\\";
    public final String PNGsFolder =
            PNGs+"\\minecraft_original"+PNGs2;
    public String prefix = "\\assets\\minecraft\\textures\\entity\\uvmap\\";
    public String UVMap;
    public ArrayList<String[][]> uvMap;

    public makeUVMap(int index) throws IOException, ParseException {
        getJsonData data = new getJsonData();
        if(!data.getModelName(index).equals("Template")) {

            ArrayList<Object[]> ret = data.getMakeUVMapValues3(index);
            if (!ret.isEmpty()) {
                ArrayList<String[][]> stuff = new ArrayList<>();
                for (Object[] obj : ret) {

                    String[] sds = new String[obj.length];
                    for (int i = 0; i < obj.length; i++) {
                        sds[i] = obj[i].toString();
                    }

                    String[] abc = obj[1].toString().replace("[", "").replace("]", "").split("\",\"");
                    String[] temp = new String[abc.length];
                    for (int i = 0; i < abc.length; i++) {
                        temp[i] = abc[i].replace("\"", "");
                    }
                    //System.out.println("temp: " + Arrays.toString(temp));
                    sds = new String[]{sds[0]};
                    String[][] ghg = new String[][]{sds, temp};

                    stuff.add(ghg);
                }
                this.uvMap = stuff;
                //System.out.println("stuff0: " + Arrays.deepToString(stuff.get(0)));
                //System.out.println("stuff00: " + Arrays.toString(stuff.get(0)[0]));
//        System.out.println("stuff3: "+ Arrays.toString(stuff.get(0)[1]));
               //System.out.println("stuff011: " + stuff.get(0)[1][1]);
                //System.out.println("stuff000: " + stuff.get(0)[0][0]);
                //System.out.println("stuff010: " + stuff.get(0)[1][0]);
                //System.out.println("stuff011: " + stuff.get(0)[1][1]);
                //System.out.println("stuff001: "+ stuff.get(0)[0][1]);
                // stuff.get(4 whole)[]
                //jj.get(0).get(jj.get(0));
                //this.uvMap = jj.get(0);
                //this.UVMap = data.getModelUVMap(index);
                // should this be running? its running already from texturegetter
                // probably why its printing twice
                System.out.println(Arrays.toString(makeImage()));
            }
        }
    }
    public boolean[] makeImage() throws IOException {

        int sizeX = this.uvMap.size();
        //System.out.println("sizeX: " + sizeX);
        boolean[] result = new boolean[sizeX];
        for(int X=0;X<sizeX;X++) {
            int size = this.uvMap.get(X)[1].length;
            //System.out.println("size: "+size);
            //create image from source image
            BufferedImage uvMapImage;
            BufferedImage[] frames = new BufferedImage[size];
            int width = 0;
            int height = 0;
            int countX = 0;
            int countY = 0;
            int[][] xy = new int[size][2];
            String uvMapName = this.uvMap.get(X)[0][0];
            System.out.println("Making UV Map for " + uvMapName + " ...");
            boolean usePx = false;
            for (int i = 0; i < size; i++) {
                usePx = false;
                String texture = this.uvMap.get(X)[1][i].split(":")[0];
                //System.out.println("X: "+ X+", i: "+i);
                //System.out.println("texture: "+ texture);
                String[] xxyy = this.uvMap.get(X)[1][i].split(":")[1].split(",");
                //System.out.println("xxyy: "+ Arrays.toString(xxyy));
                int x = 0; int y = 0;

                if(xxyy[0].contains("px") || xxyy[1].contains("px")) {
                    usePx = true;
                    x = Integer.parseInt(xxyy[0].replace("px", ""));
                    y = Integer.parseInt(xxyy[1].replace("px", ""));
                } else {
                    x = Integer.parseInt(xxyy[0]);
                    y = Integer.parseInt(xxyy[1]);
                }
                xy[i][0] = x;
                xy[i][1] = y;

                // get all images from rows
                //System.out.println("formatted string: " + PNGsFolder + texture + ".png");
                frames[i] = ImageIO.read(new File(PNGsFolder + texture + ".png"));

                //System.out.println("x: " + x + ", y: " + y);

                if (x > countX || i == 0) width += frames[i].getWidth();
                if (y > countY || i == 0) height += frames[i].getHeight();
                //System.out.println("width x Height: " + width + "x" + height);

                countX += x;
                countY += y;
            }

            uvMapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = uvMapImage.getGraphics();
            int[] drawXY = new int[]{0,0};
            for (int i = 0; i < frames.length; i++) {

                //int fWidth = frames[i].getWidth() * xy[i][0];
                //int fHeight = frames[i].getHeight() * xy[i][1];

                if(!usePx){
                    //System.out.println("fWidth x fHeight: " + drawXY[0] + " x " + drawXY[1]);
                    g.drawImage(frames[i], drawXY[0], drawXY[1], null);

                    drawXY[0] += frames[i].getWidth() * xy[i][0];
                    drawXY[1] += frames[i].getHeight() * xy[i][1];
                } else {
                    //System.out.println("fWidth x fHeight: " + drawXY[0] + " x " + drawXY[1]);
                    g.drawImage(frames[i], xy[i][0], xy[i][1], null);

                    drawXY[0] += frames[i].getWidth() * xy[i][0];
                    drawXY[1] += frames[i].getHeight() * xy[i][1];
                }
            }
            g.dispose();

            File filePath = new File(Paths.PngsMaterialsDir + prefix, uvMapName + ".png");
            if (!filePath.exists())
                filePath.getParentFile().mkdirs();

            result[X] = ImageIO.write(uvMapImage, "PNG", filePath);
        }
        return result;
    }

}

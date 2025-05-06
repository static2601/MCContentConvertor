package MCContentConvertor;

import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static MCContentConvertor.Pathnames.*;

public class makeUVMap {

    //TODO maybe add to pathnames as constants
    public static final String PNGs =
            USERDIR + "\\textures\\pngs";
    public static final String PNGs2 =
            "\\assets\\minecraft\\textures\\";
    public final String PNGsFolder =
            PNGs+"\\minecraft_original"+PNGs2;
    public String prefix = "\\assets\\minecraft\\textures\\entity\\uvmap\\";
    public ArrayList<String[][]> uvMap;

    public makeUVMap(int index) throws IOException, ParseException {
        getJsonData data = new getJsonData();
        System.out.println(data.getModelName(index));
        if(!data.getModelName(index).equals("Template")) {

            // ret is the arraylist of objects for makeUVMap from modelData.json,
            // instructions to make uv maps, example object[] : "comparator" : [{"","","",""}]
            // arraylist of object arrays of strings
            System.out.println("here....");
            ArrayList<String[]> ret = data.getMakeUVMapValues3(index);

            if (!ret.isEmpty()) {
                ArrayList<String[][]> arrays = new ArrayList<>();
                for (String[] str : ret) {
                    String[] name = new String[]{str[0]};
                    String strs = str[1];
                    String[] strarr = strs.replace("[", "").replace("]","").split("\",\"");

                    String[] temp = new String[strarr.length];
                    for (int i = 0; i < strarr.length; i++) {
                        temp[i] = strarr[i].replace("\"", "");
                    }
                    strarr = temp;
                    String[][] anstr = new String[][]{name, strarr};
                    arrays.add(anstr);
                }
                this.uvMap = arrays;

                System.out.println(Arrays.toString(makeImage()));
            } else System.out.println("ret is empty");
        }
    }

    public boolean[] makeImage() throws IOException {

        int sizeX = this.uvMap.size();
        boolean[] result = new boolean[sizeX];
        // loop through uv maps

        for(int X=0;X<sizeX;X++) {
            int size = this.uvMap.get(X)[1].length;
            System.out.println("size: "+size);
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
                String textures = this.uvMap.get(X)[1][i];
                System.out.println("textures: "+textures);
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
                frames[i] = ImageIO.read(new File(PNGsFolder + texture + ".png"));


                if (x > countX || i == 0) width += frames[i].getWidth();
                if (y > countY || i == 0) height += frames[i].getHeight();

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

            File filePath = new File(Pathnames.PngsMaterialsDir + prefix, uvMapName + ".png");
            if (!filePath.exists())
                filePath.getParentFile().mkdirs();

            result[X] = ImageIO.write(uvMapImage, "PNG", filePath);
        }
        return result;
    }

}

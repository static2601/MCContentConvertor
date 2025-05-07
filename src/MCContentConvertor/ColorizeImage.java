package MCContentConvertor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static MCContentConvertor.Pathnames.*;

public class ColorizeImage {

    ArrayList<long[]> pixels_array = new ArrayList<>();
    public ArrayList<JSONObject> pixelData = new ArrayList<>();
    private String texturePath;
    private String textureName;
    private Color customColor;

    public ColorizeImage(String texturePath, Color customColor) throws IOException, ParseException {
        this.texturePath = texturePath;
        this.customColor = customColor;
        Main();
    }
    ///  run this on all textures that need colorized
    public ColorizeImage(String texturePath) throws IOException, ParseException {
        this.texturePath = texturePath;
        Main();
    }

    public void Main() throws IOException, ParseException {

        getJsonData();
        try {
            // make image path and get texture name from path
            String image_path = USERDIR + FS + this.texturePath;
            String parent = new File(texturePath).getParent().replace("\\", "/");
            // clean up name for use as texture name
            textureName = texturePath
                    .replace(parent, "")
                    .replace("\\", "")
                    .replace("/", "")
                    .replace(" ", "") // shouldn't be spaces but who knows
                    .trim()
                    .split("\\.")[0]
            ;
            // remove suffix count from animated
            // so it can color properly
            String subStr = textureName.substring(textureName.lastIndexOf("_")+1);
            System.out.println(subStr);
            if (subStr.matches("-?\\d+")) {
                textureName = textureName.substring(0, textureName.lastIndexOf("_"));
            }

            BufferedImage originalImage = ImageIO.read(new File(image_path));

            // Proceed with color changing
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = newImage.createGraphics();

            g2d.fillRect(0, 0, width, height);
            g2d.dispose();

            File outputFile = new File(image_path);

            // get color from textures settings in json
            getSettingsData settings = new getSettingsData();
            JSONObject textureObject = settings.getTextureObj(textureName);
            if(textureObject != null) {

                String color_hex = textureObject.get("customColor").toString();
                Color color;
                if (Objects.equals(color_hex, "")) {
                    color_hex = textureObject.get("defaultColor").toString();
                }
                color = Color.decode(color_hex);

                tint(originalImage, newImage, new Color(color.getRed(), color.getGreen(), color.getBlue()));

                // save to json
                //settings.set_texturesKey

                ImageIO.write(newImage, "png", outputFile);
                System.out.println("Image saved successfully.");
            } else {
                System.err.println("TextureSettings for "+ textureName +" does not exist! Ignoring...");
            }

        } catch (IOException e) {
            System.err.println("Error loading or converting image: " + e.getMessage());
        }
    }

    public void tint(BufferedImage image, BufferedImage newImage, Color color) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // check here
                // if true, color, else continue or do nothing?
                long[] xy = new long[]{x, y};
                //if(colorPixels(xy)) {
               // color.decode()

                //Color color3 = new Color(color2.getRGB(), true);
                Color pixelColor = new Color(image.getRGB(x, y), true);
                int r = (pixelColor.getRed() + color.getRed()) / 2;
                int g = (pixelColor.getGreen() + color.getGreen()) / 2;
                int b = (pixelColor.getBlue() + color.getBlue()) / 2;
                int a = pixelColor.getAlpha();
                //int a = 255;
                //r -= 50; g -= 50; b -= 50;
                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                newImage.setRGB(x, y, rgba);
                //}
            }
        }
    }

    public boolean colorPixels(long[] pixel) {
        // not really needs since there exists a mask
        // grass_block_side_overlay
        // not for waste, this can be used to create a mask
        // for illuminating pixels like vine berries

        // build array of pixels to color
        // THEN, check for whatever....
        if (!pixels_array.isEmpty()) {
            ArrayList<long[]> pixels_array = makePixelsArray();
            for(int i=0; i<pixels_array.size(); i++) {
                if (pixel == pixels_array.get(i)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean checkPixelsInRange(long[] pixel, long[] from, long[] to) {
        for(long x=from[0]; x<=to[0]; x++) {
            for(long y=from[1]; y<=to[1]; y++) {
                // if current pixel is in range
                if (pixel[0] == x && pixel[1] == y ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkPixel(long[] pixel, long[] only) {
        // if current pixel in only pixel to check
        return pixel[0] == only[0] && pixel[1] == only[1];
    }

    public void getJsonData() throws IOException, ParseException {
        // parsing file "JSONExample.json"
        JSONArray jArr = (JSONArray) new JSONParser().parse(
                new FileReader("Assets/pixelData.json"));

        jArr.forEach(jobj ->
                pixelData.add((JSONObject) jobj)
        );
    }

    public JSONObject getDataByTexturename(String textureName) {
        for(int i=0; i < this.pixelData.size(); i++) {
            JSONObject jsonObject = this.pixelData.get(i);
            if (jsonObject.containsValue(textureName)) {
                return jsonObject;
            }

        }

//        for(JSONObject texture : this.pixelData) {
//            return (JSONObject) texture.get(textureName);
//        }
        return null;
    }

    public ArrayList<long[]> makePixelsArray() {

        //ArrayList<long[]> pixels_array = new ArrayList<>();
        JSONObject data = getDataByTexturename(textureName);
        if (data == null) {
            return pixels_array;
        } else {
            JSONArray pdata = (JSONArray) data.get("pixelData");
            for (int i = 0; i < pdata.size(); i++) {

                JSONObject jsonObject = (JSONObject) pdata.get(i);
                if (jsonObject.containsKey((i + 1) + "")) {

                    // first line of data
                    //JSONArray jarLine = (JSONArray) jsonObject.get("1");
                    JSONObject line = (JSONObject) ((JSONArray) jsonObject.get((i + 1) + "")).getFirst();

                    if (!line.containsKey("only")) {
                        JSONArray jo_from = (JSONArray) line.get("from");
                        long[] from = {(long) jo_from.get(0), (long) jo_from.get(1)};

                        JSONArray jo_to = (JSONArray) line.get("to");
                        long[] to = {(long) jo_to.get(0), (long) jo_to.get(1)};
                        //int[] to = (int[]) jo_to.getFirst();

                        for (long x = from[0]; x <= to[0]; x++) {
                            for (long y = from[1]; y <= to[1]; y++) {
                                pixels_array.add(new long[]{x, y});
                            }
                        }

                        //return checkPixelsInRange(pixel, from, to);
                    } else {
                        long[] only = (long[]) line.get("only");
                        //return checkPixel(pixel, only);
                        pixels_array.add(only);
                    }
                }
            }
        }
        System.out.println("pixels_array.size(): "+ pixels_array.size());
        return pixels_array;
    }
}

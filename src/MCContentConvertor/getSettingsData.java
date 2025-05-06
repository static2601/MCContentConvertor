package MCContentConvertor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class getSettingsData {
    public ArrayList<JSONObject> settings = new ArrayList<>();
    public GUIStart guiStartRef;
    private JSONObject jSettings;
    private String mcJarPath;
    private String gameDir;
    private String outputPath;
    private boolean compileModels;
    private boolean useUnits;
    private long units;
    private double scale;
    private JSONObject texturePacks;
    private boolean extractTextures;
    private String selectedTexturePack;
    private boolean openFolder;

    private JSONArray jaTextureSettings;
    private JSONArray jaModelSettings;

    public getSettingsData() throws IOException, ParseException {
        // initialize data
        getJsonData();

        settings.forEach(s -> {
            System.out.println(s.get("settings"));
            this.jSettings = (JSONObject) s.get("settings");
            this.jaTextureSettings = (JSONArray) s.get("textureSettings");
            this.jaModelSettings = (JSONArray) s.get("modelSettings");
        });
        // settings object
        this.mcJarPath = getObjKey("mcJarPath").toString();
        this.gameDir = getObjKey("gameDir").toString();
        this.outputPath = getObjKey("outputPath").toString();
        this.compileModels = (boolean) getObjKey("compileModels");
        this.useUnits = (boolean) getObjKey("useUnits");
        this.units = (long) getObjKey("units");
        this.scale = (double) getObjKey("scale");
        this.extractTextures = (boolean) getObjKey("extractTextures");
        this.texturePacks = (JSONObject) getObjKey("texturePacks");
        this.selectedTexturePack = getObjKey("selectedTexturePack").toString();
        this.openFolder = (boolean) getObjKey("openFolder");

        //textures object

        // get texturePacks
        // populate this.texturePack with whats in json
        // scan through assets/texturePacks for any new packs
        // add whats new, not adding whats there already to keep key name
        String UserDir = System.getProperty("user.dir");
        File texturePacksDir = new File(UserDir + "\\Assets\\TexturePacks");
        String[] texture_packs = texturePacksDir.list();

        assert texture_packs != null;
        for(String tp : texture_packs) {
            if (tp.endsWith(".zip")) {
                // if file.zip is not in pack...
                if (!checkInPack(tp, this.texturePacks)) {
                    String name = tp.split(".zip")[0];
                    this.texturePacks.put(encFn(name), tp);
                }
                //TODO reverse check if zip still exists and update
            }else
            if (tp.endsWith(".jar")) {
                // if file.zip is not in pack...
                if (!checkInPack(tp, this.texturePacks)) {
                    String name = tp.split(".jar")[0];
                    this.texturePacks.put(encFn(name), tp);
                }
            }
        }
        this.writeJson();

        System.out.println("this.texturePacks: "+ this.texturePacks);
    }

    private String encFn(String n) {
        n = n.replace(".", "")
                .replace(" ", "_")
                .replace("-", "_");
        return n;
    }

    public boolean checkInPack(String val, JSONObject pack) {
        // check if value (val) is in texturePacks object(pack)
        return pack.containsValue(val);
    }

    public static void main(String[] args) throws IOException, ParseException {
        getSettingsData gsd = new getSettingsData();
        System.out.println("mcJarPath: " + gsd.get_mcJarPath());
        System.out.println("compileModels: " + gsd.get_compileModels());
        System.out.println("texturePacks: " + gsd.get_texturePacks());
        System.out.println("units: " + gsd.get_units());
        gsd.set_settingsKey("mcJarPath", "new_path_here");
        System.out.println("mcJarPath: " + gsd.get_mcJarPath());
        System.out.println("units: " + gsd.get_units());
    }

    public void set_settingsKey(String name, Object val) {

        if (Objects.equals(name, "mcJarPath")) this.mcJarPath = (String) val;
        if (Objects.equals(name, "gameDir")) this.gameDir = (String) val;
        if (Objects.equals(name, "outputPath")) this.outputPath = (String) val;
        if (Objects.equals(name, "compileModels")) this.compileModels = (boolean) val;
        if (Objects.equals(name, "useUnits")) this.useUnits = (boolean) val;
        if (Objects.equals(name, "units")) this.units = (long) val;
        if (Objects.equals(name, "scale")) this.scale = (double) val;
        if (Objects.equals(name, "extractTextures")) this.extractTextures = (boolean) val;
        //if (Objects.equals(name, "texturePacks")) this.texturePacks = (int) val;
        if (Objects.equals(name, "selectedTexturePack")) this.selectedTexturePack = (String) val;
        if (Objects.equals(name, "openFolder")) this.openFolder = (boolean) val;

        writeJson();

    }

    public void set_texturesKey(String name, Object val, String textureName) {
        JSONObject texture = getTextureObj(textureName);
        if (texture == null) {
            // create new entry for texture and value
            texture = createTextureSettingObj(textureName);

        }
        if (Objects.equals(name, "uncolored")) texture.put("uncolored", val);
        if (Objects.equals(name, "defaultColor")) texture.put("defaultColor", val);
        if (Objects.equals(name, "customColor")) texture.put("customColor", val);
        if (Objects.equals(name, "transparent")) texture.put("transparent", val);

        writeJson();
    }

    private void writeJson() {
        //Write JSON file
        String UserDir = System.getProperty("user.dir");
        Path path = new File(UserDir + "\\Assets\\settings.json").toPath();
        try {

            JSONObject settings = new JSONObject();
            settings.put("mcJarPath", this.mcJarPath);
            settings.put("gameDir", this.gameDir);
            settings.put("outputPath", this.outputPath);
            settings.put("compileModels", this.compileModels);
            settings.put("useUnits", this.useUnits);
            settings.put("units", this.units);
            settings.put("scale", this.scale);
            settings.put("extractTextures", this.extractTextures);
            //JSONArray texturePackArr = new JSONArray();

            // only write to json new texture packs
            // need to get whats in there already and check against it
            settings.put("texturePacks", this.texturePacks);
            settings.put("selectedTexturePack", this.selectedTexturePack);
            settings.put("openFolder", this.openFolder);

            // get all texture settings
            // shouldnt need to do all this for textures? if so then above may not
            // be needed

            JSONObject allSettings = new JSONObject();
            allSettings.put("settings", settings);
            allSettings.put("textureSettings", this.jaTextureSettings);
            allSettings.put("modelSettings", this.jaModelSettings);


            final Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            String toSafe = gson.toJson(allSettings);

            Files.write(path, toSafe.getBytes());

            //file.write(allSettings.toJSONString());
            //file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setComboBarThing() {
        guiStartRef.setTexturePackCombo(this.texturePacks);
    }

    public String get_mcJarPath() {
        return this.jSettings.get("mcJarPath").toString();
    }
    public String get_gameDir() {
        return this.jSettings.get("gameDir").toString();
    }
    public String get_outputPath() {
        return this.jSettings.get("outputPath").toString();
    }
    public boolean get_compileModels() {
        return (boolean) this.jSettings.get("compileModels");
    }
    public boolean get_useUnits() {
        return (boolean) this.jSettings.get("useUnits");
    }
    public long get_units() {
        return (long) this.jSettings.get("units");
    }
    public double get_scale() {
        return (double) this.jSettings.get("scale");
    }
    public boolean get_extractTextures() {
        return (boolean) this.jSettings.get("extractTextures");
    }
    public JSONObject get_texturePacks() {
        return (JSONObject) this.jSettings.get("texturePacks");
    }
    public String get_selectedTexturePack() {
        return this.jSettings.get("selectedTexturePack").toString();
    }
    public boolean get_openFolder() {
        return (boolean) this.jSettings.get("openFolder");
    }

    public void getJsonData() throws IOException, ParseException {
        // parsing file "JSONExample.json"
        JSONObject obj = (JSONObject) new JSONParser().parse(
                new FileReader("Assets/settings.json"));
        settings.add(obj);
    }

    public Object getObjKey(String name) {
        return this.jSettings.get(name);
    }

    /// get texture settings object by textureName eg: 'oak_leaves'
    /// if object not found by textureName, return null
    public JSONObject getTextureObj(String textureName) {
        for(Object jo : jaTextureSettings) {
            JSONObject jobj = (JSONObject) jo;
            if (jobj.get("textureName").equals(textureName)) {
                return (JSONObject) jo;
            }
        }
        return null;
    }

    public JSONObject createTextureSettingObj(String textureName) {
        // not found, add new texture option
        JSONObject newTexture = new JSONObject();
        newTexture.put("uncolored", "");
        newTexture.put("defaultColor", "");
        newTexture.put("customColor", "");
        newTexture.put("transparent", false);
        newTexture.put("textureName", textureName);

        this.jaTextureSettings.add(newTexture);
        return newTexture;
    }
    /// get textureSettings array
    public JSONArray getTexturesSettings() {
        return this.jaTextureSettings;
    }
}

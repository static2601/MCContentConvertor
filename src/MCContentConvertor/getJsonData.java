package MCContentConvertor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class getJsonData {

    public ArrayList<JSONArray> modelData = new ArrayList<>();
    private int index;
    public String modelname;
    public String type;
    public String body;
    public String cdmaterials;
    public String sequence;
    public String surfaceprop;
    public String selfillum;
    public String collisionModel;

    /** special location of vtf. block/ = "" (default, not necessary for json), entity = entity/, etc.
     * if not ended in /, will get by name, else directory */
    //public String[] vtfLocation;
    public String collisionOptions;
    public String textureGroup;

    public getJsonData() throws IOException, ParseException {
        // parsing file "JSONExample.json"
        JSONArray obj = (JSONArray) new JSONParser().parse(
                new FileReader("Assets/ModelData.json"));
                //new FileReader("src/MCContentConvertor/ModelData.json"));
        modelData.add(obj);
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public getJsonData(int index) throws IOException, ParseException {
        this.index = index;
        JSONArray obj = (JSONArray) new JSONParser().parse(
                new FileReader("Assets/ModelData.json"));
                //new FileReader("src/MCContentConvertor/ModelData.json"));
        this.modelData.add(obj);
        getSkinPropSkinAProps(index);

        this.modelname = getObjString("modelname");
        this.type = getObjString("type");
        this.body = getObjString("body");
        this.cdmaterials = getObjString("cdmaterials");
        this.sequence = getObjString("sequence");
        this.surfaceprop = getObjString("surfaceprop");
        //this.selfillum = getObjString("selfillum");
        this.collisionModel = getObjString("collisionmodel");
        //this.vtfLocation = getVtfLocation("vtfLocation");
    }
    public String getModelName(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("modelname").toString();
    }
    public String getModelType(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("type").toString();
    }
    public String getModelBody(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("body").toString();
    }
    public String getModelcdMaterials(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("cdmaterials").toString();
    }
    public String[] getModelSequence(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        JSONArray jarr = (JSONArray) jqc.get("sequence");
        JSONObject seq = (JSONObject) jarr.getFirst();
        Object[] objArr = seq.keySet().toArray();
        return new String[]{objArr[0].toString(), (String) seq.get(objArr[0])};
    }
    public String getModelSurfaceProp(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("surfaceprop").toString();
    }
    public String getColisionModel(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("collisionmodel").toString();
    }
    public String[] getCollisionOptions(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        JSONArray jarr = (JSONArray) jqc.get("collisionOptions");
        Object[] objarr = jarr.toArray();
        String[] strarr = new String[objarr.length];
        for(int i = 0; i < objarr.length; i++) {
            strarr[i] = objarr[i].toString();
        }
        return strarr;
    }
    public String[] getModelSkinsA(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        JSONArray jarr = (JSONArray) jqc.get("textureGroup");
        JSONObject tg = (JSONObject) jarr.getFirst();
        JSONArray sArr = (JSONArray) tg.get("skinsA");
        Object[] objarr = sArr.toArray();
        String[] strarr = new String[objarr.length];
        for(int i = 0; i < objarr.length; i++) {
            strarr[i] = objarr[i].toString();
        }
        return strarr;
    }
    public String[] getModelSkinsB(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        JSONArray jarr = (JSONArray) jqc.get("textureGroup");
        JSONObject tg = (JSONObject) jarr.getFirst();
        JSONArray sArr = (JSONArray) tg.get("skinsB");
        //System.out.println("sArr: " + sArr);
        if(sArr != null) {
            Object[] objarr = sArr.toArray();
            String[] strarr = new String[objarr.length];
            for (int i = 0; i < objarr.length; i++) {
                strarr[i] = objarr[i].toString();
            }
            return strarr;
        }
        else return new String[0];
    }
    public String getModelUVMap(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);
        if(jqc.get("UVMap") != null)
            return jqc.get("UVMap").toString();
        else return "";
    }

    public int getIndexByModelName(String name) {
        JSONArray qc = this.modelData.getFirst();
        for(int i = 0; i < qc.size(); i++) {
            JSONObject jqc = (JSONObject) qc.get(i);
            String jname = (String) jqc.get("modelname");
            if(jname.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexByName(String name) {
        JSONArray qc = this.modelData.getFirst();
        for(int i = 0; i < qc.size(); i++) {
            JSONObject jqc = (JSONObject) qc.get(i);
            String jname = (String) jqc.get("modelname");
            if(jname.equals(name)) {
                return i;
            }
        }
        return -1;
    }
    public JSONObject getJsonArrObjs(String name, int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);

        if(jqc.containsKey(name)) {
            JSONArray skinPropsArr = (JSONArray) jqc.get(name);
            JSONObject skinProps = (JSONObject) skinPropsArr.getFirst();
            return skinProps;
        }
        return null;
    }

    /// check each object in modelData, return those with makeUVMap's array of objects
    /// the instructions on how to make the UV Map for the model, else return empty arraylist
    public ArrayList<String[]> getMakeUVMapValues3(int index){
        JSONObject makeUVMapObj = getJsonArrObjs("makeUVMap", index);
        if(makeUVMapObj != null) {
            System.out.println("makeUVMapObj: "+ makeUVMapObj);
            ArrayList<String[]> aarr = new ArrayList<>();

            Object[] jKeyset = makeUVMapObj.keySet().toArray();
            System.out.println("jKeyset: "+ jKeyset);
            for (int i = 0; i < jKeyset.length; i++) {
                //of returned objects, get first object values
                JSONArray row = (JSONArray) makeUVMapObj.get(jKeyset[i]);
                String[] str_arr = new String[]{jKeyset[i].toString(), row.toJSONString()};
                aarr.add(str_arr);
                System.out.println("str_arr: "+ Arrays.toString(str_arr));
            }
            return aarr;
        }
        return new ArrayList<>();
    }

    public String nocull;
    public String alphatest;
    public String color;
    public ArrayList<perSkinProperties> perSkinPropsArr = new ArrayList<>();
    public static class perSkinProperties {
        public String texture;
        public String nocull;
        public String alphatest;
        public String color;
        public String surfaceprop;
        public String selfillum;
        public String animatedTextureVar;
        public String animatedTextureFrameNumVar;
        public String animatedTextureFrameRate;
    }

    public String animatedTextureVar;
    public String animatedTextureFrameNumVar;
    public String animatedTextureFrameRate;

    public void getSkinPropSkinAProps(int index) {
        JSONArray qc = modelData.getFirst();
        JSONObject jqc = (JSONObject) qc.get(index);

        if(jqc.containsKey("skinProperties")) {
            JSONArray arr_sp = (JSONArray) jqc.get("skinProperties");
            JSONObject jo_sp = (JSONObject) arr_sp.getFirst();
            System.out.println("jo_sp keyset: "+jo_sp.keySet());

            if (jo_sp.containsKey("skinA")) {
                JSONArray arr_skinA = (JSONArray) jo_sp.get("skinA");
                JSONObject jo_skinA = (JSONObject) arr_skinA.getFirst();

                if (jo_skinA.containsKey("nocull")) {
                    this.nocull = jo_skinA.get("nocull").toString();
                }
                if (jo_skinA.containsKey("alphatest")) {
                    this.alphatest = jo_skinA.get("alphatest").toString();
                }
                if (jo_skinA.containsKey("color")) {
                    this.color = jo_skinA.get("color").toString();
                }
                if (jo_skinA.containsKey("surfaceprop")) {
                    this.surfaceprop = jo_skinA.get("surfaceprop").toString();
                }
                if (jo_skinA.containsKey("selfillum")) {
                    this.selfillum = jo_skinA.get("selfillum").toString();
                }
                if (jo_skinA.containsKey("animatedTextureVar")) {
                    this.animatedTextureVar = jo_skinA.get("animatedTextureVar").toString();
                }
                if (jo_skinA.containsKey("animatedTextureFrameNumVar")) {
                    this.animatedTextureFrameNumVar = jo_skinA.get("animatedTextureFrameNumVar").toString();
                }
                if (jo_skinA.containsKey("animatedTextureFrameRate")) {
                    this.animatedTextureFrameRate = jo_skinA.get("animatedTextureFrameRate").toString();
                }
            }
            if (jo_sp.containsKey("perSkin")) {
                JSONArray arr_perSkin = (JSONArray) jo_sp.get("perSkin");
                JSONObject jo_perSkin = (JSONObject) arr_perSkin.getFirst();
                Object[] textures = jo_perSkin.keySet().toArray();

                for(int i=0;i<textures.length;i++) {
                    System.out.println("true");

                    perSkinProperties perSkins = new perSkinProperties();
                    JSONArray arr_textures = (JSONArray) jo_perSkin.get(textures[i].toString());
                    JSONObject jo_textures = (JSONObject) arr_textures.getFirst();

                    perSkins.texture = textures[i].toString();

                    if (jo_textures.containsKey("nocull")) {
                        perSkins.nocull = jo_textures.get("nocull").toString();
                    }
                    if (jo_textures.containsKey("alphatest")) {
                        perSkins.alphatest = jo_textures.get("alphatest").toString();
                    }
                    if (jo_textures.containsKey("color")) {
                        perSkins.color = jo_textures.get("color").toString();
                    }
                    if (jo_textures.containsKey("surfaceprop")) {
                        perSkins.surfaceprop = jo_textures.get("surfaceprop").toString();
                    }
                    if (jo_textures.containsKey("selfillum")) {
                        perSkins.selfillum = jo_textures.get("selfillum").toString();
                    }
                    if (jo_textures.containsKey("animatedTextureVar")) {
                        perSkins.animatedTextureVar = jo_textures.get("animatedTextureVar").toString();
                    }
                    if (jo_textures.containsKey("animatedTextureFrameNumVar")) {
                        perSkins.animatedTextureFrameNumVar = jo_textures.get("animatedTextureFrameNumVar").toString();
                    }
                    if (jo_textures.containsKey("animatedTextureFrameRate")) {
                        perSkins.animatedTextureFrameRate = jo_textures.get("animatedTextureFrameRate").toString();
                    }
                    perSkinPropsArr.add(perSkins);
                }
            }
        }
    }
    public String getObjString(String name) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jobj = (JSONObject) qc.get(this.index);

        if(jobj.containsKey(name)) {
            return jobj.get(name).toString();

        }
        return "";
    }
    public String getVtfLocation(String name) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jobj = (JSONObject) qc.get(this.index);

        // name of texture
        if(jobj.containsKey("vtfLocation")) {
            JSONArray vtfLoc = (JSONArray) jobj.get("vtfLocation");
            JSONObject vtfLocObj = (JSONObject) vtfLoc.getFirst();
            //System.out.println("vtfLocObj: "+vtfLocObj);
            if (vtfLocObj.containsKey(name)) {
                return vtfLocObj.get(name).toString();
            }
        }
        return "";
    }
    public String[] getVtfLocations(int index) {
        JSONArray qc = this.modelData.getFirst();
        JSONObject jobj = (JSONObject) qc.get(index);

        if(jobj.containsKey("vtfLocation")) {
            JSONArray vtfLoc = (JSONArray) jobj.get("vtfLocation");
            JSONObject vtfLocObj = (JSONObject) vtfLoc.getFirst();
            //System.out.println("vtfLocObj: "+vtfLocObj);
            Object[] keys = vtfLocObj.keySet().toArray();
            System.out.println(Arrays.toString(keys) + "keys <-i: "+index);
            String[] values = new String[keys.length];
            for(int i=0;i<keys.length;i++) {
                values[i] = "/" + vtfLocObj.get(keys[i]).toString();
            }
            System.out.println("values: "+ Arrays.toString(values));
            return values;
        }
        return new String[0];
    }

    public void getMakeUVMapValues2(int index){
        JSONObject getjsra = getJsonArrObjs("makeUVMap", index);
        Object[] keys = getjsra.keySet().toArray();
        System.out.println(keys[0]);
        Object[] objArr = new Object[keys.length];
        ArrayList<String> blah = new ArrayList<>();
        ArrayList<String[]> meh = new ArrayList<>();

        for(int i=0;i< keys.length;i++) {
            String[] obja = getjsra.get(keys[i]).toString()
                    .replace("[", "").replace("]","")
                    .replace("\"","").split("\",\"");
            meh.add(obja);
            blah.add(keys[i].toString());
        }
        System.out.println("objArr: "+objArr[0]);
    }

    public ArrayList<Object[]> getMakeUVMapValues4(int index){
        JSONObject getjsra = getJsonArrObjs("makeUVMap", index);
        ArrayList<Object[]> aarr = new ArrayList<>();
        Object[] jar = getjsra.keySet().toArray();

        for (int i = 0; i < jar.length; i++) {
            //of returned objects, get first object values
            JSONArray row = (JSONArray) getjsra.get(jar[i]);
            Object[] jar2 = new Object[]{jar[i], row};
            aarr.add(jar2);
        }
        return aarr;
    }
}

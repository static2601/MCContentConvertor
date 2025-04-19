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
    public String collisionModel;

    /** special location of vtf. block/ = "" (default, not necessary for json), entity = entity/, etc.
     * if not ended in /, will get by name, else directory */
    //public String[] vtfLocation;
    public String collisionOptions;
    public String textureGroup;

    public getJsonData() throws IOException, ParseException {
        // parsing file "JSONExample.json"
        JSONArray obj = (JSONArray) new JSONParser().parse(
                new FileReader("src/MCContentConvertor/ModelData.json"));
        modelData.add(obj);
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public getJsonData(int index) throws IOException, ParseException {
        this.index = index;
        JSONArray obj = (JSONArray) new JSONParser().parse(
                new FileReader("src/MCContentConvertor/ModelData.json"));
        this.modelData.add(obj);
        getSkinPropSkinAProps(index);

        this.modelname = getObjString("modelname");
        this.type = getObjString("type");
        this.body = getObjString("body");
        this.cdmaterials = getObjString("cdmaterials");
        this.sequence = getObjString("sequence");
        this.surfaceprop = getObjString("surfaceprop");
        this.collisionModel = getObjString("collisionmodel");
        //this.vtfLocation = getVtfLocation("vtfLocation");
    }
    public String getModelName(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("modelname").toString();
    }
    public String getModelType(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("type").toString();
    }
    public String getModelBody(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("body").toString();
    }
    public String getModelcdMaterials(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("cdmaterials").toString();
    }
    public String[] getModelSequence(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        JSONArray jarr = (JSONArray) jqc.get("sequence");
        JSONObject seq = (JSONObject) jarr.get(0);
        Object[] objArr = seq.keySet().toArray();
        return new String[]{objArr[0].toString(), (String) seq.get(objArr[0])};
    }
    public String getModelSurfaceProp(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("surfaceprop").toString();
    }
    public String getColisionModel(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        return jqc.get("collisionmodel").toString();
    }
    public String[] getCollisionOptions(int index) {
        JSONArray qc = this.modelData.get(0);
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
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        JSONArray jarr = (JSONArray) jqc.get("textureGroup");
        JSONObject tg = (JSONObject) jarr.get(0);
        JSONArray sArr = (JSONArray) tg.get("skinsA");
        Object[] objarr = sArr.toArray();
        String[] strarr = new String[objarr.length];
        for(int i = 0; i < objarr.length; i++) {
            strarr[i] = objarr[i].toString();
        }
        return strarr;
    }
    public String[] getModelSkinsB(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        JSONArray jarr = (JSONArray) jqc.get("textureGroup");
        JSONObject tg = (JSONObject) jarr.get(0);
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
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        if(jqc.get("UVMap") != null)
            return jqc.get("UVMap").toString();
        else return "";
    }
//    public JSONArray getPerSkinProps(int index) {
//        JSONArray qc = this.modelData.get(0);
//        JSONObject jqc = (JSONObject) qc.get(index);
//        JSONArray skinProps = (JSONArray) jqc.get("skinProperties");
//        JSONArray perSkin = (JSONArray) skinProps.get(1);
//        if(!perSkin.isEmpty()) return perSkin;
//        else return new JSONArray();
//    }
//    public JSONArray getPerSkinByModelName(String name) {
//        JSONArray qc = this.modelData.get(0);
//        for(int i = 0; i < qc.size(); i++) {
//            JSONObject jqc = (JSONObject) qc.get(i);
//            String jname = (String) jqc.get("modelname");
//            if(jname.equals(name)) {
//                return getPerSkinProps(i);
//            }
//        }
//        return new JSONArray();
//    }
//    public JSONObject getDataByModelName(String name) {
//        JSONArray qc = this.modelData.get(0);
//        for(int i = 0; i < qc.size(); i++) {
//            JSONObject jqc = (JSONObject) qc.get(i);
//            String jname = (String) jqc.get("modelname");
//            if(jname.equals(name)) {
//                return jqc;
//                //return getPerSkinProps(i);
//            }
//        }
//        return new JSONObject();
//    }
    public int getIndexByModelName(String name) {
        JSONArray qc = this.modelData.get(0);
        for(int i = 0; i < qc.size(); i++) {
            JSONObject jqc = (JSONObject) qc.get(i);
            String jname = (String) jqc.get("modelname");
            if(jname.equals(name)) {
                return i;
            }
        }
        return -1;
    }
//    public JSONObject getSkinProperties(int index) {
//        JSONArray qc = this.modelData.get(0);
//        JSONObject jqc = (JSONObject) qc.get(index);
//        JSONArray skinPropsArr = (JSONArray) jqc.get("skinProperties");
//        JSONObject skinProps = (JSONObject) skinPropsArr.get(0);
//        return skinProps;
//    }
//    public ArrayList<String[]> getSkinAProperties(int index) {
//        JSONArray qc = this.modelData.get(0);
//        JSONObject jqc = (JSONObject) qc.get(index);
//        JSONArray skinPropsArr = (JSONArray) jqc.get("skinProperties");
//        JSONObject skinProps = (JSONObject) skinPropsArr.get(0);
//        JSONArray skinArr = (JSONArray) skinProps.get("skinA");
//
//        JSONObject skinsA = (JSONObject) skinArr.get(0);
//        Object[] skinPropsArr2 = skinsA.keySet().toArray();
//        ArrayList<String[]> props = new ArrayList<>();
//        String[] str = new String[2];
//        for(int i = 0; i < skinsA.keySet().size(); i++) {
//            String prop = skinPropsArr2[i].toString();
//            String val = skinsA.get(prop).toString();
//
//             str = new String[]{prop, val};
//             props.add(str);
//        }
//        return props;
//    }
//    public ArrayList<JSONObject> getSkinAPropertiesArray(int index) {
//        JSONArray qc = this.modelData.get(0);
//        JSONObject jqc = (JSONObject) qc.get(index);
//        JSONArray skinPropsArr = (JSONArray) jqc.get("skinProperties");
//        JSONObject skinProps = (JSONObject) skinPropsArr.get(0);
//        JSONArray skinArr = (JSONArray) skinProps.get("skinA");
//
//        JSONObject skinsA = (JSONObject) skinArr.get(0);
//        Object[] skinPropsArr2 = skinsA.keySet().toArray();
//        ArrayList<JSONObject> props = new ArrayList<>();
//        String[] str = new String[2];
//        for(int i = 0; i < skinsA.keySet().size(); i++) {
//            String prop = skinPropsArr2[i].toString();
//            String val = skinsA.get(prop).toString();
//
//            str = new String[]{prop, val};
//            //props.add(str);
//        }
//        return props;
//    }
//    public JSONObject getPerSkinProperties(int index) {
//        JSONObject perSkin = getSkinProperties(index);
//        JSONArray perSkinArr = (JSONArray) perSkin.get("perSkin");
//        if(perSkinArr != null)
//            return (JSONObject) perSkinArr.get(0);
//        else return new JSONObject();
//    }
//    public ArrayList<JSONObject> getPerSkinProps2(int index) {
//        JSONObject perSkinProps = this.getPerSkinProperties(index);
//        Object[] perSkinPropsSkin = perSkinProps.keySet().toArray();
//        ArrayList<JSONObject> matProps = new ArrayList<>();
//        for(int i = 0; i < perSkinProps.keySet().size(); i++) {
//            Object skin = perSkinPropsSkin[i];
//            ArrayList<String[]> properties = new ArrayList<>();
//
//            JSONArray skinProps = (JSONArray) perSkinProps.get(skin);
//            JSONObject skinPropsArr3 = (JSONObject) skinProps.get(0);
//            Object[] objSkinProps = skinPropsArr3.keySet().toArray();
//            for(int x = 0; x < skinPropsArr3.keySet().size(); x++) {
//
//                Object skinProp = objSkinProps[x];
//                JSONObject ggg = (JSONObject) ((JSONArray) perSkinProps.get(skin)).get(0);
//                String[] strArr = new String[]{skinProp.toString(), ggg.get(skinProp).toString()};
//                properties.add(strArr);
//            }
//            JSONObject obj = new JSONObject();
//            obj.put(skin, properties);
//            matProps.add(obj);
//        }
//        return matProps;
//    }
    //private int modelIndex = 0;
    public int getIndexByName(String name) {
        JSONArray qc = this.modelData.get(0);
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
        JSONArray qc = this.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);
        if(jqc.containsKey(name)) {
            JSONArray skinPropsArr = (JSONArray) jqc.get(name);
            JSONObject skinProps = (JSONObject) skinPropsArr.get(0);
            return skinProps;
        }
        return null;
    }
//    public JSONObject getJsonArrObjs2(String name, JSONObject jobj) {
//        JSONArray skinPropsArr = (JSONArray) jobj.get(name);
//        JSONObject skinProps = (JSONObject) skinPropsArr.get(0);
//        return skinProps;
//    }
//    public ArrayList<Object[]> getMakeUVMapValues(int index){
//        JSONObject getjsra = getJsonArrObjs("makeUVMap", index);
//        Object[] jar = getjsra.keySet().toArray();
//        JSONArray row1 = (JSONArray) getjsra.get(jar[0]);
//        //JSONArray row2 = (JSONArray) getjsra.get(jar[1]);
//
//        ArrayList<Object[]> aarr = new ArrayList<>();
//        aarr.add(row1.toArray());
//        //aarr.add(row2.toArray());
//        return aarr;
//    }
    public ArrayList<Object[]> getMakeUVMapValues3(int index){
        JSONObject getjsra = getJsonArrObjs("makeUVMap", index);
        if(getjsra != null) {
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
        public String animatedTextureVar;
        public String animatedTextureFrameNumVar;
        public String animatedTextureFrameRate;
    }

    public String animatedTextureVar;
    public String animatedTextureFrameNumVar;
    public String animatedTextureFrameRate;

//    public void getSkin11(int index) {
//        JSONArray qc = modelData.get(0);
//        JSONObject jqc = (JSONObject) qc.get(index);
//
//        if (jqc.containsKey("skinProperties")) {
//            JSONArray arr_sp = (JSONArray) jqc.get("skinProperties");
//            JSONObject jo_sp = (JSONObject) arr_sp.get(0);
//        }
//    }

    public void getSkinPropSkinAProps(int index) {
        JSONArray qc = modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(index);

        if(jqc.containsKey("skinProperties")) {
            JSONArray arr_sp = (JSONArray) jqc.get("skinProperties");
            JSONObject jo_sp = (JSONObject) arr_sp.get(0);
            System.out.println("jo_sp keyset: "+jo_sp.keySet());

            if (jo_sp.containsKey("skinA")) {
                JSONArray arr_skinA = (JSONArray) jo_sp.get("skinA");
                JSONObject jo_skinA = (JSONObject) arr_skinA.get(0);

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
                JSONObject jo_perSkin = (JSONObject) arr_perSkin.get(0);
                Object[] textures = jo_perSkin.keySet().toArray();

                for(int i=0;i<textures.length;i++) {
                    System.out.println("true");

                    perSkinProperties perSkins = new perSkinProperties();
                    JSONArray arr_textures = (JSONArray) jo_perSkin.get(textures[i].toString());
                    JSONObject jo_textures = (JSONObject) arr_textures.get(0);

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
        JSONArray qc = this.modelData.get(0);
        JSONObject jobj = (JSONObject) qc.get(this.index);

        if(jobj.containsKey(name)) {
            return jobj.get(name).toString();

        }
        return "";
    }
    public String getVtfLocation(String name) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jobj = (JSONObject) qc.get(this.index);

        // name of texture
        if(jobj.containsKey("vtfLocation")) {
            JSONArray vtfLoc = (JSONArray) jobj.get("vtfLocation");
            JSONObject vtfLocObj = (JSONObject) vtfLoc.get(0);
            //System.out.println("vtfLocObj: "+vtfLocObj);
            if (vtfLocObj.containsKey(name)) {
                return vtfLocObj.get(name).toString();
            }
        }
        return "";
    }
    public String[] getVtfLocations(int index) {
        JSONArray qc = this.modelData.get(0);
        JSONObject jobj = (JSONObject) qc.get(index);

        if(jobj.containsKey("vtfLocation")) {
            JSONArray vtfLoc = (JSONArray) jobj.get("vtfLocation");
            JSONObject vtfLocObj = (JSONObject) vtfLoc.get(0);
            //System.out.println("vtfLocObj: "+vtfLocObj);
            Object[] keys = vtfLocObj.keySet().toArray();
            System.out.println(Arrays.toString(keys) + "keys <----------------------------------i: "+index);
            String[] values = new String[keys.length];
            for(int i=0;i<keys.length;i++) {
                values[i] = "/" + vtfLocObj.get(keys[i]).toString();
            }
            System.out.println("values: "+ Arrays.toString(values));
            return values;
        }
        return new String[0];
    }

    //public skinProperties skinProperties = new skinProperties();
//    public getJsonData(int index) {
//        this.skinProperties = new skinProperties();
//    }

//    public class skinProperties {
//
//        public skinA skinA;
//        public skinProperties() {
//            //this.name = name;
//            this.skinA = new skinA();
//        }
//
//        public class skinA {
//            //private String name;
//            public proxies proxies;
//            public String nocull;
//            public String alphatest;
//
//            public skinA() {
//                //this.name = name;
//                //this.proxies = new proxies(name);
//                getData();
//            }
//            public void getData() {
//                JSONArray qc = modelData.get(0);
//                JSONObject jqc = (JSONObject) qc.get(index);
//
//                JSONArray arr_sp = (JSONArray) jqc.get("skinProperties");
//                JSONObject jo_sp = (JSONObject) arr_sp.get(0);
//                JSONArray arr_skinA = (JSONArray) jo_sp.get("skinA");
//                JSONObject jo_skinA = (JSONObject) arr_skinA.get(0);
//
//                if(jo_skinA.containsKey("nocull")) {
//                    this.nocull = jo_skinA.get("nocull").toString();
//                }
//                if(jo_skinA.containsKey("alphatest")) {
//                    this.alphatest = jo_skinA.get("alphatest").toString();
//                }
//                if(jo_skinA.containsKey("proxies")) {
//                    this.proxies = new proxies();
//                }
//            }
//
//            public class proxies {
//                public animatedTextures animatedTextures;
//                public proxies() {
//                    this.animatedTextures = new animatedTextures();
//                }
//
//                public class animatedTextures {
//
//                    public String animatedTextureVar;
//                    public String animatedTextureFrameNumVar;
//                    public String animatedTextureFrameRate;
//
//                    public animatedTextures() {
//                        //this.animatedTextureVar = name;
//                        //this.animatedTextureVar = name;
//                    }
//                }
//            }
//        }
//    }


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


        //Object content = getjsra.get(keys[0]);
        System.out.println("objArr: "+objArr[0]);
        //System.out.println("blah: "+ blah.get(0)[0]);
        //System.out.println("content: "+content);






//        ArrayList<ArrayList<Object>> aarr = new ArrayList<>();
//        Object[] jar = getjsra.keySet().toArray();
//        //ArrayList<String> ArstR = new ArrayList<>();
//        JSONObject jobj = new JSONObject();
//        for (int i = 0; i < jar.length; i++) {
//            //of returned objects, get first object values
//
//            ArrayList<Object> list = new ArrayList<>();
//            list.add(jar[i]);
//
//            Object[] row = (Object[]) getjsra.get(jar[i]);
//
//            list.add(row);
//
//            //Object[] jar2 = new Object[]{jar[i]};
//            aarr.add(list);
//        }
        //return aarr;
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

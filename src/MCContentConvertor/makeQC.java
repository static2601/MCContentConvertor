package MCContentConvertor;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

import static MCContentConvertor.GUIStart.*;
import static MCContentConvertor.Pathnames.DEBUG;

public class makeQC {

    private final ArrayList<getJsonData.perSkinProperties> perSkinProperties2;
    private String SMDsPath = "SMDs/";
    private final String modelPath = modelsDir + "_"+ mdlUnits +"/";
    private final String VTFSMATDIR = GAMEDIR + "/materials/"+ TEXTUREPACK +"/";

    public String materialsPath = TEXTUREPACK + "/minecraft_prop_materials/";
    public String modelName;
    public String type;
    public String body;
    public String cdMaterials;
    public String[] sequence;
    public String surfaceProp;
    public String collisionModel;
    public String[] collisionOptions;
    public String UVMap;

    public String nocull;
    public String alphatest;
    public String color;
    public String surfaceprop;
    public String selfillum;
    public String animatedTextureVar;
    public String animatedTextureFrameNumVar;
    public String animatedTextureFrameRate;
    public String[] vtfLocation;

    public getJsonData data = new getJsonData();

    public ArrayList<String[]> skinAProperties;
    public ArrayList<JSONObject> perSkinProperties;
    public String[] skinsA;
    public String[] skinsB;
    public StringBuilder sb = new StringBuilder();

    public makeQC(int index) throws IOException, ParseException {
        getJsonData data = new getJsonData(index);
        this.data = data;
        this.modelName = data.modelname;
        this.type = data.type;
        this.body = data.body;
        this.cdMaterials = data.cdmaterials;
        this.sequence = data.getModelSequence(index);
        this.surfaceProp = data.getModelSurfaceProp(index);
        this.collisionModel = data.collisionModel;
        this.collisionOptions = data.getCollisionOptions(index);
        this.UVMap = data.getModelUVMap(index);

        this.nocull = data.nocull;
        this.alphatest = data.alphatest;
        this.color = data.color;
        this.surfaceprop = data.surfaceprop;
        this.selfillum = data.selfillum;
        this.animatedTextureVar = data.animatedTextureVar;
        this.animatedTextureFrameNumVar = data.animatedTextureFrameNumVar;
        this.animatedTextureFrameRate = data.animatedTextureFrameRate;

        //this.skinAProperties = data.getSkinAProperties(index);
        //this.perSkinProperties = data.getPerSkinProps2(index);
        this.perSkinProperties2 = data.perSkinPropsArr;
        this.skinsA = data.getModelSkinsA(index);
        this.skinsB = data.getModelSkinsB(index);

    }

    public StringBuilder makeQCFile() {
        if(this.modelName.equals("Template"))
            return new StringBuilder();

        StringBuilder sb = new StringBuilder();
        sb.append("$").append(this.type).append("\n");
        sb.append("$modelname").append("\t\"").append(this.modelPath)
                .append(this.modelName).append("\"\n");

        sb.append("$scale").append("\t\"").append(mdlScale).append("\"\n");
        sb.append("$body").append("\t").append("\"Body\" \"").append(this.SMDsPath).append(this.body).append("\"\n");
        sb.append("$cdmaterials").append("\t\"").append(this.materialsPath).append(this.cdMaterials).append("/\"\n");
        sb.append("$sequence").append(" ").append(this.sequence[0]).append(" \"").append(this.SMDsPath).append(this.sequence[1]).append("\"\n");
        sb.append("$surfaceprop").append("\t\"").append(this.surfaceProp).append("\"\n");
        sb.append("$collisionmodel").append("\t\"").append(this.SMDsPath).append(this.collisionModel).append("\"\n");
        sb.append("{").append("\n");
        for (String collisionOption : this.collisionOptions) {
            sb.append("\t").append(collisionOption).append("\n");
        }
        sb.append("}").append("\n");
        sb.append("$texturegroup skins").append("\n");
        sb.append("{").append("\n");

        if(this.skinsA.length > 0) {
            for(int i = 0; i < this.skinsA.length; i++) {
                String skinB = "";
                if(this.skinsB.length > 0) {
                    if (i < this.skinsB.length) skinB = " \"" + this.skinsB[i] + "\"";
                    System.out.println("skinsB.length: "+ this.skinsB.length);
                    System.out.println("skinB: "+ skinB+ ", i: "+ i);
                }
                //if(skinsB[i] != null) skinB = "\" \""+ skinsB[i];
                sb.append("\t").append("{\t\"").append(this.skinsA[i]).append("\"").append(skinB).append("\t\t}").append(" // skin ").append(i).append("\n");
            }
        }
        sb.append("}").append("\n");
        return sb;
    }

    public String[] makeVMT() throws IOException {
        String[] vmts = new String[skinsA.length+skinsB.length];
        int i = 0;
        for(String skin : skinsA) {

            sb = new StringBuilder();
            sb.append("VertexlitGeneric").append("\n");
            sb.append("{").append("\n");
            sb.append("\t\"").append("$basetexture").append("\" \"").append(this.materialsPath)
                    .append(this.cdMaterials).append("/").append(skin).append("\"\n");

            if(!checkForPerSkins2(skin).isEmpty())
                sb.append(checkForPerSkins2(skin));
            else sb.append(checkForSkinProps(skin));
            
            if(skin.contains("redstone_dust_line"))
                sb.append(doRedstoneDustColor(skin));

            sb.append("}").append("\n");
            WriteVMTFile(sb.toString(), TEXTUREPACK, skin);
            vmts[i] = sb.toString();
            //System.out.println("i: " + i + ", "+vmts[i]);
            i++;
        }
        //vmts = new String[skinsA.length];
        //i = 0;
        for (String skin : skinsB) {
            if (!skin.isEmpty()) {
                sb = new StringBuilder();
                sb.append("VertexlitGeneric").append("\n");
                sb.append("{").append("\n");
                sb.append("\t\"").append("$basetexture").append("\" \"").append(this.materialsPath)
                        .append(this.cdMaterials).append("/").append(skin).append("\"\n");

                if (!checkForPerSkins2(skin).isEmpty())
                    sb.append(checkForPerSkins2(skin));
                else sb.append(checkForSkinProps(skin));

                sb.append("}").append("\n");
                    WriteVMTFile(sb.toString(), TEXTUREPACK, skin);
                vmts[i] = sb.toString();
                    //System.out.println("i: " + i + ", "+vmts[i]);
                i++;
            }
        }
        return vmts;
    }

    public String doRedstoneDustColor(String skin) {
        // do this for redstone dust
        //keep all vmts referencing same vtf
        //gradually change color to be lighter red
       int i = Integer.parseInt(skin.replace("redstone_dust_line", ""));
            //point to redstone_dust_line0 vtf for all vmts
            //n = skins.get(0);
            //15 * 16 skins, max color of 240 - 15 each
            int[] color = {0, 0, 0};

            if(i > 4) color[0] = (21*(i+1)); //gradually less color
            else color[0] = 75; //minimum color

            return "\t\"$color2\" \"{"+color[0]+" "+color[1]+" "+ color[2] +"}\" \n";
    }

    public String checkForSkinProps(String skin) {
        StringBuilder sb = new StringBuilder("");

        if(this.nocull != null) sb.append("\t\"").append("$nocull\" \"").append(this.nocull).append("\"\n");
        if(this.alphatest != null) sb.append("\t\"").append("$alphatest\" \"").append(this.alphatest).append("\"\n");
        if(this.color != null) sb.append("\t\"").append("$color\" \"").append(this.color).append("\"\n");
        if(this.surfaceprop != null) sb.append("\t\"").append("$surfaceprop\" \"").append(this.surfaceprop).append("\"\n");
        if(this.selfillum != null) sb.append("\t\"").append("$selfillum\" \"").append(this.selfillum).append("\"\n");

        if(this.animatedTextureVar != null) {
            sb.append("\t").append("Proxies").append("\n");
            sb.append("\t").append("{").append("\n");
            sb.append("\t\t").append("AnimatedTexture").append("\n");
            sb.append("\t\t").append("{").append("\n");

            sb.append("\t\t\t\"").append("$animatedTextureVar\" \"")
                    .append(this.animatedTextureVar).append("\"\n");
            if(this.animatedTextureFrameNumVar != null) sb.append("\t\t\t\"").append("$animatedTextureFrameNumVar\" \"")
                    .append(this.animatedTextureFrameNumVar).append("\"\n");
            if(this.animatedTextureFrameRate != null) sb.append("\t\t\t\"").append("$animatedTextureFrameRate\" \"")
                    .append(this.animatedTextureFrameRate).append("\"\n");

            sb.append("\t\t").append("}").append("\n");
            sb.append("\t").append("}").append("\n");
        }
        return sb.toString();
    }

    public String checkForPerSkins2(String skin) {
        StringBuilder sb = new StringBuilder();
        for(int x = 0; x < this.perSkinProperties2.size(); x++) {
            String skinz = this.perSkinProperties2.get(x).texture;
            getJsonData.perSkinProperties prop = this.perSkinProperties2.get(x);

            if(skinz.equals(skin)) {
                if(prop.nocull != null) sb.append("\t").append("\"$nocull\" \"").append(prop.nocull).append("\"\n");
                if(prop.alphatest != null) sb.append("\t").append("\"$alphatest\" \"").append(prop.alphatest).append("\"\n");
                if(prop.color != null) sb.append("\t").append("\"$color\" \"").append(prop.color).append("\"\n");
                if(prop.surfaceprop != null) sb.append("\t").append("\"$surfaceprop\" \"").append(prop.surfaceprop).append("\"\n");
                if(prop.selfillum != null) sb.append("\t\"").append("$selfillum\" \"").append(prop.selfillum).append("\"\n");

                if(prop.animatedTextureVar != null) {
                    sb.append("\t").append("Proxies").append("\n");
                    sb.append("\t").append("{").append("\n");
                    sb.append("\t\t").append("AnimatedTexture").append("\n");
                    sb.append("\t\t").append("{").append("\n");

                    if(prop.animatedTextureVar != null) sb.append("\t").append("\"$animatedTextureVar\" \"")
                            .append(prop.animatedTextureVar).append("\"\n");
                    if(prop.animatedTextureFrameNumVar != null) sb.append("\t").append("\"$animatedTextureFrameNumVar\" \"")
                            .append(prop.animatedTextureFrameNumVar).append("\"\n");
                    if(prop.animatedTextureFrameRate != null) sb.append("\t").append("\"$animatedTextureFrameRate\" \"")
                            .append(prop.animatedTextureFrameRate).append("\"\n");

                    sb.append("\t\t").append("}").append("\n");
                    sb.append("\t").append("}").append("\n");
                }
                break;
            }
        }
        return sb.toString();
    }

    public void WriteQCFile(String toWrite, String qcPath, String name) {

        if(!qcPath.endsWith("/")) qcPath += "/";
        //String ModelMatPath = qcPath;
        boolean testwriteOnly = false;

        if(!testwriteOnly) {
            try {
                File theDir = new File(qcPath);
                if (!theDir.exists()){
                    theDir.mkdirs();
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(qcPath + name + ".qc"));

                writer.write(toWrite);
                writer.close();

            } catch (IOException e) {

                e.printStackTrace();
            }
        } else {
            //System.out.println("Written Content: "+ qcPath + name + ".qc" +"\n");
            //System.out.println("if statement Folder location: "+ matPath);
            //System.out.println(toWrite);
        }
    }

    protected static boolean PrepareCompiler(String studioMdlPath, String gameFolder, String QCName, String QCPath) {

        //path to compiler
        //String fileName = "compiler.bat";
        String fileName = QCName.replace(".qc","")+"_compiler.bat";
        String q = "\"";
        String batContent =
                q+ studioMdlPath +q
                + " -game "+q+ gameFolder + q
                + " -nop4"
                + " -quiet"
                + " " + QCName;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(QCPath + fileName));
            writer.write(batContent);
            writer.close();
            System.out.println("Written Content \n"+ QCPath + fileName +"\n");
            System.out.println("batContent:\n"+ batContent);
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    //ArrayList<String> failedCompiles = new ArrayList<>();
    public int CompileModel(String QCPath, String QCName) throws IOException, InterruptedException {

        String studioMdlPath = new File(GAMEDIR).getParent() + "/bin" + "/studiomdl";
        int code = 1;
        //Create bat file
        if(PrepareCompiler(studioMdlPath, GAMEDIR, QCName, QCPath)) {

            // show or suppress model compile results
            String showOutput = "/b";
            if (DEBUG) showOutput = "";

            // Execute bat command
            String[] args = {"cmd.exe", "/c", "start", showOutput, QCName.replace(".qc","")+"_compiler.bat"};
            code = BatchRunner.runBat(args, QCPath);

            if(code > 0) {
                System.out.println("ERROR: p/waitFor returned " + code);
            }
            else {
                System.out.println("finished compiling model: "+ QCPath + QCName);
            }

            //System.out.println();//needs to wait for each to
            //finish before doing the next one

        }
        else {
            System.out.println("Compiler failed at PrepareCompiler"
                    + "("
                    + " studioMdlPath:"+ studioMdlPath
                    +", gameFolder: "+ GAMEDIR
                    +", QCName: "+ QCName
                    +", QCPath: "+ QCPath
                    + ")"
            );
            System.exit(0);
        }
        return code;
    }

    public void WriteVMTFile(String toWrite, String matPath, String skin) throws IOException {

        String ModelMatPath = GAMEDIR +"/materials/"+ TEXTUREPACK +"/minecraft_prop_materials/"+ this.cdMaterials + "/";
        // get vtf location and copy from to matPath
        // if vtfLocation is not null, get vtf from there
        // if path ends with /, add skin name, else use provided name
        String fromPath = VTFSMATDIR + "/";
        //if(this.vtfLocation != null)
        // check for vtf first at vtfLocation for vtf,
        // if vtflocation ends with /, search for all textures?
        // if vtflocation ends with name, search only for that name?

        String vtfLoc = data.getVtfLocation(skin);
        System.out.println("vtfLoc1: "+vtfLoc);
        // if skin is found as the key, return value, else ""
        if(!vtfLoc.isEmpty()) {
            fromPath += vtfLoc + skin + ".vtf";
//            if(vtfLoc.endsWith("/"))
//                fromPath += vtfLoc + skin + ".vtf";
//            else fromPath += vtfLoc + ".vtf";
        } else {
            // check if key is "all" meaning set all textures to key path
            vtfLoc = data.getVtfLocation("all");
            System.out.println("vtfLoc2: "+vtfLoc);
            if(!vtfLoc.isEmpty()) {
                // if ends with name rather then "/"
                // set all skins texture location, eg redstone_dust_line0
                if(!vtfLoc.endsWith("/"))
                    fromPath += vtfLoc + ".vtf";
                else {
                    // set all to directory of location
                    fromPath += vtfLoc + skin + ".vtf";
                }
            } else {
                // else nothing found treat as normal,
                // look in block/ for textures
                fromPath += skin + ".vtf";
            }
        }

        File from = new File(fromPath);
        // should prepend gameDir so it goes into /tf
        File to = new File(ModelMatPath + skin + ".vtf");
        System.out.println("from: "+ from);
        System.out.println("to: "+ to);
        TextureGetter.copyFile(from, to);

        try {
            File theDir = new File(ModelMatPath);
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(ModelMatPath + skin + ".vmt"));

            writer.write(toWrite);
            System.out.println("Written Content: " + ModelMatPath + skin + "\n");
            writer.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String checkForAnims() {
        StringBuilder sb = new StringBuilder();
        System.out.println("test run: "+data.animatedTextureVar);
        if(data.animatedTextureVar != null) {
            System.out.println("running animvmts");
            sb.append("\t").append("Proxies").append("\n");
            sb.append("\t").append("{").append("\n");
            sb.append("\t\t").append("AnimatedTexture").append("\n");
            sb.append("\t\t").append("{").append("\n");
            sb.append("\t\t\t").append("animatedTextureVar").append(" \"$").append(data.animatedTextureVar).append("\"\n");
            sb.append("\t\t\t").append("animatedTextureFrameNumVar").append(" \"$").append(data.animatedTextureFrameNumVar).append("\"\n");
            sb.append("\t\t\t").append("animatedTextureFrameRate").append(" \"$").append(data.animatedTextureFrameRate).append("\"\n");
            sb.append("\t\t").append("}").append("\n");
            sb.append("\t").append("}").append("\n");
            return sb.toString();
        } else return "";
    }

    public String checkForPerSkins(String skin) {
        StringBuilder sb = new StringBuilder();
        for(int x = 0; x < perSkinProperties.size(); x++) {
            String skinz = perSkinProperties.get(x).keySet().toArray()[0].toString();
            if(skinz.equals(skin)) {
                JSONObject sp = perSkinProperties.get(x);
                ArrayList<String[]> jj = (ArrayList<String[]>) sp.get(sp.keySet().toArray()[0]);
                jj.forEach(d -> {
                    if(d[0].contains("proxies")) {
                        // get proxies properties
                    } else {
                        sb.append("\t\"").append(d[0]).append("\" \"").append(d[1]).append("\"\n");
                    }
                });
                break;
            }
        }
        if(sb.isEmpty()) {
            this.skinAProperties.forEach(s -> {
                if(s[0].contains("proxies")) {
                    // get proxies properties
                } else {
                    sb.append("\t\"").append(s[0]).append("\" \"").append(s[1]).append("\"\n");
                }
            });
        }
        return sb.toString();
    }
}

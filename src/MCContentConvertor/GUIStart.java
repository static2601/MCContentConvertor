package MCContentConvertor;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import static MCContentConvertor.Pathnames.*;

public class GUIStart {
    private JTextField jar_textfield;
    private JButton jar_search_button;
    private JButton output_search_button;
    private JProgressBar progressBar1;
    private JCheckBox compileModelsCheckBox;
    private JSpinner scale_spinner;
    private JCheckBox useUnitsCheckBox;
    private JButton models_custom_button;
    private JCheckBox extractTexturesCheckBox;
    private JButton textures_custom_button;
    private JTextArea progress_bar_label;
    private JScrollPane console_text_area;
    private JTextField output_textfield;
    private JPanel jPanel1;
    private JPanel main_jPanel;
    private JPanel progress_jPanel;
    private JLabel jar_error;
    private JLabel output_error;
    private JButton startExtractionButton;
    private JLabel gameDir_error;
    private JTextField gameDir_textfield;
    private JButton gameDir_search_button;
    private JComboBox texturepack_comboBox;
    private JCheckBox open_on_complete_checkbox;
    private JSpinner units_spinner;
    private JLabel materials_output_label;
    private JLabel models_output_label;

    private String jar_selected;
    private String output_selected;
    private String gameDir_selected;
    private boolean compileModels;
    private boolean extractTextures;
    private boolean useUnits = true;
    private double scale_spinner_value;
    private long units_spinner_value;
    private String texturePack_selected;
    private JSONObject texturePacksJSON;
    private getSettingsData settings;
    /// full path to game directory as specified by user in text field.
    public static String GAMEDIR;
    /// texturepack name only
    public static String TEXTUREPACK;
    /// full path to output as specified by user in text field.
    //public static String OUTPUTDIR;
    public static String modelsDir;
    public static double mdlScale;
    public static long mdlUnits;

    public static void main(String[] args) throws IOException, ParseException {

        // Print console to file if DEBUG is false, for use without IDE Console
        if (!DEBUG) {
            File consoleLOG = new File(USERDIR + "\\console.log");
            System.out.println("console.log path: " + consoleLOG);
            PrintStream printStream = new PrintStream(consoleLOG);
            System.setOut(printStream);
        }
        new GUIStart();
    }

    //TODO
    // adjusting lights color and position from vmf, since has to be done prior to converting in sourcecraft
    // add other vmf related options

    public GUIStart() throws IOException, ParseException {
        //getSettingsData settings = new getSettingsData();
        settings = new getSettingsData();
        // set this script as a reference in getSettingsData
        // so we can call back to here non-staticly
        settings.guiStartRef = this;
        // call set combobox for textures since get texturepack
        // files doesnt return until after all has ran.
        if(!DEBUG) textures_custom_button.setVisible(false);
        settings.setComboBarThing();
        JFrame frame = new JFrame();
        frame.setContentPane(jPanel1);
        scale_spinner.setModel(new SpinnerNumberModel(1.0, 0.0, 1000.0, 0.01));

        frame.setTitle("Minecraft Content 2 Source Convertor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 420);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        jar_textfield.setText(settings.get_mcJarPath());
        jar_selected = settings.get_mcJarPath();
        gameDir_textfield.setText(settings.get_gameDir());
        gameDir_selected = settings.get_gameDir();
        output_textfield.setText(settings.get_outputPath());
        //output_selected = settings.get_outputPath();
        output_selected = settings.get_gameDir();

        // disabled, use gameDir
        if (Objects.equals(output_selected, ""))
            output_selected = gameDir_selected;

        compileModelsCheckBox.setSelected(settings.get_compileModels());
        useUnitsCheckBox.setSelected(settings.get_useUnits());

        if (useUnitsCheckBox.isSelected()) {
            scale_spinner.setVisible(false);
            units_spinner.setVisible(true);
            units_spinner.setValue(settings.get_units());
            units_spinner_value = settings.get_units();

        } else {
            scale_spinner.setVisible(true);
            units_spinner.setVisible(false);
            scale_spinner.setValue(settings.get_scale());
            scale_spinner_value = settings.get_scale();
        }
        extractTextures = settings.get_extractTextures();
        compileModels = settings.get_compileModels();
        extractTexturesCheckBox.setSelected(settings.get_extractTextures());
        startExtractionButton.setEnabled(extractTextures || compileModels);

        System.out.println("get texturepacks in gui: "+ settings.get_texturePacks());

        texturepack_comboBox.setSelectedItem(settings.get_selectedTexturePack());
        texturePack_selected = settings.get_selectedTexturePack();
        if (!Objects.equals(output_selected, "")) setOutputLabels();

        jar_search_button.addActionListener(e -> {

            File selectedFile = fileChooserDialog(true);
            if (selectedFile != null) {
                if (!check_filetype(selectedFile, true)) {
                    // wrong file show error
                    System.out.println("Error: No JAR file selected");
                } else if (check_filetype(selectedFile, true)) {
                    // correct file selected
                    System.out.println("JAR Path: " + selectedFile.getAbsolutePath());
                    jar_selected = selectedFile.getAbsolutePath();
                }
                jar_textfield.setText(selectedFile.getAbsolutePath());
                settings.set_settingsKey("mcJarPath", selectedFile.getAbsolutePath());
            }
        });
        gameDir_search_button.addActionListener(e -> {
            File selectedFile = fileChooserDialog(false);
            if (selectedFile != null) {
                gameDir_selected = selectedFile.getAbsolutePath();
                System.out.println("gameDir: " + selectedFile.getAbsolutePath());
                gameDir_textfield.setText(selectedFile.getAbsolutePath());
                settings.set_settingsKey("gameDir", selectedFile.getAbsolutePath());
            }
        });
        output_search_button.addActionListener(e -> {

            File selectedFile = fileChooserDialog(false);
            if (selectedFile != null) {
                output_selected = selectedFile.getAbsolutePath();
                if (!Objects.equals(output_selected, "")) setOutputLabels();
                System.out.println("Output path: " + selectedFile.getAbsolutePath());
                output_textfield.setText(selectedFile.getAbsolutePath());
                settings.set_settingsKey("outputPath", selectedFile.getAbsolutePath());
            }
        });
        compileModelsCheckBox.addActionListener(e -> {
            compileModels = compileModelsCheckBox.isSelected();
            settings.set_settingsKey("compileModels",compileModels );
            startExtractionButton.setEnabled(extractTextures || compileModels);
        });
        extractTexturesCheckBox.addActionListener(e -> {
            extractTextures = extractTexturesCheckBox.isSelected();
            settings.set_settingsKey("extractTextures", extractTextures);
            startExtractionButton.setEnabled(extractTextures || compileModels);
        });
        useUnitsCheckBox.addActionListener(e -> {
            useUnits = useUnitsCheckBox.isSelected();
            if (useUnits) {
                units_spinner.setVisible(true);
                scale_spinner.setVisible(false);
            } else {
                units_spinner.setVisible(false);
                scale_spinner.setVisible(true);
            }
            settings.set_settingsKey("useUnits", useUnits);
        });
        models_custom_button.addActionListener(e -> {

        });
        textures_custom_button.addActionListener(e -> {
            // on button pressed, updates json, doesn't need run but once
            // button disabled until more functionality added
            updateTextureSettingsJson();
        });
        scale_spinner.addChangeListener(e -> {
            scale_spinner_value = (double) scale_spinner.getValue();
            settings.set_settingsKey("scale", scale_spinner_value);
        });
        units_spinner.addChangeListener(e -> {
            units_spinner_value = (int) units_spinner.getValue();
            settings.set_settingsKey("units", units_spinner_value);
        });
        jar_textfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                //System.out.println(e);
                // convert to File since check_filetype can only take File
                File file = new File(jar_textfield.getText());
                boolean is_good = check_filetype(file, true);
                if (is_good) {
                    System.out.println("text check: "+ is_good);
                    settings.set_settingsKey("mcJarPath", file.getAbsolutePath());
                }
                super.focusGained(e);
            }
        });
        gameDir_textfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // convert to File since check_filetype can only take File
                File file = new File(output_textfield.getText());
                boolean is_good = check_filetype(file, false);
                if (is_good) {
                    System.out.println("text check: "+ is_good);
                    settings.set_settingsKey("gameDir", file.getAbsolutePath());
                }
                super.focusGained(e);
            }
        });
        output_textfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // convert to File since check_filetype can only take File
                File file = new File(output_textfield.getText());
                boolean is_good = check_filetype(file, false);
                if (is_good) {
                    System.out.println("text check: "+ is_good);
                    settings.set_settingsKey("outputPath", file.getAbsolutePath());
                }
                super.focusGained(e);
            }
        });
        startExtractionButton.addActionListener(e -> {
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    try {
                        start_extraction();
                    } catch (IOException | ParseException | InterruptedException ex) {
                        System.err.println(ex);
                        throw new RuntimeException(ex);

                    }
                    return null;
                }
            };
            worker.execute();
        });
        texturepack_comboBox.addItemListener(e -> {
            texturePack_selected = Objects.requireNonNull(texturepack_comboBox.getSelectedItem()).toString();
            settings.set_settingsKey("selectedTexturePack", texturePack_selected);
            System.out.println("selectedTexturePack: "+ texturePack_selected);
            if (!Objects.equals(output_selected, "")) setOutputLabels();
        });
    }

    public void start_extraction() throws IOException, ParseException, InterruptedException {

        //TODO return with error, suggest checking file paths
        if (Objects.equals(jar_selected, "")
                || Objects.equals(gameDir_selected, "")) {
            progress_bar_label.setText("Check that your Paths are correct.");
            System.out.println("Check that your Paths are correct.");
            return;
        }

        // disable extraction button
        startExtractionButton.setEnabled(false);
        startExtractionButton.setVisible(false);
        materials_output_label.setText("Status:");
        materials_output_label.setHorizontalAlignment(SwingConstants.CENTER);
        models_output_label.setVisible(false);
        progress_bar_label.setText("");

        GAMEDIR = this.gameDir_selected;
        TEXTUREPACK = this.texturePack_selected;
        //OUTPUTDIR = this.output_selected;
        modelsDir = "props"+ "/" + this.texturePack_selected;

        System.out.println("extractedTextures: " + extractTextures);
        if (extractTextures) {
            // start texture extraction
            // run any options as needed
            getTextures();
            System.out.println("jar_selected: " + jar_selected);
            //System.out.println("output_selected: " + output_selected);
            System.out.println("gameDir_selected: " + gameDir_selected);
        }
        //TODO make save to json and update spinner in gui
        if (compileModels) {
            if (useUnits) {
                mdlScale = ( (double) this.units_spinner_value / 48);
                //mdlUnits = (long) scale_spinner_value;
                mdlUnits = this.units_spinner_value;
            }
            else {
                mdlScale = this.scale_spinner_value;
                mdlUnits = (long) (this.scale_spinner_value * 48);
            }
            // start compiling models
            // run any options as needed
            makeModels();
        }
    }
    public void getTextures() throws IOException, ParseException {

        // delete textures folder to start
        //TODO delete textures folder before run

        // TEMPTEXTURES = System.getProperty("user.dir") + TEXTURESTMP
        // TEXTURESTMP = "textures-tmp"
        File tempFolder = new File(TEMPTEXTURES);
        File base = new File(System.getProperty("user.dir"));
        File toDelete = new File(base, TEXTURESTMP);
        if(toDelete.equals(tempFolder)) {
            if (tempFolder.exists()) {

                if (!tempFolder.getCanonicalPath().startsWith(base.getCanonicalPath())) {
                    throw new SecurityException("Unsafe delete path: " + tempFolder);

                } else {

                    // delete it and remake it
                    //System.out.println("Simulate deleting texture folder contents: delete " + tempFolder);
                    FileUtils.deleteDirectory(tempFolder);
                    boolean makeTemp = tempFolder.mkdirs();
                    System.out.println("Making temporary textures folder: " + makeTemp);
                    System.out.println("temporary folder location: " + tempFolder);
                }
            } else {
                // make it
                boolean makeTemp = tempFolder.mkdirs();
                System.out.println("Making temporary textures folder: " + makeTemp);
                System.out.println("temporary folder location: " + tempFolder);
            }
        }
        else throw new SecurityException("Temporary paths do not match!: " + toDelete);

        getJsonData modelData = new getJsonData();
        JSONArray qc = modelData.modelData.getFirst();

        try {
            //TODO either create method to extract as zip or copy zip to temp folder
            // and rename there instead of renaming in source folder

            //TODO also copy SMDs to QCs folder in textures(temp) folder

            // first extract all textures from main textures of jar
            // will also need either specific or all textures of entities folders
            // extract all pngs to texture/pngs
            TextureGetter textureGetter = new TextureGetter();
            textureGetter.guiStartRef = this;

            String texturePackJAR = "";
            if (!texturePack_selected.equals("minecraft_original")) {

                if (texturePacksJSON.containsKey(texturePack_selected)) {

                    String oldName = (String) texturePacksJSON.get(texturePack_selected);
                    File oldNameFile = new File(USERDIR + "/Assets/TexturePacks/" + oldName);
                    if (oldNameFile.toString().endsWith(".zip")) {
                        File newFileName = new File(oldNameFile.toString().split("\\.zip")[0] + ".jar");

                        boolean renamed = oldNameFile.renameTo(newFileName);
                        if (renamed) {
                            texturePackJAR = newFileName.toString();
                        }
                    }
                    else
                    if (oldNameFile.toString().endsWith(".jar")) {
                        texturePackJAR = oldNameFile.toString();
                    }
                }
            }
            System.out.println("texturePack.jar: "+ texturePackJAR);

            // extract textures twice, first, base materials,
            // then if a valid texturepack.zip is selected, rename zip to jar and extract
            if (getExtractedTextures(textureGetter, texturePackJAR, jar_selected, PngsMaterialsDir)) {

                // if option to use custom texture pack is true,
                // copy zip to textures and rename zip to jar
                // rerun texture getter to overwrite vanilla textures
                // this will allow textures to fall back to vanilla if not in pack

                // color image, where should this go?
                // need to change color before writing? should if we can
                TintGrayTextures();

                // make uv map for model if it requires it
                // run for loop through all json data building uv map pngs
                int size = qc.size();
                set_progress_label("Making UV Map Textures...", false, "");
                System.out.println(dash+"Making UV Map Textures..."+dash);
                System.out.println("jqc.size(): "+ qc.size());
                for(int i=0; i<size; i++) {
                    makeUVMap uv_map = new makeUVMap(i);
                }

                set_progress_label("Making UV Map Textures...Done!", true, "");
                System.out.println("Making UV Map Textures...Done!");

                // convert all pngs from textures/pngs to vtfs
                textureGetter.MakeVTFs();

                // make all non-model vmts and place with vtfs
                textureGetter.MakeVMTs();

                // make animation vtfs
                // disabled for now since they need to be hand
                // imported into vtfedit and animations not working
                // properly at this time

                // copies the first frame of every animation to folder
                // is essential for textures to not be shrunk looking
                textureGetter.MakeAnimVTFs();

                // done, now copy all contents of vtfs/minecraft_original/


                // cant yet auto make animated vtfs, need to
                // import all of same name+numbers and save as vtf
                // vmt would have animation data in it.
                // overwriting vmt properties from previous vmt creator function
                // water being overwritten
                TextureGetter.MakeAnimVMTs();

                // copy everything from /textures/materials/ to materials of game directory
                File copy_from = new File(VTFSMATDIR + "/materials/");
                File copy_to = new File(GAMEDIR + "/materials/");
                System.out.println("copy_from: "+ copy_from + ", copy_to: "+ copy_to);
                TextureGetter.copyDirectory(copy_from.toString(), copy_to.toString());
            }

        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }

    }

    /// texturePath - path to texture.png from userDir
    public void colorizeImages(String texturePath) {
        //String texture = "textures/pngs/minecraft_original/assets/minecraft/textures/block/grass_block_top.png";
        try {
            // only run on gray textures needing colorized and once,
            // running multiple times will keep tinting color
            // no stop in place yet, textures folder should be deleted
            // ever run anyways...
            System.out.println("texturesPath: "+ texturePath);
            ColorizeImage colorizeImage = new ColorizeImage(texturePath);
            //ColorizeImage colorizeImage = new ColorizeImage(texture, Color.decode(color_hex));
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        } catch (ParseException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
    }

    public void TintGrayTextures() {
        // loop through all textures in array
        // runs colorizeImage function on them below
        ArrayList<String> grayTextures = new ArrayList<>() {};
        grayTextures.add("block/acacia_leaves");
        grayTextures.add("block/birch_leaves");
        grayTextures.add("block/dark_oak_leaves");
        grayTextures.add("block/jungle_leaves");
        grayTextures.add("block/mangrove_leaves");
        grayTextures.add("block/oak_leaves");
        //grayTextures.add("block/pale_oak_leaves");
        grayTextures.add("block/spruce_leaves");
        grayTextures.add("block/grass_block_top");
        grayTextures.add("block/short_grass");
        grayTextures.add("block/tall_grass_top");
        grayTextures.add("block/tall_grass_bottom");
        grayTextures.add("block/vine");
        grayTextures.add("block/lily_pad");
        grayTextures.add("block/large_fern_top");
        grayTextures.add("block/large_fern_bottom");
        grayTextures.add("block/fern");

        for(int i=0; i<grayTextures.size(); i++) {
            String textures_path = TEXTURESTMP + "/pngs/minecraft_original/assets/minecraft/textures/";
            colorizeImages(textures_path + grayTextures.get(i) + ".png");
        }
        // animated/generated
        grayTextures.clear();
        grayTextures.add("water_still");
        grayTextures.add("water_flow");

        for(int i=0; i<grayTextures.size(); i++) {
            String textures_path = TEXTURESTMP + "/pngs/minecraft_original/animated/generated/";
            File filename = new File(USERDIR + "\\" + textures_path);
            for(String file : Objects.requireNonNull(filename.list())) {
                if (file.startsWith(grayTextures.get(i))) {
                    colorizeImages(textures_path + file);
                }
            }

        }
//                JSONArray texture_settings_arr = settings.getTexturesSettings();
//                for(int i=0; i<texture_settings_arr.size(); i++) {
//                    System.out.println("texture_settings_arr: "+ texture_settings_arr.get(i));
//                    JSONObject jsonObject = (JSONObject) texture_settings_arr.get(i);
//                    JSONArray defaultColor = (JSONArray) jsonObject.get("defaultColor");
//                    JSONArray customColor = (JSONArray) jsonObject.get("customColor");
//                    JSONArray uncolored = (JSONArray) jsonObject.get("uncolored");
//                    long[] colorDefault = new long[]{
//                            (long) defaultColor.get(0),
//                            (long) defaultColor.get(1),
//                            (long) defaultColor.get(2)
//                    };
//                    String hex = String.format("#%02X%02X%02X", 255, 255, 255);
//                    //int colorr = new Color(25, 255, 255).;
//                    System.out.println("colorDefault: "+ hex);
//                    System.out.println("-15073281 to rgb: "+ Color.decode(hex));
//
//
//                }
    }

    public void updateTextureSettingsJson() {
        // test to set color value
        int red = (int) (0.55*255);
        int green = (int) (0.76*255);
        int blue = (int) (0.0*255);
        String hex = String.format("#%02X%02X%02X", red, green, blue);
        hex = "#003600";
        settings.set_texturesKey("defaultColor", "#319400", "grass_block_top");
        settings.set_texturesKey("transparent", true, "grass_block_top");
        settings.set_texturesKey("defaultColor", "#319400", "grass_block_side");
        settings.set_texturesKey("transparent", true, "grass_block_side");
        settings.set_texturesKey("defaultColor", hex, "lily_pad");
        settings.set_texturesKey("transparent", true, "lily_pad");
        settings.set_texturesKey("defaultColor", hex, "short_grass");
        settings.set_texturesKey("transparent", true, "short_grass");
        settings.set_texturesKey("defaultColor", hex, "tall_grass_bottom");
        settings.set_texturesKey("transparent", true, "tall_grass_bottom");
        settings.set_texturesKey("defaultColor", hex, "tall_grass_top");
        settings.set_texturesKey("transparent", true, "tall_grass_top");
        settings.set_texturesKey("defaultColor", hex, "vine");
        settings.set_texturesKey("transparent", true, "vine");
        settings.set_texturesKey("defaultColor", hex, "large_fern_bottom");
        settings.set_texturesKey("transparent", true, "large_fern_bottom");
        settings.set_texturesKey("defaultColor", hex, "large_fern_top");
        settings.set_texturesKey("transparent", true, "large_fern_top");
        settings.set_texturesKey("defaultColor", hex, "fern");
        settings.set_texturesKey("transparent", true, "fern");

        String water = String.format("#%02X%02X%02X", (int)(0.23*255), (int)(0.27*255), (int)(0.80*255));
        settings.set_texturesKey("defaultColor", water, "water_still");
        settings.set_texturesKey("transparent", true, "water_still");
        settings.set_texturesKey("defaultColor", water, "water_flow");
        settings.set_texturesKey("transparent", true, "water_flow");


        red = (int) (0.34*255);
        green = (int) (0.71*255);
        blue = (int) (0.07*255);
        hex = String.format("#%02X%02X%02X", red, green, blue);

        String hex2 = "#4b4712";
        String hex3 = "#223522";
        String hex4 = "#364724";
        String hex5 = "#494411";

        // tree leaves transparency
        boolean trees = false;
        settings.set_texturesKey("defaultColor", hex2, "oak_leaves");
        settings.set_texturesKey("transparent", trees, "oak_leaves");

        settings.set_texturesKey("defaultColor", hex2, "acacia_leaves");
        settings.set_texturesKey("transparent", trees, "acacia_leaves");

        settings.set_texturesKey("defaultColor", hex2, "jungle_leaves");
        settings.set_texturesKey("transparent", trees, "jungle_leaves");

        settings.set_texturesKey("defaultColor", hex3, "spruce_leaves");
        settings.set_texturesKey("transparent", trees, "spruce_leaves");

        settings.set_texturesKey("defaultColor", hex2, "dark_oak_leaves");
        settings.set_texturesKey("transparent", trees, "dark_oak_leaves");

        settings.set_texturesKey("defaultColor", hex4, "birch_leaves");
        settings.set_texturesKey("transparent", trees, "birch_leaves");

        settings.set_texturesKey("defaultColor", hex5, "mangrove_leaves");
        settings.set_texturesKey("transparent", trees, "mangrove_leaves");

        settings.set_texturesKey("transparent", trees, "azalea_leaves");
        settings.set_texturesKey("transparent", trees, "flowering_azalea_leaves");
        settings.set_texturesKey("transparent", trees, "cherry_leaves");
        settings.set_texturesKey("transparent", trees, "pale_oak_leaves");

        System.out.println("Texture Settings Updated.");
    }

    public boolean getExtractedTextures(TextureGetter tg, String texturePackJAR, String jar_selected, String PngsMaterialsDir) throws IOException {
        if (tg.ExtractJar(jar_selected, PngsMaterialsDir, true)) {
            if (!Objects.equals(texturePackJAR, "")) {
                return tg.ExtractJar(texturePackJAR, PngsMaterialsDir, false);
            }
            return true;
        }
        return false;
    }

    public void makeModels() throws IOException, ParseException, InterruptedException {

        // might need to check if textures are all there before processing models
        PrintQCFileOfMdlName();

        set_progress_label("100% Done!", false, "");
        System.out.println("100% Done!");

        startExtractionButton.setEnabled(true);

        // open output folder on completed (if true)
//        if (open_on_complete_checkbox.isSelected()) {
//            File out = new File(output_selected + "/");
//            System.out.println("is directory: " +out.isDirectory());
//            Desktop.getDesktop().browseFileDirectory(out);
//            System.out.println(out);
//        }
    }

    public void PrintQCFileOfMdlName() throws IOException, ParseException, InterruptedException {
        getJsonData modelData = new getJsonData();
        JSONArray qc = modelData.modelData.getFirst();

        set_progress_label("Making QC Files and Compiling Models...", false, "");
        System.out.println(dash+"Making QC Files and Compiling Models..."+dash);

        // make QCs folder in textures(temp) folder
        String qcPath = TEMPTEXTURES + "\\QCs\\";
        File qcPathFile = new File(qcPath+"SMDs\\");
        if(!qcPathFile.exists()) {
            qcPathFile.mkdirs();
        }

        // copy SMDs from Assets to new QCs folder
        File copy_from = new File(USERDIR + "/Assets/SMDs/");
        File copy_to = new File(TEMPTEXTURES + "/QCs/SMDs");
        System.out.println("copy_from: "+ copy_from + ", copy_to: "+ copy_to);

        String[] files = copy_from.list();
        assert files != null;
        for(String file : files) {
            File copy_from_2 = new File(copy_from + "\\" + file);
            File copy_to_2 = new File(copy_to + "\\"+ file);
            TextureGetter.copyFile(copy_from_2, copy_to_2);
        }

        ArrayList<String> failedCompiles = new ArrayList<>();
        for(int i = 0; i < qc.size(); i++) {
            makeQC qcc = new makeQC(i);
            System.out.println("\nQC File for index[" + i + "]:");
            String QC = qcc.makeQCFile().toString();
            String mdlName = qcc.modelName;

            if (QC.isEmpty())
                continue;


            //String qcPath = USERDIR + "\\QCs\\";
            qcc.WriteQCFile(QC, qcPath, mdlName);
            System.out.println(qcPath + "<- qcPath :" + mdlName);

            String[] vmts = qcc.makeVMT();

            System.out.println("compiling next model, " + qcPath + mdlName);
            if (qcc.CompileModel(qcPath, mdlName + ".qc") > 0) {
                failedCompiles.add(qcPath + mdlName);
            }
        }
        String failed = "";
        if (!failedCompiles.isEmpty()) {
            //TODO do something to show failed compiles in gui
            failed = failedCompiles.size()+" failed compiles!";
        }
        failedCompiles.forEach(c -> System.err.println("Failed Compiles: " + c));

        set_progress_label("Done!", true, failed);
        //set_progress_label("Making QC Files and Compiling Models...Done!", true);
        System.out.println("Making QC Files and Compiling Models...Done!");

    }

    /// called from getSettingsData() after a reference set for this script
    public void setTexturePackCombo(JSONObject tp){
        this.texturePacksJSON = tp;
        Set keys = tp.keySet();
        keys.forEach(k ->{
            this.texturepack_comboBox.addItem(k);
        });
    }

    public void setOutputLabels() {
        File output = new File(this.gameDir_selected);
        String out;
        out = output.toString().replace(output.getParent(), "").replace("\\", "/");
        String materials = "Materials output -> ...";
        String models = "Models output -> ...";
        // probably not needed if game dir is the only option...
        this.materials_output_label.setText(materials + out + "/materials/"+ this.texturePack_selected + "/");
        this.models_output_label.setText(models + out + "/models/props/"+ this.texturePack_selected + "/");
    }

    /** Open file dialog to search for file or directory
     *
     * @param is_file is true if looking for file, false if directory
     * @return selected file or directory
     */
    public File fileChooserDialog(boolean is_file) {
        JFileChooser file_chooser = new JFileChooser();

        if (is_file)
            file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        else
            file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = file_chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return file_chooser.getSelectedFile();
        }
        return null;
    }

    /** Check if the file(file) is a type of file(is_file) or directory
     *
     * @param file File to be checked
     * @param is_file check if file or directory
     * @return true if is actual File and if is_file is true also; return false if anything else
     */
    public boolean check_filetype(File file, boolean is_file) {
        if (is_file) {
            jar_error.setText("");
            if (file.isFile()) {
                if (file.getName().contains(".jar")) return true;
            }
            jar_error.setText("!");
        }
        else {
            // not resetting when error and selecting right directory
            // works to reselect correct dir again
            output_error.setText("");
            if (file.isDirectory()) return true;
            output_error.setText("!");
        }
        return false;
    }

    /** Set GUI Processing label
     * @param str message
     * @param addToLast true, make new message, false - add to last  */
    public void set_progress_label(String str, boolean addToLast, String errors) {

        String s = progress_bar_label.getText();
        if(addToLast) {
            //String s = progress_bar_label.getText();
            if (!errors.isEmpty()) {
                progress_bar_label.setText(s + errors + "(check console.log)\n");
            }
            else {
                progress_bar_label.setText(s + "Done!" + "\n");
            }
        } else {
            //make new message
            progress_bar_label.setText(s + str);
        }
    }
}

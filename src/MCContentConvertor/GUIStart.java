package MCContentConvertor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class GUIStart implements Paths {
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
    private JLabel progress_bar_label;
    private JScrollPane console_text_area;
    private JTextField output_textfield;
    private JPanel jPanel1;
    private JPanel main_jPanel;
    private JPanel progress_jPanel;
    private JLabel jar_error;
    private JLabel output_error;
    private JButton startExtractionButton;

    private String jar_selected;
    private String output_selected;
    private boolean compileModels = true;
    private boolean extractTextures = true;
    private boolean useUnits = true;
    private int scale_spinner_value;

    public static String tfDirPath; // needs to be tf folder for getting studiomdl, could still make it any folder.
                                    // will need to think about other games without the tf folder
    public static double mdlScale;


    public static void main(String[] args) {
        new GUIStart();
    }

    public GUIStart() {

        JFrame frame = new JFrame();
        frame.setContentPane(jPanel1);

        frame.setTitle("Minecraft Content 2 Source Convertor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        jar_search_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
                }
            }
        });
        output_search_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File selectedFile = fileChooserDialog(false);
                if (selectedFile != null) {
                    output_selected = selectedFile.getAbsolutePath();
                    System.out.println("Output path: " + selectedFile.getAbsolutePath());
                    output_textfield.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        compileModelsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compileModels = compileModelsCheckBox.isSelected();
            }
        });
        extractTexturesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extractTextures = extractTexturesCheckBox.isSelected();
            }
        });
        useUnitsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                useUnits = useUnitsCheckBox.isSelected();
            }
        });
        models_custom_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        textures_custom_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        scale_spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                scale_spinner_value = (int) scale_spinner.getValue();
            }
        });
        jar_textfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                System.out.println(e);
                // convert to File since check_filetype can only take File
                File file = new File(jar_textfield.getText());
                System.out.println(check_filetype(file, true));
                super.focusGained(e);
            }
        });
        output_textfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // convert to File since check_filetype can only take File
                File file = new File(output_textfield.getText());
                System.out.println( check_filetype(file, false));
                super.focusGained(e);
            }
        });
        startExtractionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    start_extraction();
                } catch (IOException | ParseException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void start_extraction() throws IOException, ParseException, InterruptedException {
        // disable extraction button
        startExtractionButton.setEnabled(false);
        scale_spinner_value = 40;
        // check if path and output set
        //jar_selected = "C:\\Users\\Statiic\\AppData\\Roaming\\.minecraft\\versions\\1.21.5\\1.21.5.jar";
        jar_selected = "C:\\Users\\Statiic\\AppData\\Roaming\\.minecraft\\versions\\1.21\\1.21.jar";
        // can not do it liek this, looking for studiomdl in that. tempstudiomdl...
        // should throw an error if something that bad happens...
        //output_selected = "C:\\Users\\Statiic\\Desktop\\tftemp";
        output_selected = "D:\\SteamLibrary\\steamapps\\common\\Team Fortress 2\\tf";
        if (jar_selected == null) return;
        if (output_selected == null) return;

        // define our path to out tf directory or output folder
        // hack to globalize our path since we cant store in interface
        tfDirPath = output_selected;

        if (extractTextures) {
            // start texture extraction
            // run any options as needed
            getTextures();

        }
        if (compileModels) {
            if (useUnits) {
                mdlScale = ( (double) scale_spinner_value / 48);
            }
            else mdlScale = scale_spinner_value;
            // start compiling models
            // run any options as needed
            makeModels();
        }
    }

    public void getTextures() throws IOException, ParseException {

        //String fsPath = "D:/Steam/steamapps/common/Team Fortress 2/tf/materials/";
        getJsonData modelData = new getJsonData();
        JSONArray qc = modelData.modelData.get(0);
        JSONObject jqc = (JSONObject) qc.get(0);

        // set our value for scale
        // hack to globalize our scale since we cant store in interface



        try {
            // first extract all textures from main textures of jar
            // will also need either specific or all textures of entities folders
            // extract all pngs to texture/pngs
            if (TextureGetter.ExtractJar(jar_selected, PngsMaterialsDir)) {

                // make uv map for model if it requires it
                // run for loop through all json data building uv map pngs
                int size = jqc.size();
                System.out.println(dash+"Making UV Map Textures..."+dash);
                for(int i=0; i<size; i++) {
                    makeUVMap uv_map = new makeUVMap(i);
                    // commenting out made this work...
                    //System.out.println(Arrays.toString(uv_map.makeImage()));
                }
                System.out.println("Making UV Map Textures...Done!");

                // make all qc files and compile, creating the models and prop materials folders
                // where they go
                // should materials directory be made on qc compilation or vmt?

                // make all vmts and place in materials folder of props

                // will need to make vtfs and vmts from those(no resizing needed)
                // convert all pngs from textures/pngs to vtfs
                TextureGetter.MakeVTFs();
                // make all non-model vmts and place with vtfs
                TextureGetter.MakeVMTs();
                // make animation vtfs
                TextureGetter.MakeAnimVTFs();


                // cant yet auto make animated vtfs, need to
                // import all of same name+numbers and save as vtf
                // vmt would have animation data in it.
                TextureGetter.MakeAnimVMTs();
            }

        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public void makeModels() throws IOException, ParseException, InterruptedException {
        getJsonData modelData = new getJsonData();
        String modelName = "Lectern";

        System.out.println(dash+"Making UV Maps..."+dash);

        makeUVMap uvmapp = new makeUVMap(modelData.getIndexByName(modelName));

        System.out.println("Making UV Maps...Done!");

        //System.out.println(Arrays.toString(uvmapp.makeImage()));

        ///TextureGetter.MakeVTFs();

        ///modelQCCreator QCCreator = new modelQCCreator();
        //printVMTsOfMdlName("CrossModel3");

        // might need to check if textures are all there before processing models
        PrintQCFileOfMdlName(modelName, false);

        System.out.println("100% Done!");
        set_progress_label("100% Done?", false);
        startExtractionButton.setEnabled(true);

        //int idx = modelData.getIndexByName(modelName);
        //getJsonData jData = new getJsonData(idx);

        //jData.setIndex(idx);

        //System.out.println("modelname: "+jData.modelname);
        //System.out.println("aimdata: "+jData.animatedTextureVar);

    }

    public void PrintQCFileOfMdlName(String name, boolean doOne) throws IOException, ParseException, InterruptedException {
        getJsonData modelData = new getJsonData();
        JSONArray qc = modelData.modelData.get(0);
        //JSONObject jqc = (JSONObject) qc.get(0);

        //boolean doOne = false;
        if(doOne){
            int indexx = modelData.getIndexByModelName(name);
            if (indexx != -1) {
                //materialsCreator.makeQC qcc = new materialsCreator.makeQC(indexx);
                makeQC qcc = new makeQC(indexx);
                System.out.println("\nQC File for index[" + indexx + "]:");
                String QC = qcc.makeQCFile().toString();
                System.out.println(QC);

                //makeQC qcc = new makeQC(i);
                String qcPath = UserDir + "/QCs";
                qcc.WriteQCFile(QC, qcPath, name);
                //qcc.CompileModel(qcPath, name+".qc");


                //qcc.WriteQCFile(QC, "", name);

                //qcc = new makeVMT(indexx);
                String[] vmts = qcc.makeVMT();
                //qcc.CompileModel(qcPath, name+".qc");
                //System.out.println(Arrays.toString(vmts));

                //for(String vmt : vmts) {
                //System.out.println(vmt);
                //}
                //qcc.WriteVMTFile(vmts, tfMatDir);

            } else System.out.println("model data not found.");
        } else {

            System.out.println(dash+"Making QC Files and Compiling Models..."+dash);

            set_progress_label("Making QC Files...", false);
            ArrayList<String> failedCompiles = new ArrayList<>();
            for(int i = 0; i < qc.size(); i++) {
                makeQC qcc = new makeQC(i);
                System.out.println("\nQC File for index[" + i + "]:");
                String QC = qcc.makeQCFile().toString();
                String mdlName = qcc.modelName;

                if (QC.isEmpty())
                    continue;

                // why was this setup like this??
                // bat file name is QCscompiler.bat
                //String qcPath = UserDir + "/QCs";
                String qcPath = UserDir + "\\QCs\\";
                qcc.WriteQCFile(QC, qcPath, mdlName);
                System.out.println(qcPath + "<-------------------------------qcPath :" + mdlName);

                String[] vmts = qcc.makeVMT();

                System.out.println("compiling next mdodel, " + qcPath + mdlName);
                if (qcc.CompileModel(qcPath, mdlName + ".qc") > 0) {
                    failedCompiles.add(qcPath + mdlName);
                }
            }
            failedCompiles.forEach(c -> System.out.println("Failed Compiles: " + c));

            System.out.println("Making QC Files and Compiling Models...Done!");
            set_progress_label("Done!", true);
        }
    }
    public void compileModels() {

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
    public void set_progress_label(String str, boolean addToLast) {
        //append to progress message
        if(addToLast) {
            String s = progress_bar_label.getText();
            progress_bar_label.setText(s + str);

        } else {
            //make new message
            progress_bar_label.setText(str);
        }
    }
}

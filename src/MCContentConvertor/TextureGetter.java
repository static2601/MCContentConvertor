package MCContentConvertor;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static MCContentConvertor.GUIStart.*;
import static MCContentConvertor.Pathnames.*;


public class TextureGetter {

	public static GUIStart guiStartRef;
	static QCFunctions qc = new QCFunctions();
	private getSettingsData settings;
	//private static int fileCount = 0;
	public static ArrayList<String> animatedVTFs = new ArrayList<String>();
	//String VTFsMatDir = "materials/"+ texturePack +"/";
	//String VTFsMatDir = gameDir + "/materials/"+ texturePack +"/";
	//String VTFsMatDir = OUTPUTDIR + "/materials/"+ TEXTUREPACK +"/";
    static String VTFsMatDir = VTFSMATDIR + "/materials/"+ TEXTUREPACK +"/";

	public TextureGetter() throws IOException, ParseException {
		this.settings = new getSettingsData();
	}
	/**
	 * Extract pngs from jar
	 * @param jarFile
	 * @param destDir
	 * @return True on success
	 * @throws IOException
	 */
	public boolean ExtractJar(String jarFile, String destDir, boolean base) throws IOException {
		
		//print to GUI Extracting Textures...
		String sBase = "Base ";
		if (!base) sBase = "Custom ";
		guiStartRef.set_progress_label("Extracting "+sBase+"Textures...", false, "");
		System.out.println(dash+"Extracting "+sBase+"Textures..."+dash);

		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> enumEntries = jar.entries();
		while (enumEntries.hasMoreElements()) {
		    JarEntry file = enumEntries.nextElement();

			File f = new File(destDir + File.separator + file.getName());

			String[] aStr  = file.getName().split("/");
			String fn = file.getName().split("/")[aStr.length-1];
			String str2 = file.getName().replace(fn, "");
			boolean splitTexture = false;
			boolean isMeta = false;

			//needs to get only the directories we are using
			if(f.getName().endsWith(".png") || f.getName().endsWith(".mcmeta")) {

				if(f.getName().endsWith(".mcmeta")) isMeta = true;

				if(str2.endsWith("assets/minecraft/textures/block/")
				|| str2.contains("assets/minecraft/textures/entity/")) {


					if(!f.exists())
						f.getParentFile().mkdirs();

					if(str2.endsWith("assets/minecraft/textures/block/")){

						//set variable to indicate true to run this function AFTER extraction
						splitTexture = true;
					}

					//InputStream is = jar.getInputStream(file);
					//FileOutputStream fos = new FileOutputStream(f);
					//TODO would this be faster is writing all at once?
					if(!isMeta) {
						InputStream is = jar.getInputStream(file); // get the input stream
						FileOutputStream fos = new FileOutputStream(f);
						while (is.available() > 0) {  // write contents of 'is' to 'fos'

							fos.write(is.read());
						}
						fos.close();
						is.close();
					}
					//TODO had to uncheck because it was erroring interpolating frames
					// after trying to run a 32bit texturepack, but still doesnt work
					// trying to run the one weve been using
				    if(splitTexture && !isMeta) splitTextures(f);
				}
			}
		}
		jar.close();

		// move premade assets to pngs folder
		// Assets/Textures/air.png
		// textures/pngs/minecraft_original/assets/minecraft/textures/block
		// Path of the file where data is to be copied

		copyFile(new File(USERDIR + "/Assets/Textures/air.png"),
                new File(USERDIR + "/textures/pngs/minecraft_original/assets/minecraft/textures/block/air.png"));
		
		//print to GUI Processing label
		guiStartRef.set_progress_label("Extracting "+sBase+"Textures...Done!", true, "");
		System.out.println("Extracting "+sBase+"Textures...Done!");
		
		return true;
	}
	
	public static ArrayList<ArrayList<File>> imageList = new ArrayList<ArrayList<File>>();
	private static void splitTextures(File f) {

		String outputFolder = Animated;
		
		//find all textures where pixel height is more the width
		//indicating animated texture needing additional processing
		String imgPath = f.getPath();
		int startX = 0, startY = 0, endY = 0, endX = 0;
		BufferedImage img = null;
		
		try { img = ImageIO.read(new File(imgPath));

			//if height is more then width
			if(img.getWidth() != img.getHeight()) {
				
				ArrayList<File> images = new ArrayList<File>();
				//get number of frames
				int frames = img.getHeight() / img.getWidth();
				startX = 0; startY = 0; endX = 0; endY = 0;
				
				for(int i = 0; i < frames; i++) {

					startX = 0;
					endX = img.getWidth();
					startY = i * img.getWidth();
					endY = (i+1) * img.getWidth();

					BufferedImage c = new BufferedImage(img.getWidth(), img.getWidth(), BufferedImage.TYPE_INT_ARGB);
					Graphics g = c.createGraphics();
					
					if(g.drawImage(img, 0, 0, img.getWidth(), img.getWidth(), startX, startY, endX, endY, null)) {
						
						String fname = f.getName().split(".png")[0];
						
						//add file name to array so we can know which pngs not to make vtfs immediately
						animatedVTFs.add(fname);
						
						String inc = "";
						if(i < 10) inc = "0"+ i;
						else inc = ""+ i;
						
						File outputfile = new File(outputFolder +"\\"+ fname+ "_" + inc + ".png");
						
						if(!outputfile.exists()) outputfile.mkdirs();
						
					    ImageIO.write(c, "png", outputfile);
					    //add image to array for interpolating later if needed
					    images.add(outputfile);
					    
					    img.flush();
					    c.flush();
					    
					}
				}
				//add images to array of images needing interpolated
				imageList.add(images);
				//TODO
				String fn = f.getName().split(".png")[0];
				List<Integer> g = AnimVMTProps.animVMTData.get(fn);
				
				if(g != null) {
					int interpFrames = g.get(0);
					InterpolateFrames(outputFolder+"\\"+f.getName(), interpFrames);
					//System.out.println("interpFrames: "+interpFrames);
//					System.out.println("hashmap "+AnimVMTProps.animVMTData.get(fn));
//					System.out.println(fn);
//					System.out.println("InterpolateFrames("+ outputFolder+"\\"+f.getName() +", "+ interpFrames +");");
//					System.out.println("\n\n");
				}
			}
		}
		catch (IOException e1 ) {
			
			e1.printStackTrace();
			System.out.println("e: "+ imgPath);
			System.out.println("e: Start X:"+ startX +" Y:"+ startY + " end X:"+ endX + " Y:" + endY);
			System.out.println("e: Image X: "+ img.getWidth() + " Y: "+ img.getHeight());
			System.out.println("e: "+ f.getName());

		}
	}
	
	public void MakeVTFs() throws IOException, InterruptedException, ParseException {
		
		//get batch file or create batch file with properties
		//and set path of pngs. set output folder to vtfs for now.
		//need to do non recursive, when done, change input to /entity and output to /entity
		//need to get these directories from mat paths i did
		guiStartRef.set_progress_label("Checking Models for Texture Paths...", false, "");
		System.out.println(dash+"Checking Models for Texture Paths..."+dash);

		/* folder path for current model material path retrieved from
		 * getUniquePaths function, switched on array*/
		String modelMatPath = "";
		List<String> arr = qc.getDirUniquePaths(false);
		//arr.add("\\entity\\uvmap");
		arr.forEach(s->System.out.println("arr: "+s));
		guiStartRef.set_progress_label("Checking Models for Texture Paths...Done!", true, "");
		System.out.println("Checking Models for Texture Paths...Done!");
		//print to GUI Processing label
		//GUIStart.set_progress_label("Making VTFs...", false);

		guiStartRef.set_progress_label("Making VTFs...", false, "");
		System.out.println(dash+"Making VTFs..."+dash);

		//ArrayList<String> tempFolders = new ArrayList<>();

		// for each unique model material path...
		for(int i=0; i<arr.size();) {
			modelMatPath = arr.get(i).replace("/", "\\");
			String modelMatPath2 = modelMatPath;
			int resizeHeight = 128;
			int resizeWidth = 128;
			boolean convertingTemp = false;

			System.out.println("Converting modelMatPath " + modelMatPath);
			//fix up to put block folder in root
			// skip any name not in a folder, except /blocks
			if(!modelMatPath.equals("\\block") && !modelMatPath.endsWith("/") && !modelMatPath.endsWith("\\")) {
				i++;
				System.out.println("Skipping this modelMatPath " + modelMatPath);
				continue;
			}
			//if(modelMatPath.equals("\\block")) modelMatPath2 = "";
			// added because modelJson data vtfLocation: block/ was being picked up separately
			// and adding block folder with all vtfs inside it
			if(modelMatPath.equals("\\block") || modelMatPath.equals("\\block\\")) modelMatPath2 = "";

			//path to model's materials (modelMatPath)
			String prefix = "\\assets\\minecraft\\textures";


			String inputFolder = PngsMaterialsDir + prefix + modelMatPath +"*.png";
			//String f = inputFolder.replace("*.png", "");

			boolean isBlock = false;
			if(modelMatPath.equals("\\block")) {
				isBlock = true;
				inputFolder = PngsMaterialsDir + prefix + modelMatPath +"\\*.png";
				//f = inputFolder.replace("\\*.png", "");
			}

			// modelMatPath2 is a copy of modelMatPath, if our run is on /block,
			// then modelMatPath2 equals "", below statement wont run
			if(modelMatPath2.endsWith("/")) {
				int  lastSlash = modelMatPath2.lastIndexOf("/");
				modelMatPath2 = modelMatPath2.substring(0, lastSlash);
				System.out.println("modelMatPath2: "+ modelMatPath2);
			}
			String outputFolder = VTFsMatDir + modelMatPath2;

			String fileName = "convert"+ (i+1) +".bat";
			String q = "\"";
			

			if(outputFolder.endsWith("\\")) {
				outputFolder = outputFolder.substring(0, outputFolder.lastIndexOf("\\"));
				System.out.println("outputFolder: "+outputFolder);
			}
			File folderOutput = new File(outputFolder);
			File folderInput = new File(inputFolder);

			
			File PNGFolderInput = new File(folderInput.toString().split("\\*.png")[0]);
			
			if(!folderOutput.exists()) folderOutput.mkdirs();
			String doResize = "";
			if(isBlock) doResize = " -rwidth "+resizeHeight+" -rheight "+resizeWidth;
			String batContent = ""
					+ q+ VTFCMDEXE +q
					+ " -folder "+q+inputFolder +q
					+ " -output "+q+outputFolder+q
					+ doResize
					+ " -resize -format \"RGBA8888\""
					+ " -alphaformat \"RGBA8888\""
					+ " -rfilter \"POINT\""
					+ " -flag \"POINTSAMPLE\""
					+ " -nomipmaps"
					+ " -silent"
					//+ " -pause"
					+ "";
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(BATDIR + "\\" + fileName));
			
			//Create bat file
			writer.write(batContent); 
			writer.close();

			// show or suppress model compile results
			String showOutput = "/b";
			if (DEBUG) showOutput = "";

			// execute bat file
			String[] args = {"cmd.exe", "/c", "Start", showOutput, "convert"+(i+1)+".bat"};
			int code = BatchRunner.runBat(args, BATDIR);

		    if(code >= 0) {
				if(code > 0) {
			 	    //something didnt go right
			 	    System.err.println("Process returned "+ code + " for subFolder: "+ modelMatPath);
					// else here to stop the program ...
					//System.exit(0);
			    }

			    i++;
			    //if array is on block, subfolder2 will equal "".
			    //check if all files are there before running next bat
			    if(modelMatPath2.isEmpty()) {
			 	   int fiCount = Objects.requireNonNull(PNGFolderInput.listFiles()).length;
			 	   //check if all files extracted were made into VTFs, then continue
					//TODO
			 	   //DOES NOT RUN IF OLD FILE STILL IN FOLDER!	Delete folder before starting or something
			 	   System.out.println("Converting VTFs 0 / "+fiCount);
			 	   while(Objects.requireNonNull(folderOutput.listFiles()).length < fiCount-1) {
			 		   System.out.println(Objects.requireNonNull(folderOutput.listFiles()).length+" <--length|fileCount--> "+fiCount);
			 	   }
			 	   System.out.println("Converting VTFs "+ Objects.requireNonNull(folderOutput.listFiles()).length+" / "+fiCount);
				}
		   	}
		}
		
		//print to GUI Processing label
		//GUIStart.set_progress_label("Done!", true);
		guiStartRef.set_progress_label("Making VTFs...Done!", true, "");
		System.out.println("Making VTFs...Done!");

	}

	public void MakeAnimVTFs() throws IOException, InterruptedException {
		
		//print to GUI Processing label
		//GUIStart.set_progress_label("Making Animated VTFs...", false);
		guiStartRef.set_progress_label("Making Animated VTFs...", false, "");
		System.out.println(dash+"Making Animated VTFs..."+dash);

		String inputFolder = Generated +"\\*.png";
		// temp, compiled anims will go to actual MaterialsDir
		//must be made by hand, file > import all frames save as vtf
		//move to base materials folder
		String outputFolder = VTFs2MatDir;
		String fileName = "convertAnim" + ".bat";
		String q = "\"";
		
		File f = new File(outputFolder);
		File f2 = new File(inputFolder);
		File fi = new File(f2.toString().split("\\*.png")[0]);
		int fiCount = fi.listFiles().length;
		
		if(!f.exists()) f.mkdirs();

		System.out.println(fi.listFiles().length +", "+ fiCount);
		
		String batContent = ""
				+ q+ VTFCMDEXE +q
				+ " -folder "+q+inputFolder +q
				+ " -output "+q+outputFolder+q
				+ " -rwidth 128 -rheight 128"
				+ " -resize -format \"RGBA8888\""
				+ " -alphaformat \"RGBA8888\""
				+ " -rfilter \"POINT\""
				+ " -flag \"POINTSAMPLE\""
				+ " -nomipmaps"
				+ " -silent"
				//+ " -pause"
				+ "";
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(BATDIR + "\\" + fileName));
		
		//make bat file
		writer.write(batContent); 
		writer.close();
		System.out.println(BATDIR + "/" + fileName);

		// Execute command
       	String[] args = {"cmd.exe", "/c", "Start", "/b", "convertAnim.bat"};
	   	int returned = BatchRunner.runBat(args, BATDIR);
	   	//Process p = Runtime.getRuntime().exec(args);
	   	//int returned = p.waitFor();

	   if(returned == 0) {
			System.out.println(returned);
			System.out.println(outputFolder);

			//if directory is already populated with files, this wont work!
			//either delete manually, through the script or remove this
			//delete generated folder, delete vtfs2 folder before starting, maybe vtfs also
			//same for the other while loop in makevtfs()


			while(f.listFiles().length < fiCount) {
				System.out.println(f.listFiles().length + " < " + fiCount);
			}
			System.out.println(f.listFiles().length+" - 100% - "+fiCount);

		   guiStartRef.set_progress_label("Making Animated VTFs...Done!", true, "");
		   System.out.println("Making Animated VTFs...Done!");
			//MakeAnimVMTs();
		   // move animated vtf from vtf2 to vtf folder
		   // only move vtf of name ending with zero, the first frame
		   // this will be default until animations are generated (manually for now)
		   MoveAnimVTF(f);
	   }
	}

	public void MoveAnimVTF(File f) throws IOException {

		guiStartRef.set_progress_label("Moving Animated VTFs to Textures Folder...", false, "");
		System.out.println(dash+"Moving Animated VTFs to Textures Folder..."+dash);

		File[] files = f.listFiles();
		for(int i = 0; i < Objects.requireNonNull(f.listFiles()).length; i++) {
            assert files != null;
			//System.out.println("files[i].getName().endsWith(\"_0\"): " +files[i].getName());
			String name = files[i].getName().replace(".vtf", "");
            if(name.endsWith("_0")) {
				// copy to vtf folder (minecraft_original)
				String fname = files[i].getName().replace("_0", "");
				File from = new File(files[i].getAbsolutePath());

				File to = new File(VTFsMatDir + "\\" + fname);
				boolean written = copyFile(from, to);
				//System.out.print("from: " + from + ", to: "+ to + "...");
				System.out.println(to+"...written = "+written);
			}
		}
		guiStartRef.set_progress_label("Moving Animated VTFs to Textures Folder...Done!", true, "");
		System.out.println("Moving Animated VTFs to Textures Folder...Done!");
	}

	public void MakeVMTs() {

		guiStartRef.set_progress_label("Making VMTs...", false, "");
		System.out.println(dash+"Making VMTs..."+dash);

		File f = new File(VTFsMatDir);
		String outputPath = f.toString();
		
		//if(!f.exists()) f.mkdirs();
		
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
		//System.out.println("names.size(): "+names.size());
		ArrayList<String> vmts = new ArrayList<String>();
		
		for(int i = 0; i < names.size(); i++) {
			if(names.get(i).endsWith(".vtf")) {
				vmts.add(names.get(i));
			}
		}
		String path = outputPath;
		String vmt = "";
		
		//create VMT file for every name.vtf
		for(int i = 0; i < vmts.size(); i++) {

			String matdir = VTFsMatDir.split("materials/")[1];
			String n = vmts.get(i).split("\\.")[0];

			String setAlpha = "";
			int alphatest = CheckAlphaTest(n);
			if (alphatest == 0)
				setAlpha = "	\"$translucent\" "+CheckTranslucent(n)+"\n";
			else setAlpha = "	\"$alphatest\" "+alphatest+"\n";

			String color = "";
			//take names and create a vmt for all
			vmt = ""
				+ ""
				+ "\"LightmappedGeneric\"\n"
				+ "{\n"
				+ "	\"$basetexture\" \""+ matdir +""+ n +"\"\n"
				+ setAlpha
				+ setColor(n)
				+ setOtherProps(n)
				+ "}";

			WriteFile(vmt, path+"\\", n+".vmt");
		}
		guiStartRef.set_progress_label("Making VMTs...Done!", true, "");
		System.out.println("Making VMTs...Done!");
	}

	public static int CheckTranslucent(String vtf) {
		if(vtf.contains("glass")) return 1;
		if(vtf.contains("vine")) return 1;
		if(vtf.contains("lichen")) return 1;
		if(vtf.contains("ladder")) return 1;
		if(vtf.contains("vein")) return 1;
		//if(vtf.contains("water")) return 1;
		if(vtf.equals("rail")) return 1;
		if(vtf.equals("lily_pad")) return 1;
		if(vtf.equals("ice")) return 1;
		if(vtf.equals("pink_petals")) return 1;
		if(vtf.equals("iron_bars")) return 1;
		if(vtf.endsWith("_grate")) return 1;

		if(vtf.endsWith("_trapdoor")) {
			if (vtf.endsWith("dark_oak_trapdoor")) return 0;
			if (vtf.endsWith("spruce_trapdoor")) return 0;
			if (vtf.endsWith("birch_trapdoor")) return 0;
			return 1;
		}
		if(vtf.endsWith("_door")) return 1;
		//if(vtf.endsWith("_leaves")) return 1; //may want to leave opaque due to rendering

		return 0;
	}

	// if this is true, override transparency
	public int CheckAlphaTest(String vtf){

		// check first for transparency in texture settings
		for(Object texture : this.settings.getTexturesSettings()) {
			JSONObject jobj = (JSONObject) texture;
			if (jobj.get("textureName").toString().endsWith("_leaves")) {
				//System.out.println("jobj.get(\"textureName\").toString(): "+ jobj.get("textureName").toString());
				if (jobj.get("textureName").toString().equals(vtf)) {
					boolean transparent = (boolean) jobj.get("transparent");
					System.out.println("transparent: "+ transparent);
					if(transparent) return 1;
					else return 0;
				}
			}
		}
		if(vtf.endsWith("_trapdoor")) {
			if (vtf.endsWith("dark_oak_trapdoor")) return 0;
			if (vtf.endsWith("spruce_trapdoor")) return 0;
			if (vtf.endsWith("birch_trapdoor")) return 0;
			return 1;
		}
		if (vtf.endsWith("_leaves")) return 1;
		return 0;
	}

	public static String setColor(String vtf) {
//		if (vtf.equals("oak_leaves")) return thisColor("[0.34 0.71 0.07]");
//		if (vtf.equals("acacia_leaves")) return thisColor("[0.34 0.71 0.07]");
//		if (vtf.equals("jungle_leaves")) return thisColor("[0.34 0.71 0.07]");
//		if (vtf.equals("spruce_leaves")) return thisColor("[0.34 0.71 0.07]");
//		if (vtf.equals("dark_oak_leaves")) return thisColor("[0.34 0.71 0.07]");
//		if (vtf.equals("birch_leaves")) return thisColor("[0.34 0.71 0.07]");
//		if (vtf.equals("vine")) return thisColor("[0.34 0.71 0.07]");
//		if (vtf.equals("lily_pad")) return thisColor("[0.55 0.76 0.0]");
//		if (vtf.equals("grass_block_top")) return thisColor("[0.55 0.76 0.0]");
		//if (vtf.contains("water")) return thisColor("[0.23 0.27 0.80]");

		return "";
	}

	public static String setOtherProps(String vtf) {
		if (vtf.equals("glowstone")) return propValue("selfillum", "1");
		if (vtf.contains("lava")) return propValue("selfillum", "1");
		if (vtf.equals("redstone_lamp_on")) return propValue("selfillum", "1");
		if (vtf.equals("water_still")) return MakeWater();
		if (vtf.equals("water_flow")) return MakeWater();

		return "";
	}

	private static String propValue(String prop, String val) {
		StringBuilder sb = new StringBuilder();
		prop = prop.replace("$", "");
		sb.append("\t\"").append("$").append(prop).append("\" \"").append(val).append("\"\n");
		return sb.toString();
		//return "\t$"+ prop +" \"" + val + "\"\n";
	}

	private static String thisColor(String c) {
		return "\t\"$color\" \"" + c + "\"\n";
	}

	private static String MakeWater() {
		String body =
				"\t\"$translucent\" 1\n" +
				"\t\"$abovewater\" 1   //This is what tell the water to be on top.\n" +
				"\t\"%compilewater\" 1 // to compile as water\n" +
				"\t\"$surfaceprop\" \"water\" //for physics\n" +
				"\t\"$fogenable\" 1 // set to 0 for no fog\n" +
				"\t\"$fogcolor\" \"[.20 .10 .60]\" // RGB setting for color of the fog. 0 is white 1 is black.\n" +
				"\t\"$fogstart\" 0 // keep this a 0 or the fog will not look right\n" +
				"\t\"$fogend\" 500 //larger the # the farther the fog will start from the player.\n" +
				"\t\"$bottommaterial\" \"minecraft_original/water_still\" //see below for waterunder.vmt\n" +
				" ";
		return body;
	}

	/** Make VMTs for Animated vtfs in \vtfs2, outputs \MaterialsDir */
	public static void MakeAnimVMTs() {

		guiStartRef.set_progress_label("Making Animated VMTs...", false, "");
		System.out.println(dash+"Making Animated VMTs..."+dash);

		//TODO needs animation frames and done properly done before generating animated vmts.
		// is this still needed since its added in the json data? Is this separate from that?
		// maybe json sata is models only, where this is textures of base folder.
		File f = new File(VTFs2MatDir);
		String outputPath = f.toString();
		
		if(!f.exists()) f.mkdirs();
		
		//make vmts for folder (f)
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
		ArrayList<String> vmts = new ArrayList<String>();
		
		for(int i = 0; i < names.size(); i++) {
			if(names.get(i).endsWith(".vtf")) {
				vmts.add(names.get(i));
			}
		}
		//System.out.println(vmts);
		//TODO
		// should these go into vtfs or vtf2?
		String path = VTFsMatDir;
		String vmt = "";
		
		//create VMT file for every name.vtf
		for(int i = 0; i < vmts.size(); i++) {

			String VMT = vmts.get(i);
			// n is filename without _00 suffix
			String n = VMT.substring(0, VMT.lastIndexOf("_"));
			String matdir = VTFsMatDir.split("materials/")[1];
			//String matdir = VTFsMatDir.replace(VTFs1, "");

			//get base vtf so only runs once
			if(vmts.get(i).equals(n+"_0.vtf")) {

				// Get-Set frame rate, Alphatest value
				int fr = AnimVMTProps.animVMTData.get(n).get(1);
				int at = AnimVMTProps.animVMTData.get(n).get(2);
				
				//take names and create a vmt for all
				vmt = ""
					+ "\"LightmappedGeneric\"\n"
					+ "{\n"
					+ "	\"$basetexture\" \""+ matdir +""+ n +"\"\n"
					+ "	\"$alphatest\" "+at+"\n"
					+ setOtherProps(n)
					+ "\n"
					+ "\tProxies\n"
					+ "\t{\n"
					+ "\t\tAnimatedTexture\n"
					+ "\t\t{\n"
					+ "\t\t\tanimatedTextureVar \"$basetexture\"\n"
					+ "\t\t\tanimatedTextureFrameNumVar \"$frame\"\n"
					+ "\t\t\tanimatedTextureFrameRate "+fr+"\n"
					+ "\t\t}\n"
					+ "\t}\n"
					+ "}";
					
				WriteFile(vmt, path+"\\", n+".vmt");
			}
		}
		guiStartRef.set_progress_label("Making Animated VMTs...Done!", true, "");
		System.out.println("Making Animated VMTs...Done!");
	}
	
	private static void WriteFile(String toWrite, String path, String name) {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path + name));

			writer.write(toWrite); 
			writer.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void InterpolateFrames(String filepath, int totalFrames) throws IOException {
		
		//get all files by file name ending in a number
		//get path, separate directory by name
		//take _number off end and add back manually
		//get all files by that name
		//file split by _
		//get length, remove last index of String[]
		//return the rest
		//add to array of filenames to iterate through
		//img1 = arr[0], img2 = arr[1]
		//img1 = arr[1], img2 = arr[2] etc until length -1 is reached

		// your directory
		boolean framesArr = false;
		String outputfile = Generated+"\\";
		
		File f = new File(outputfile);
		if(!f.exists()) f.mkdirs();
		
		int ncnt = 0;
		//System.out.println(filepath+" <-fp | tf-> "+totalFrames);
		
		int sl = filepath.lastIndexOf("\\");
		File fs = new File(filepath.substring(0, sl+1));
		String fn = filepath.substring(sl+1, filepath.length()).replace(".png", "");
		
		//only works if name ends with _00 double digits, add 4 for .png
		int nameLength = fn.length() + 3 + 4;
		
		File[] matchingFiles = fs.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith(fn) && name.endsWith("png") && name.length() == nameLength;
		    }
		});
		
		if(framesArr) {
			
			ArrayList<String> arrS = new ArrayList<String>();
			//sort by pattern (numbers)
			arrS = sortNumbers(matchingFiles, numbers);
			
			for(int v = 0; v < matchingFiles.length; v++) {
				
				matchingFiles[v] = new File(arrS.get(v));
			}
			
		} else {
		
			//sort files by from zero
			sortByNumber(matchingFiles);
		}
		
		for(int c = 0; c < matchingFiles.length; c++) {
		
			if(totalFrames == 0) {
				
				//copy files to generated without interpolating
				f = new File(outputfile + fn +"_"+ ncnt +".png");
		        copyFile(matchingFiles[c], f);    
				ncnt++;
				
			} else {
				
				//interpolate between files to smooth animations
				String img1 = "";
				String img2 = "";
		
				img1 = matchingFiles[c].toString();
	
				//if last image, interpolate between last and first
				if(c == matchingFiles.length-1)
					img2 = matchingFiles[0].toString();
				else
					img2 = matchingFiles[c+1].toString();
				
				//System.out.println(img1+", "+ img2);
				
				File f1 = new File(img1);
				File f2 = new File(img2);
		
				String imgPath1 = f1.getPath();
				String imgPath2 = f2.getPath();
				
				BufferedImage frame1 = ImageIO.read(new File(imgPath1));
				BufferedImage frame2 = ImageIO.read(new File(imgPath2));
				BufferedImage image = null;
				
				ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
				
				//initialize all images needed
				for(int x = 0; x < totalFrames; x++) {
					
					image = new BufferedImage(frame1.getWidth(), frame1.getHeight(), BufferedImage.TYPE_INT_ARGB);
					images.add(image);
					
				}
				//create image from source image
				BufferedImage sourceImage = new BufferedImage(frame1.getWidth(), frame1.getHeight(), BufferedImage.TYPE_INT_ARGB);
				
				int r1 = 0, g1 = 0, b1 = 0, a1 = 0; //first image
				int r2 = 0, g2 = 0, b2 = 0, a2 = 0; //second image
				int r3 = 0, g3 = 0, b3 = 0, a3 = 0; //resulting image
				
				//get all pixels of x and y
				for(int w = 0; w < frame1.getWidth(); w++) {
					
					for(int h = 0; h < frame1.getHeight(); h++) {
						
						
						//get pixel, convert to rgba
						int p1 = frame1.getRGB(w, h);
						getRGB(p1);
						r1 = r; g1 = g; b1 = b; a1 = a;
						
						int p2 = frame2.getRGB(w, h);
						getRGB(p2);
						r2 = r; g2 = g; b2 = b; a2 = a;
						
						//interpolate between frames (totalFrames) amount of times
						for(int i = 0; i < totalFrames; i++) {
							
							int frames = totalFrames + 1;
							//get highest number to subtract from
							//highestNum - lowestNum, divide by frames to add, add frame1 times iteration

							// is only writing the interpolated frame? not the others?
							//if only one frame to make between frames, same amount will be made
							//before doing frames + 1, was doing the same as first frame, so final count is the same
							//now it will get the difference between the 2 images and only make that one, still same count
							//we need to write the others 2 frames also
							//either rewrite them or copy them
							//if copying, make sure to only write one, probably the first one
							
							//if totalFrames = 2
							
							//200 > 100 r3 = 200 - (200 - 100) / 3) * (0 + 1) r3 = 200 - 33 | r3 = 166
							//200 > 100 r3 = 200 - (200 - 100) / 3) * (1 + 1) r3 = 200 - 66 | r3 = 133
							
							//if totalFrames = 1
							
							//200 > 100 r3 = 200 - (200 - 100) / 2) * (0 + 1) r3 = 200 - 50 | r3 = 150
							
							//if totalFrames = 3
							
							//200 > 100 r3 = 200 - (200 - 100) / 4) * (0 + 1) r3 = 200 - 25 | r3 = 175
							//200 > 100 r3 = 200 - (200 - 100) / 4) * (1 + 1) r3 = 200 - 50 | r3 = 150
							//200 > 100 r3 = 200 - (200 - 100) / 4) * (2 + 1) r3 = 200 - 75 | r3 = 125
							
							// R
							if(r1 > r2) r3 = (int) (r1 - ((r1 - r2) / frames) * (i+1));
							else
							if(r1 < r2) r3 = (int) (r1 + ((r2 - r1) / frames) * (i+1));
							else r3 = r1;
							
							// G
							if(g1 > g2) g3 = (int) (g1 - ((g1 - g2) / frames) * (i+1));
							else
							if(g1 < g2) g3 = (int) (g1 + ((g2 - g1) / frames) * (i+1));
							else g3 = g1;
	
							// B
							if(b1 > b2) b3 = (int) (b1 - ((b1 - b2) / frames) * (i+1));
							else
							if(b1 < b2) b3 = (int) (b1 + ((b2 - b1) / frames) * (i+1));
							else b3 = b1;
							
							// A
							if(a1 > a2) a3 = (int) (a1 - ((a1 - a2) / frames) * (i+1));
							else
							if(a1 < a2) a3 = (int) (a1 + ((a2 - a1) / frames) * (i+1));
							else a3 = a1;
							
							//convert back to pixel
							int p = (a3 << 24) | (r3 << 16) | (g3 << 8) | b3;

							//make image from pixels
							images.get(i).setRGB(w, h, p);
							
							//make image from pixels
							//do once for source image
							if(i == 0) {
								
								//Source Image, convert back to pixel
								int sp = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
								sourceImage.setRGB(w, h, sp);
							}
							
							if(h == 4 && w == 4) {
								
								//System.out.println("r1: "+r1+" g1: "+g1+" b1: "+b1+" a1: "+a1);
								//System.out.println("r3: "+r3+" g3: "+g3+" b3: "+b3+" a3: "+a3+"<--result " + (i+1));
								//System.out.println("r2: "+r2+" g2: "+g2+" b2: "+b2+" a2: "+a2);
							}
						}	
					}
				}
				
				f = new File(outputfile + fn +"_"+ ncnt +".png");
			    ncnt++;
			   // copyFile(matchingFiles[c], f);
			    ImageIO.write(sourceImage, "png", f);
			    
			    //System.out.println("Writing file: "+f+"<-sourceImage");
			    
				//for each image created, write to file
				for(int x = 0; x < images.size(); x++) {
					
					// write image
			        try {
			            f = new File(
			                outputfile + fn +"_"+ ncnt +".png");

			            ImageIO.write(images.get(x), "png", f);
			            ncnt++;
			            //System.out.println("Writing file: "+f);
			        }
			        catch (IOException e) {
			            System.out.println(e);
			        }
				}
			}
		}
	}
	private static void getMetaData() {}
	
	@SuppressWarnings("resource")
	public static boolean copyFile(File sourceFile, File destFile) throws IOException {
	    if (!sourceFile.exists()) {
	        return false;
	    }
	    if (!destFile.exists()) {
			if(destFile.getParentFile().mkdirs())
	        	destFile.createNewFile();
	    }
	    FileChannel source = null;
	    FileChannel destination = null;
	    source = new FileInputStream(sourceFile).getChannel();
	    destination = new FileOutputStream(destFile).getChannel();
		long bytesWritten = 0;
	    if (destination != null && source != null) {
			bytesWritten = destination.transferFrom(source, 0, source.size());
	    }
	    if (source != null) {
	        source.close();
	    }
	    if (destination != null) {
	        destination.close();
	    }
		if(bytesWritten > 0) return true;
		else return false;

	}
	public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
		File sourceDirectory = new File(sourceDirectoryLocation);
		File destinationDirectory = new File(destinationDirectoryLocation);
		FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
	}

    public static void sortByNumber(File[] files) {

		Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i = 0;
                try {
                    int s = name.indexOf('_')+1;
                    int e = name.lastIndexOf('.');
                    String number = name.substring(s, e);
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    i = 0; // if filename does not match the format
                           // then default to 0
                }
                return i;
            }
        });

        //for(File f : files) {
        //    System.out.println(f.getName());
        //}
    }//16-31, 0-15

    private static int[] numbers = {16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,
    		29, 30, 31, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    public static ArrayList<String> sortNumbers(File[] sourceFiles, int[] numbers){
    	
    	ArrayList<String> arr = new ArrayList<String>();

    	for(int i : numbers) {
    		
    		for(File f : sourceFiles) {
    			
    			int num = extractNumber(f.toString());
    			
    			if(i == num) {
    				arr.add(f.toString());
    				//System.out.println(f.toString()+"<-----------------"+num);
    			}
    		}
    	}

    	return arr;
    	
    }

    private static int extractNumber(String name) {
        
    	int i = 0;
        try {
            int s = name.lastIndexOf('_');
            
            int e = name.lastIndexOf('.');
            String number = name.substring(s, e);
            
            if(number.contains("_0"))
            	number = number.replace("_0", "");
            else number = number.replace("_", "");

            i = Integer.parseInt(number);
        } catch(Exception e) {

            i = 0; // if filename does not match the format
                   // then default to 0
        }
        return i;
    }

    private static int r = 0, g = 0, b = 0, a = 0;
	private static void getRGB(int rawRGB) {
		
		a = (rawRGB >> 24) & 0xff;
		r = (rawRGB >> 16) & 0xff;
		g = (rawRGB >> 8) & 0xff;
		b = rawRGB & 0xff;
	}
}

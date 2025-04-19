package MCContentConvertor;

import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class TextureGetter implements Paths {

	static QCFunctions qc = new QCFunctions();
	private static int fileCount = 0;
	public static ArrayList<String> animatedVTFs = new ArrayList<String>();

	/**
	 * Extract pngs from jar
	 * @param jarFile
	 * @param destDir
	 * @return True on success
	 * @throws IOException
	 */
	public static boolean ExtractJar(String jarFile, String destDir) throws IOException {
		
		//print to GUI Extracting Textures...
		//GUIStart.set_progress_label("Extracting Textures...", false);
		System.out.println(dash+"Extracting Textures..."+dash);

		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> enumEntries = jar.entries();
		while (enumEntries.hasMoreElements()) {
		    JarEntry file = (JarEntry) enumEntries.nextElement();
		   
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
						
						//fileCount++;
						
						//set variable to indicate true to run this function AFTER extraction
						splitTexture = true;
					}

					//InputStream is = jar.getInputStream(file);
					//FileOutputStream fos = new FileOutputStream(f);
					if(!isMeta) {
						InputStream is = jar.getInputStream(file); // get the input stream
						FileOutputStream fos = new FileOutputStream(f);
						while (is.available() > 0) {  // write contents of 'is' to 'fos'

							fos.write(is.read());
						}
						fos.close();
						is.close();
					}
				    else {
						//System.out.println("meta: " + file.getName());
				    	//read meta data into array
				    	/////String json = new Gson().toJson(fos);
				    	//////System.out.println(json+"GSON json file");
						//ArrayList<String> aLines = new ArrayList<String>();
						//BufferedReader br = new BufferedReader(new FileReader(f));
						
						//String line = "";
						//while((line += br.readLine()) != null) {
							//aLines.add(line);
							//System.out.println(line);
						//}
						//br.close();
						//System.out.println(line+"<----");
						
						//parse line into json object
				    }
				    
				    if(splitTexture && !isMeta) splitTextures(f);
				    //fos.close();
				    //is.close();
				}
			}
		}
		jar.close();
		
		//print to GUI Processing label
		//GUIStart.set_progress_label("Done!", true);
		System.out.println("Extracting Textures...Done!");
		
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
	
	public static void MakeVTFs() throws IOException, InterruptedException, ParseException {
		
		//get batch file or create batch file with properties
		//and set path of pngs. set output folder to vtfs for now.
		//need to do non recursive, when done, change input to /entity and output to /entity
		//need to get these directories from mat paths i did

		System.out.println(dash+"Checking Models for Texture Paths..."+dash);

		/** folder path for current model material path retreived from
		 * getUniquePaths function, switched on array*/
		String modelMatPath = "";
		List<String> arr = qc.getDirUniquePaths(false);
		//arr.add("\\entity\\uvmap");
		arr.forEach(s->System.out.println("arr: "+s));
		System.out.println("Checking Models for Texture Paths...Done!");
		//print to GUI Processing label
		//GUIStart.set_progress_label("Making VTFs...", false);

		System.out.println(dash+"Making VTFs..."+dash);

		ArrayList<String> tempFolders = new ArrayList<>();

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
			if(modelMatPath.equals("\\block")) modelMatPath2 = "";

			//path to model's materials (modelMatPath)
			String prefix = "\\assets\\minecraft\\textures";


			String inputFolder = PngsMaterialsDir + prefix + modelMatPath +"*.png";
			String f = inputFolder.replace("*.png", "");

			boolean isBlock = false;
			if(modelMatPath.equals("\\block")) {
				isBlock = true;
				inputFolder = PngsMaterialsDir + prefix + modelMatPath +"\\*.png";
				f = inputFolder.replace("\\*.png", "");
			}

			File file = new File(f);
			// dont need to recheck our files of temp folders
			// !animation textures not converting because its runs through this
			// and is ...
			if(!convertingTemp && 1 == 2) {
				// scan all files for dimensions other than 16x16
				// put references to all in array
				// reprocess later one at a time with a custom dimension
				int size = file.listFiles().length;
				for (int x = 0; x < size; x++) {

					File ff = Objects.requireNonNull(file.listFiles())[x].getAbsoluteFile();
					String name = ff.getName();
					if (ff.getName().endsWith(".png") && ff.getAbsolutePath().contains("textures\\entity\\")) {

						BufferedImage bimg = ImageIO.read(ff);
						if (bimg.getHeight() != bimg.getWidth()) {

							// make folder in textures named temp32x64 or whatever the height by width is
							// copy to this folder, the file
							// add this folder to the queue of subfolders to be checked
							// check if subfolder is this one and convert all of and
							// put them where they go, how do we know where they go? by there original path?
							// should we wait for everything to be done converting before adding to queue? yes wait until
							// all folders have been populated with the files
							System.out.println("ff: " + ff);
							//make destination by using height and width
							String tempFolder = "temp" + bimg.getHeight() + "x" + bimg.getWidth();
							//String ffFolder = ff.getAbsolutePath().replace(ff.getName(), "").replace("entity", tempFolder);
							String ffFolder = ff.getAbsolutePath().replace("entity", tempFolder);
							File ffDest = new File(ffFolder);

							// copy file to temp folder of dimensions
							// folder should be created if not exists
							System.out.println("copyfile from: "+ff);
							System.out.println("copyfile to: "+ ffDest);
							copyFile(ff, ffDest);
							String path = ffDest.toString().split("textures")[ffDest.toString().split("textures").length-1];
							String path2 = path.replace(ffDest.getName(), "").replace("\\", "/");
							System.out.println("path2: "+path2);
							tempFolders.add(path2);

//						JSONObject jobj = new JSONObject();
//						jobj.put("file", ff);
//						jobj.put("height", bimg.getHeight());
//						jobj.put("width", bimg.getWidth());
//						toConvertDems.add(jobj);
						}
					}
				}
			}

			if(modelMatPath2.endsWith("/")) {
				int  lastSlash = modelMatPath2.lastIndexOf("/");
				modelMatPath2 = modelMatPath2.substring(0, lastSlash);
				System.out.println("subFolder2: "+ modelMatPath2);
			}
			String outputFolder = VTFsMatDir + modelMatPath2;
			if(convertingTemp) {
				// change output folder to use the files original path instead
				// so signs would be /entity/signs/hanging/
				// instead of /entity/temp32x16/

			}
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
					+ "src\\VTFEdit\\bin\\x64\\vtfcmd.exe"
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
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(UserDir + "\\" + fileName));
			
			//Create bat file
			writer.write(batContent); 
			writer.close();
			//System.out.println(UserDir + "\\" + fileName);


			
			// Execute command
	       /*String[] args = {
	    		   "cmd.exe", "/C", "Start /B",
	    		  //"cmd.exe", "/c /B", "Start",
	    		   UserDir + "\\" + "convert"+(i+1)+".bat"
	        	};*/

		   /*String[] args = {
				   "cmd.exe",
				   "/C",
				   "Start",
				   "/B",
				   UserDir + "\\" + "convert"+(i+1)+".bat"
		   };
		   System.out.println(Arrays.toString(args));
		   Process p = Runtime.getRuntime().exec(args);
		   int returned = p.waitFor();
		   //int returned = 0;
		   //System.out.println("returned: "+returned);
		   //System.out.println(outputFolder);
		   if(i == 0) System.out.println("first stage done, incrementing...");*/

			// execute bat file
			int code = 1;
			try {
				ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "convert"+(i+1)+".bat");
				pb.directory(new File(UserDir));
				Process process = pb.start();
				code = process.waitFor();
				System.out.println("Waiting for process...");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		   if(code >= 0) {
			   if(code > 0) {
				   //something didnt go right
				   System.out.println("Process returned "+ code + " for subFolder: "+ modelMatPath);
			   } // else here to stop the program ...
				i++;

				//if array is on block, subfolder2 will equal "".
				//check if all files are there before running next bat
				if(modelMatPath2.isEmpty()) {
					//TODO make temp folder to put files in to get proper count number, then move to folder
					int fiCount = PNGFolderInput.listFiles().length;
					//check if all files extracted were made into VTFs, then continue
					//DOES NOT RUN IF OLD FILE STILL IN FOLDER!	Delete folder before starting or something
					System.out.println("Converting VTFs 0 / "+fiCount);
					while(folderOutput.listFiles().length < fiCount-1) {
						//System.out.println(folderOutput.listFiles().length+" <--length|fileCount--> "+fiCount);
					}
					System.out.println("Converting VTFs "+folderOutput.listFiles().length+" / "+fiCount);
				}
		   }
		   	// if we are at the last element of the array,
			// add our temp folders to be converted
			if(i == (arr.size()) && !convertingTemp) {

				//arr.addAll(getUniqueOfList(tempFolders));
				//arr.forEach(a ->System.out.println("paths of arr: "+ a));
				// last step
				// should have toConvertDems populated with every file
				// need to check for
				// check for any temp folder in textures folder
//				File texturesDir = new File(PngsMaterialsDir + prefix);
//				File[] files = texturesDir.listFiles();
//				for(int x = 0; x < files.length; x++) {
//					if(files[x].isDirectory() && files[x].getName().contains("temp")) {
//						// add subfolder, removing path to it( all before /temp16x32 )
//
//						// of the full path, get its folders
//						System.out.println("files[x]: "+ files[x]);
//						File newFiles = new File(files[x].toString() + "/");
//						File[] tempfiles = newFiles.listFiles();
//						for(File fff : tempfiles) {
//							System.out.println("tempfile: "+fff);
//						}
//
//                        assert tempfiles != null;
//                        String tempF = tempfiles[tempfiles.length - 1].toString().replace(tempfiles[tempfiles.length - 1].getName(), "");
//						System.out.println("tempF: "+tempF);
//
//						//String addName = files[x].getAbsolutePath().split(texturesDir.toString())[1];
//						System.out.println("files[x]: "+ files[x]);
//						System.out.println("tempfiles[0]: "+ tempfiles[0]);
//						//String addName = files[x].toString().split("textures")[2] +"/";
//						// add the full path to the files instead of just temp folder
//						String tmpf = tempfiles[0].getParent();
//						//String addName = tempF.split("textures")[2];
//						System.out.println("tmpf: "+tmpf);
//						String addName = tmpf.split("textures")[tmpf.split("textures").length-1];
//						System.out.println("addName: "+ addName);
//
//						arr.add(addName);
//					}
//				}
			}
		}
		//MakeVMTs();
		
		//print to GUI Processing label
		//GUIStart.set_progress_label("Done!", true);
		System.out.println("Making VTFs...Done!");
		
		//MakeAnimVTFs();
	}
	public static ArrayList<String> getUniqueOfList(ArrayList<String> p) {

		for(int i = 0; i < p.size(); i++) {
			String str1 = p.get(i);
			//look for same string in array
			for(int a=0; a<p.size(); a++) {
				if(str1.equals(p.get(a))) {
					//remove all copies, replace with one
					p.remove(a);
					a--;
				}
			}
			p.add(str1);
		}
		return p;
	}
	public static void MakeAnimVTFs() throws IOException, InterruptedException {
		
		//print to GUI Processing label
		//GUIStart.set_progress_label("Making Animated VTFs...", false);
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
				+ "VTFEdit\\bin\\x64\\vtfcmd.exe"
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
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(UserDir + "\\" + fileName));
		
		//make bat file
		writer.write(batContent); 
		writer.close();
		System.out.println(UserDir + "/" + fileName);
		
		// Execute command
       String[] args = {
    		   "cmd.exe", "/C", "Start /B",
    		   UserDir + "\\" + "convertAnim.bat"
        	};
       
       Process p = Runtime.getRuntime().exec(args);
       int returned = p.waitFor();
       	
       if(returned == 0) {
        	System.out.println(returned);
        	System.out.println(outputFolder);
        	
        	//if directory is already populated with files, this wont work!
        	//either delete manually, through the script or remove this
        	//delete generated folder, delete vtfs2 folder before starting, maybe vtfs also
        	//same for the other while loop in makevtfs()

        	while(f.listFiles().length < fiCount) {

    		}
        	System.out.println(f.listFiles().length+" - 100% - "+fiCount);
		   System.out.println("Making Animated VTFs...Done!");
        	//MakeAnimVMTs();
		   // move animated vtf from vtf2 to vtf folder
		   // only move vtf of name ending with zero, the first frame
		   // this will be default until animations are generated (manually for now)
		   MoveAnimVTF(f);
       }
	}
	public static void MoveAnimVTF(File f) throws IOException {

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
		System.out.println("Moving Animated VTFs to Textures Folder...Done!");
	}
	public static void MakeVMTs() {


		System.out.println(dash+"Making VMTs..."+dash);

		// runs before all vtfs are fully generated. needs to wait or do something
		// get files size, wait for 1 second, check size, if the same continue
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
		//System.out.println("vmts size: "+ vmts.size());
		for(int i = 0; i < vmts.size(); i++) {
			
			String matdir = VTFsMatDir.replace(VTFs1, "");
			String n = vmts.get(i).split("\\.")[0];

			// check for transparent and place
			int translucent = CheckTranslucent(n);
			String color = "";
			//take names and create a vmt for all
			vmt = ""
				+ ""
				+ "\"LightmappedGeneric\"\n"
				+ "{\n"
				+ "	\"$basetexture\" \""+ matdir +"/"+ n +"\"\n"
				+ "	\"$translucent\" "+translucent+"\n"
				+ setColor(n)
				+ "}";

			WriteFile(vmt, path+"\\", n+".vmt");
		}
		System.out.println("Making VMTs...Done!");
	}
	public static int CheckTranslucent(String vtf) {
		if(vtf.contains("glass")) return 1;
		if(vtf.contains("vine")) return 1;
		if(vtf.contains("lichen")) return 1;
		if(vtf.contains("ladder")) return 1;
		if(vtf.contains("vein")) return 1;
		if(vtf.contains("water")) return 1;
		if(vtf.equals("rail")) return 1;
		if(vtf.equals("lily_pad")) return 1;
		if(vtf.equals("ice")) return 1;
		if(vtf.equals("pink_petals")) return 1;
		if(vtf.equals("iron_bars")) return 1;
		if(vtf.endsWith("_grate")) return 1;
		if(vtf.endsWith("_trapdoor")) return 1;
		if(vtf.endsWith("_door")) return 1;
		if(vtf.endsWith("_leaves")) return 1; //may want to leave opaque due to rendering

		return 0;
	}
	public static String setColor(String vtf) {
		if (vtf.equals("oak_leaves")) return thisColor("[0.34 0.71 0.07]");
		if (vtf.equals("acacia_leaves")) return thisColor("[0.34 0.71 0.07]");
		if (vtf.equals("jungle_leaves")) return thisColor("[0.34 0.71 0.07]");
		if (vtf.equals("spruce_leaves")) return thisColor("[0.34 0.71 0.07]");
		if (vtf.equals("dark_oak_leaves")) return thisColor("[0.34 0.71 0.07]");
		if (vtf.equals("birch_leaves")) return thisColor("[0.34 0.71 0.07]");
		if (vtf.equals("vine")) return thisColor("[0.34 0.71 0.07]");
		if (vtf.equals("lily_pad")) return thisColor("[0.55 0.76 0.0]");
		if (vtf.equals("grass_block_top")) return thisColor("[0.55 0.76 0.0]");

		return "";
	}
	private static String thisColor(String c) {
		//String c = "(0, 0, 0)";
		return "\t$color \"" + c + "\"\n";
	}


	/** Make VMTs for Animated vtfs in \vtfs2, outputs \MaterialsDir */
	public static void MakeAnimVMTs() {


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
		String path = VTFsMatDir;
		String vmt = "";
		
		//create VMT file for every name.vtf
		for(int i = 0; i < vmts.size(); i++) {
			
			String VMT = vmts.get(i);
			String n = VMT.substring(0, VMT.lastIndexOf("_"));
			String matdir = VTFsMatDir.replace(VTFs1, "");

			//get base vtf so only runs once
			if(vmts.get(i).equals(n+"_0.vtf")) {

				// Get-Set frame rate, Alphatest value
				int fr = AnimVMTProps.animVMTData.get(n).get(1);
				int at = AnimVMTProps.animVMTData.get(n).get(2);
				
				//take names and create a vmt for all
				vmt = ""
					+ "\"LightmappedGeneric\"\n"
					+ "{\n"
					+ "	\"$basetexture\" \""+ matdir +"\\"+ n +"\"\n"
					+ "	\"$alphatest\" "+at+"\n"
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
	private static void getMetaData() {
		
		
	}
	
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

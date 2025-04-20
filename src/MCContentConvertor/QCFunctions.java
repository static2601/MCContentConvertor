package MCContentConvertor;

import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class QCFunctions implements Paths {

	/** Scale to set in QC for model size. 
	*  A scale of 48 would be 1.0. Scale = SetScale / 48 */
	//public static String scale = mdlScale;
	
	protected static boolean PrepareCompiler(String studioMdlPath, String gameFolder, String QCName, String QCPath) {
		
		//path to compiler
		String fileName = "compiler.bat";
		String q = "\"";
		String batContent = 
				q+ studioMdlPath +q 
				+ " -game "+q+ gameFolder + q
				+ " -nop4 "
				+ "models/" + QCName;
		
		boolean testwriteOnly = false;
		
		if(!testwriteOnly) {
			try {
				//File theDir = new File(path);
				//if (!theDir.exists()){
				//	theDir.mkdirs();
				//}
				BufferedWriter writer = new BufferedWriter(new FileWriter(QCPath + fileName));
				
				writer.write(batContent); 
				writer.close();
				System.out.println("Written Content \n"+ QCPath + fileName +"\n");
				System.out.println("batContent:\n"+ batContent);
				return true;
				
			} catch (IOException e) {
				
				e.printStackTrace();
				return false;
			}
		} else {
			System.out.println("Written Content \n"+ QCPath + fileName +"\n");
			System.out.println("batContent:\n"+ batContent);
			return true;
		}
	}
	
	protected static void CompileModel(String QCPath, String QCName) throws IOException, InterruptedException {
		
		//will need to use other paths. eg css, garrysmod, etc
		String gameFolder = "D:/Steam/steamapps/common/Team Fortress 2/tf";
		String binFolder = gameFolder.replace("tf", "bin/");
		String studioMdlPath = binFolder + "studiomdl";
		
		//Create bat file
		if(PrepareCompiler(studioMdlPath, gameFolder, QCName, QCPath)) {
			// Execute command
	        String[] args = {
	        		"cmd.exe",
	        		"/C",
	        		"Start",
	        		QCPath + "compiler"
	        		};
	        Process p = Runtime.getRuntime().exec(args);
	        System.out.println(p.waitFor());//needs to wait for each to 
	        //finish before doing the next one
	        
		} else System.out.println("Compiler failed.");
	}
	
	/** Get all texture folders in use in a model minus duplicates
	 *  so we know what paths we need for model textures
	 * @return Array of path directories */
	public List<String> getDirUniquePaths(boolean addPrefix) throws IOException, ParseException {
		List<String> p = new ArrayList<String>();
		String str = "";
		String prefix = "assets\\minecraft\\textures\\";
		if(!addPrefix) prefix = "";

		// TODO adjust for new json data file
		// of getJsonData, get all paths of vtfLocations values
		getJsonData data = new getJsonData();
		//loop through array looking for all paths
		ArrayList<String> paths = new ArrayList<>();
		System.out.println("data.modelData.get(0).size(): "+data.modelData.get(0).size());
		for(int i = 0; i < data.modelData.get(0).size(); i++) {

			String[] values = data.getVtfLocations(i);
            p.addAll(Arrays.asList(values));

			//if less then 2, textures will be in block/
			p.add("\\block");
		}
		for(int i = 0; i < p.size(); i++) {
			String str1 = p.get(i);
			//look for same string in array
			for(int a=0; a<p.size(); a++) {
				//System.out.println("check if: "+ str1 + ":str1.equals("+p.get(a)+")");
				if(str1.equals(p.get(a))) {
					//remove all copies, replace with one
					//System.out.println("removing: s = "+ a + ", " + p.get(a));
					p.remove(a);
					a--;
				}
			} 
			p.add(str1);
		}
		//System.out.println("p: "+p);
		return p;
	}
}


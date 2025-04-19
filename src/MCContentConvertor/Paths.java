package MCContentConvertor;

public interface Paths {

	String UserDir = System.getProperty("user.dir");
	String SteamBaseDir = "D:/SteamLibrary";
	String GameDir = "steamapps/common/Team Fortress 2/tf";

	String PROPSMaterialsDir = "minecraft_prop_materials_test";
	String PROPSDir = "props/minecraft_original_test";
	//String tfDir = SteamBaseDir + "/" + GameDir;
	String tfMatDir = SteamBaseDir + "/" + GameDir + "/" + "materials";

	String dash = "\n- - - - - - - - - - - - - - - - - - - -\n";
	/** Scale to set in QC for model size.
	*  A scale of 48 would be 1.0. Scale = SetScale / 48 */

	//double scale = 1.0;
	//double scale = 1.041666666666667;

	String VTFs1 =
			UserDir + "\\textures\\vtfs";
	String VTFs2 =
			UserDir + "\\textures\\vtfs2";
	String PNGs =
			UserDir + "\\textures\\pngs";
	 String Animated =
			UserDir + "\\textures\\pngs\\minecraft_original_test\\animated";
	String Generated =
			UserDir + "\\textures\\pngs\\minecraft_original\\animated\\generated";
	/** UserDir + \textures\pngs\ + MaterialsDir */
	String PngsMaterialsDir =
			UserDir + "\\textures\\pngs\\minecraft_original";
	String VTFsMatDir =
			UserDir + "\\textures\\vtfs\\minecraft_original";
	String VTFs2MatDir =
			UserDir + "\\textures\\vtfs2\\minecraft_original";
}
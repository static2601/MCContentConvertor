package MCContentConvertor;

import java.io.File;

public interface Pathnames {

	String FS = File.separator;

	String USERDIR = System.getProperty("user.dir");

	String dash = "\n- - - - - - - - - - - - - - - - - - - -\n";

	///  name of just the temporary textures folder
	String TEXTURESTMP = "textures-tmp";

	/// path to our temporary 'textures' folder, will be deleted on start
	String TEMPTEXTURES = USERDIR + "\\" + TEXTURESTMP;

	String VTFCMDEXE = USERDIR + "\\Assets\\VTFEdit\\bin\\x64\\VTFCmd.exe";

	String PNGs =
			TEMPTEXTURES + "\\pngs";

	String Animated =
			TEMPTEXTURES + "\\pngs\\minecraft_original_test\\animated";

	String Generated =
			TEMPTEXTURES + "\\pngs\\minecraft_original\\animated\\generated";

	/** UserDir + \textures\pngs\ + MaterialsDir */
	String PngsMaterialsDir =
			TEMPTEXTURES + "\\pngs\\minecraft_original";

	String VTFSMATDIR =
			TEMPTEXTURES + "\\vtfs";

	String VTFs2MatDir =
			TEMPTEXTURES + "\\vtfs2\\minecraft_original";

	String ANIMTEXTEXTURES = USERDIR + "/Assets/Textures/Anims";

	String MASKS = USERDIR + "/Assets/Textures/Masks";

	String BATDIR = TEMPTEXTURES;

	boolean DEBUG = true;
}
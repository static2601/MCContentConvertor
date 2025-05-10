# Minecraft Content Convertor

Minecraft Content Convertor extracts all textures from the Minecraft jar and converts to vtf and vmt files
for use in Source games like Team Fortress 2, Garry'sMod and Counter Strike. This is mostly for making
the textures needed for Sourcecraft, a Minecraft world convertor originally created by Garten, but could
be used for any Source project.

This is a work in progress, I have just recently gotten it to a point where I can make a release.
All textures should be displayed properly, animations are still a work in progress and not included
yet. Many of the models have been created through blender and textured. ~~I have not uploaded a release yet~~
as I am still testing to make sure everything works as I intended. I plan on creating more documentation on
this and Sourcecraft in the future.

Initial release (pre-release) to Minecraft Content Convertor to extract textures from the Minecraft JAR for use in
vmt and vtf materials in Source Engine games. This has been stable in my testing but further work is needed for error
displaying and compatibility. Currently, this will only work on Windows since it requires creating a batch file to 
be executed to convert the png files to VTFs via VTFEditor which is included in the ZIP.

# Instructions

- unpack zip anywhere and run the bat file. Set your path to the Minecraft JAR and set your path to your Source game 
directory, typically it is something like ```"/steamapps/common/Team Fortress 2/tf/"```. If you went back from there to 
```"/Team Fortress 2/bin/"```, you would find the "Studiomdl.exe" file which is needed to compile the SMDs into MDLs 
using the QC files. Materials 
will be placed in the
  ```"/tf/materials/minecraft_original/"``` directory and Models in the ```"/tf/models/props/minecraft_original_50/"```
for example. The "_50" is the scale you set in the GUI and is needed by Sourcecraft to find which texture folder to use
for the models.


- In the "Settings.json", you can change the tint color of the tree leaves and any other texture that would be gray by 
default. Minecraft doesn't add the color until the texture is used in-game, then colors it based on the biome it's in.
The coloring is applied to the PNG before it's converted to a VTF. color can be set with the '\$color/\$color2' property 
in the VMT, but in GarrysMod, the texture will show gray if shining a flashlight on it. This is why it is set on the PNG
itself. I have set a basic color for now, more tweaking is needed. You can try making it whatever color you'd like, just 
be sure to include the hash(#) before the hex and it should work. 
I plan on adding a menu to do all this in the GUI. You can also color the water, transparency cannot be changed for water,
but any other transparent texture can be changed.

# Custom Texture Packs

I was experimenting with using custom texture packs but do to the complications of each one and the varying ways they can come,
I decided to leave it out for now. I tried a couple that worked perfectly, but others didnt work at all or broke functionality.
This is something I plan on doing more work on in the future. 

# Animated Textures

- these can be added right now, the problem is having VTFEdit convert them automatically. The only way to convert them now is
by hand. The VMTs are created with at least the basic set of properties for them. All you would have to do is the importing.

  The process: 

    - Open VTFEdit in ```"/Assets/VTFEdit/bin/x64/VTFEdit.exe"``` or your own. 
    - File -> Import -> go to ```"/textures-tmp/vtfs2/minecraft_original/"``` from your install folder.
    - Select multiple textures from name_0 to whatever it goes to. Press OK.
    - A dialog for VTF Options will come up asking how you would like to import them. 

    - General -> 
      - Normal Format, select 'RGBA8888'
      - Alpha Format, select 'RGBA8888'
      - Texture Type, select 'Animated Texture'
     
    - Resize -> 
      - Resize Method, select 'Nearest Power Of 2'
      - Resize Filter, select 'Point'
      - Sharpen Filter, select 'None'


- Uncheck Clamp, Generate Mipmaps and Generate Normals. click OK
- In Flags, check 'Point Sample'. click Save As, 'Save as Type' '.vtf' and save it as the same name as what you imported but without the '_xx' on the end.

- Once the textures you want animated are created, you will need to copy them to your 
`"/game directory/materials/minecraft_original/"`
folder. Depending on if it's used in a model, you will also have to copy them to the 
`"/game directory/materials/minecraft_prop_materials/<ModelName>/"` folder.

You would need to do this for the following:
- lantern and soul_lantern in `"/minecraft_prop_materials/Lantern/"`
- kelp and kelp_plant in `"/minecraft_prop_materials/CrossModel/"`
- seagrass, tall_seagrass_bottom and tall_seagrass_top in `"/minecraft_prop_materials/CrossModel/"`

Once these are in place, generating again will overwrite them (I will soon make it pull them from the Assets folder, 
I'll update this once I do)

Animation Play rate and order still need adjusted and will be something I add in future versions. For now, you
can adjust the play rate in the texture's VMT file under "animatedTextureFrameRate" set the number after whatever you want.
Lower is faster. Save it. It will update live in hammer if it is a texture. If a model, you will need to close and reopen the VMF.




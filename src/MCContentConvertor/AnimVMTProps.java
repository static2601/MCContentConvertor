package MCContentConvertor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO this shouldnt be needed since we can get all mcdata files
// data to determine interpolations needed?
public class AnimVMTProps extends QCFunctions {
	
	public static Map<String, List<Integer>> animVMTData = new HashMap<>();
	static {makeHashMap();} 

	public static void makeHashMap() {
		   
		//vmt( texture name, how many frames to make(interpolate), framerate to set, $alphatest 0-1 )
		vmt("blast_furnace_front_on", 10, 10, 0);//interp
		vmt("campfire_fire", 0, 2, 0);
		vmt("campfire_log_lit", 0, 20, 0);//interp
		vmt("chain_command_block_back", 0, 10, 0);//interp
		vmt("chain_command_block_conditional", 0, 10, 0);//interp
		vmt("chain_command_block_front", 0, 10, 0);//interp
		vmt("chain_command_block_side", 0, 10, 0);//interp
		vmt("command_block_back", 0, 10, 0);//interp
		vmt("command_block_conditional", 0, 10, 0);//interp
		vmt("command_block_front", 0, 10, 0);//interp
		vmt("command_block_side", 0, 10, 0);//interp
		vmt("crimson_stem", 10, 10, 0);//interp
		vmt("fire_0", 1, 0, 1);//frames 16-31, 0-15. no framesrate no interp
		vmt("fire_1", 0, 0, 1);//same? empty
		vmt("kelp", 0, 2, 1);
		vmt("kelp_plant", 0, 2, 1);
		vmt("lantern", 0, 2, 1);
		vmt("lava_flow", 0, 3, 0);
		vmt("lava_still", 0, 2, 0);//0-19, 19-1
		vmt("magma", 20, 8, 0);//interp
		vmt("nether_portal", 0, 10, 0);//nothing
		vmt("prismarine", 4, 300, 0);//0,1,0,2,0,3,0,1,2,1,3,1,0,2,1,2,3,2,0,3,1 //interp
		vmt("repeating_command_block_back", 0, 10, 0);//interp
		vmt("repeating_command_block_conditional", 0, 10, 0);//interp
		vmt("repeating_command_block_front", 0, 10, 0);//interp
		vmt("repeating_command_block_side", 0, 10, 0);//interp
		vmt("respawn_anchor_top", 0, 0, 0);
		vmt("sculk", 20, 5, 0);//interp
		vmt("sculk_catalyst_side_bloom", 0, 1, 0);
		vmt("sculk_catalyst_top_bloom", 0, 1, 0);
		vmt("sculk_sensor_tendril_active", 0, 1, 1);
		vmt("sculk_sensor_tendril_inactive", 0, 2, 1);
		vmt("sculk_shrieker_can_summon_inner_top", 10, 3, 0);//interp
		vmt("sculk_shrieker_inner_top", 10, 6, 0);//interp
		vmt("sculk_vein", 20, 5, 1);
		vmt("seagrass", 0, 2, 1);
		vmt("sea_lantern", 10, 8, 0);
		vmt("smoker_front_on", 0, 4, 0);
		vmt("soul_campfire_fire", 0, 2, 1);
		vmt("soul_campfire_log_lit", 0, 20, 1);//interp
		vmt("soul_fire_0", 0, 0, 1);//16-31, 0-15
		vmt("soul_fire_1", 0, 0, 1);//same?
		vmt("soul_lantern", 10, 8, 1);
		vmt("stonecutter_saw", 0, 1, 1);
		vmt("tall_seagrass_bottom", 0, 2, 1);
		vmt("tall_seagrass_top", 0, 2, 1);
		vmt("warped_stem", 5, 4, 0);//interp
		vmt("water_flow", 0, 6, 0);//same?
		vmt("water_still", 0, 6, 0);
		
	}
	
	private static void vmt(String name, int value1, int value2, int value3) {
			
		List<Integer> arr = new ArrayList<Integer>();
		arr.add(value1);
		arr.add(value2);
		arr.add(value3);
		animVMTData.put(name, arr);
	}
}

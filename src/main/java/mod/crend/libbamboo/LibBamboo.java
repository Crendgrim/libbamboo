package mod.crend.libbamboo;

import mod.crend.libbamboo.event.ClientEventHandler;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class LibBamboo {
	public static final String MOD_ID = "libbamboo";
	public static final String YACL_MOD_ID = "yet_another_config_lib_v3";
	public static final boolean HAS_YACL = PlatformUtils.isYaclLoaded();

	static ClientEventHandler eventHandler;

	public static void init() {
		eventHandler = new ClientEventHandler();
	}
}

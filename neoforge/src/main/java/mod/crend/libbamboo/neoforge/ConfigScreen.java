package mod.crend.libbamboo.neoforge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.opt.ConfigStore;
//? if <1.21.1 {
import net.minecraft.client.MinecraftClient;
//?} else {
/*import net.neoforged.fml.ModContainer;
*///?}
import net.minecraft.client.gui.screen.Screen;
import net.neoforged.fml.ModLoadingContext;
//? if <1.20.5 {
import net.neoforged.neoforge.client.ConfigScreenHandler;
//?} else
/*import net.neoforged.neoforge.client.gui.IConfigScreenFactory;*/

public class ConfigScreen {
	public static <T> void register(ConfigStore<T> configStore) {
		if (LibBamboo.HAS_YACL) {
			ModLoadingContext.get().registerExtensionPoint(
					//? if <1.20.5 {
					ConfigScreenHandler.ConfigScreenFactory.class,
					//?} else
					/*IConfigScreenFactory.class,*/
					() ->/*? if <1.20.5 {*/new ConfigScreenHandler.ConfigScreenFactory/*?}*/(
							(client, parentScreen) -> makeScreen(client, parentScreen, configStore)
					)
			);
		}
	}

	// Stonecutter's cache appears to have trouble with MinecraftClient vs ModContainer in different NeoForge versions,
	// so we use an explicit helper here to make sure it gets recompiled properly.
	private static <T> Screen makeScreen(
			//? if <1.21.1 {
			MinecraftClient client,
			//?} else {
			/*ModContainer container,
			*///?}
			Screen parentScreen, ConfigStore<T> configStore) {
		return configStore.makeScreen(parentScreen);
	}
}

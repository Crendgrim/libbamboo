package mod.crend.libbamboo.neoforge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.opt.ConfigStore;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ConfigScreen {
	public static <T> void register(ConfigStore<T> configStore) {
		if (LibBamboo.HAS_YACL) {
			ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
					() -> (client, parentScreen) -> configStore.makeScreen(parentScreen)
			);
		}
	}
}

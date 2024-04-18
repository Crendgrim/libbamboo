package mod.crend.libbamboo.forge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.opt.ConfigStore;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class ConfigScreen {
	public static <T> void register(ConfigStore<T> configStore) {
		if (LibBamboo.HAS_YACL) {
			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
					() -> new ConfigScreenHandler.ConfigScreenFactory(
							(minecraft, screen) -> configStore.makeScreen(screen)
					));
		}
	}
}

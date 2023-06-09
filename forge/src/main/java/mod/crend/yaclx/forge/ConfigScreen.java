package mod.crend.yaclx.forge;

import mod.crend.yaclx.YaclX;
import mod.crend.yaclx.opt.ConfigStore;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class ConfigScreen {
	public static <T> void register(ConfigStore<T> configStore) {
		if (YaclX.HAS_YACL) {
			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
					() -> new ConfigScreenHandler.ConfigScreenFactory(
							(minecraft, screen) -> configStore.makeScreen(screen)
					));
		}
	}
}

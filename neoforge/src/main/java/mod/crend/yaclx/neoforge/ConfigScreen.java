package mod.crend.yaclx.neoforge;

import mod.crend.yaclx.YaclX;
import mod.crend.yaclx.opt.ConfigStore;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.ConfigScreenHandler;

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

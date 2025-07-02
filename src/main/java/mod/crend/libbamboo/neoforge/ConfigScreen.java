//? if neoforge {
/*package mod.crend.libbamboo.neoforge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.opt.ConfigStore;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

@Deprecated
public class ConfigScreen {

	@Deprecated
	public static <T> void register(Supplier<ConfigStore<T>> configStore) {
		register(ModLoadingContext.get(), configStore);
	}

	@Deprecated
	public static <T> void register(ModLoadingContext context, Supplier<ConfigStore<T>> configStore) {
		if (LibBamboo.HAS_YACL) {
			context.registerExtensionPoint(
					IConfigScreenFactory.class,
					() -> (client, parentScreen) -> configStore.get().makeScreen(parentScreen)
			);
		}
	}
}
*///?}

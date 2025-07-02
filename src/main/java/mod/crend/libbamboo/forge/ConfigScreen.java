//? if forge {
/*package mod.crend.libbamboo.forge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.opt.ConfigStore;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.function.Supplier;

@Deprecated
public class ConfigScreen {

	//? <1.21.1 {
	@Deprecated
	public static <T> void register(Supplier<ConfigStore<T>> configStore) {
		register(ModLoadingContext.get(), configStore);
	}
	//?}

	@Deprecated
	public static <T> void register(ModLoadingContext context, Supplier<ConfigStore<T>> configStore) {
		if (LibBamboo.HAS_YACL) {
			context.registerExtensionPoint(
					ConfigScreenHandler.ConfigScreenFactory.class,
					() -> new ConfigScreenHandler.ConfigScreenFactory(
							(client, parentScreen) -> configStore.get().makeScreen(parentScreen)
					));
		}
	}
}
*///?}

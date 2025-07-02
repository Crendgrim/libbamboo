//? if forge || neoforge {
/*package mod.crend.libbamboo;

import mod.crend.libbamboo.opt.ConfigStore;
//? if forge {
/^import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
^///?} else if neoforge {
/^import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
^///?}

import java.util.function.Supplier;

public class ConfigScreen {

	//? if neoforge || <1.21.1 {
	/^public static <T> void register(Supplier<ConfigStore<T>> configStore) {
		register(ModLoadingContext.get(), configStore);
	}
	^///?}

	public static <T> void register(ModLoadingContext context, Supplier<ConfigStore<T>> configStore) {
		if (LibBamboo.HAS_YACL) {
			context.registerExtensionPoint(
					/^? if forge {^//^ConfigScreenHandler.ConfigScreenFactory.class^//^?} else {^/IConfigScreenFactory.class/^?}^/,
					() -> /^? if forge {^//^new ConfigScreenHandler.ConfigScreenFactory^//^?}^/(
							(client, parentScreen) -> configStore.get().makeScreen(parentScreen)
					));
		}
	}
}
*///?}

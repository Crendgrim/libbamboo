package mod.crend.libbamboo.fabric;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.event.GameEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class LibBambooMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LibBamboo.init();
		ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> GameEvent.TICK.invoker().tick());
	}
}

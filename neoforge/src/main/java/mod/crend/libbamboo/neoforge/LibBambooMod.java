package mod.crend.libbamboo.neoforge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.event.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@Mod(LibBamboo.MOD_ID)
@EventBusSubscriber(value = Dist.CLIENT)
public class LibBambooMod {
	@SubscribeEvent
	static void onClientTick(ClientTickEvent.Post event) {
		GameEvent.TICK.invoker().tick();
	}

	@EventBusSubscriber(modid = LibBamboo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ModEvents {
		@SubscribeEvent
		static void onClientSetup(FMLClientSetupEvent event) {
			LibBamboo.init();
		}
	}
}

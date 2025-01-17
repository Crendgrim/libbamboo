package mod.crend.libbamboo.forge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.event.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(LibBamboo.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class LibBambooMod {
	@SubscribeEvent
	static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			GameEvent.TICK.invoker().tick();
		}
	}

	@Mod.EventBusSubscriber(modid = LibBamboo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ModEvents {
		@SubscribeEvent
		static void onClientSetup(FMLClientSetupEvent event) {
			LibBamboo.init();
		}
	}
}

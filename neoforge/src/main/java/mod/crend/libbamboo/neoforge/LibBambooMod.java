package mod.crend.libbamboo.neoforge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.event.GameEvent;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.TickEvent;

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

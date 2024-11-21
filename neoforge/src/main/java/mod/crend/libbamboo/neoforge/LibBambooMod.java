package mod.crend.libbamboo.neoforge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.event.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//? if <1.20.6 {
import net.neoforged.neoforge.event.TickEvent;
//?} else {
/*import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
*///?}

@Mod(LibBamboo.MOD_ID)
//? if <1.20.6 {
@Mod.EventBusSubscriber(value = Dist.CLIENT)
//?} else
/*@EventBusSubscriber(value = Dist.CLIENT)*/
public class LibBambooMod {
	@SubscribeEvent
	static void onClientTick(/*? if <1.20.6 {*/TickEvent.ClientTickEvent/*?} else {*//*ClientTickEvent.Post*//*?}*/ event) {
		//? if <1.20.6
		if (event.phase == TickEvent.Phase.START) return;
		GameEvent.TICK.invoker().tick();
	}

	//? if <1.20.6 {
	@Mod.EventBusSubscriber(modid = LibBamboo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	//?} else
	/*@EventBusSubscriber(modid = LibBamboo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)*/
	public static class ModEvents {
		@SubscribeEvent
		static void onClientSetup(FMLClientSetupEvent event) {
			LibBamboo.init();
		}
	}
}

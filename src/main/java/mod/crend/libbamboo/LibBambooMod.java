package mod.crend.libbamboo;

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import mod.crend.libbamboo.event.GameEvent;
//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//?} else if forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//? if >=1.21.1
/^import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;^/
//? if <1.21.6 {
import net.minecraftforge.eventbus.api.SubscribeEvent;
//?} else
/^import net.minecraftforge.eventbus.api.listener.SubscribeEvent;^/
*///?} else if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}

//? if forge || neoforge
/*@Mod(LibBamboo.MOD_ID)*/
//? if fabric
@Entrypoint
public class LibBambooMod /*? if fabric {*/implements ClientModInitializer/*?}*/ {

	//? if fabric {
	@Override
	public void onInitializeClient() {
		LibBamboo.init();
		ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> GameEvent.TICK.invoker().tick());
	}
	//?}
	//? if forge {
    /*public LibBambooMod(/^? if >=1.21.1 {^//^FMLJavaModLoadingContext context^//^?}^/) {
    }
    *///?}

	//? if forge || neoforge {
    /*//? if forge {
    /^@Mod.EventBusSubscriber(modid = LibBamboo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    ^///?} else {
    @EventBusSubscriber(
			modid = LibBamboo.MOD_ID,
			//? if <1.21.9
			bus = EventBusSubscriber.Bus.MOD,
			value = Dist.CLIENT
		)
    //?}
    public static class ModBus {

        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
			LibBamboo.init();
            //? if neoforge {
            /^NeoForge.EVENT_BUS.addListener(ModBus::onClientTick);
            ^///?} else if <1.21.6 {
            MinecraftForge.EVENT_BUS.addListener(ModBus::onClientTick);
            //?} else
            /^TickEvent.ClientTickEvent.Post.BUS.addListener(ModBus::onClientTick);^/
        }

        static void onClientTick(
                //? if neoforge {
                /^ClientTickEvent.Post event
                ^///?} else if <1.21.6 {
                TickEvent.ClientTickEvent event
                //?} else
                /^TickEvent.ClientTickEvent.Post event^/
        ) {
            //? if forge && <1.21.6
            /^if (event.phase == TickEvent.Phase.START) return;^/
            GameEvent.TICK.invoker().tick();
        }
    }

    *///?}
}

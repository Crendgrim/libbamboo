package mod.crend.libbamboo.test.neoforge;

import mod.crend.libbamboo.neoforge.ConfigScreen;
import mod.crend.libbamboo.test.Config;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("libbambootest")
@EventBusSubscriber(modid = "libbambootest", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TestModNeoForge {

	public TestModNeoForge() { }

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		ConfigScreen.register(Config.CONFIG_STORE);
	}
}

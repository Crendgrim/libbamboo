package mod.crend.libbamboo.test.forge;

import mod.crend.libbamboo.forge.ConfigScreen;
import mod.crend.libbamboo.test.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("libbambootest")
@Mod.EventBusSubscriber(modid = "libbambootest", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TestModForge {

	public TestModForge() { }

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		ConfigScreen.register(Config.CONFIG_STORE);
	}
}

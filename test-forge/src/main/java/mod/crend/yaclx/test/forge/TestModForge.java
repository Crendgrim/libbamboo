package mod.crend.yaclx.test.forge;

import mod.crend.yaclx.forge.ConfigScreen;
import mod.crend.yaclx.test.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("yaclxtest")
@Mod.EventBusSubscriber(modid = "yaclxtest", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TestModForge {

	public TestModForge() { }

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		ConfigScreen.register(Config.CONFIG_STORE);
	}
}

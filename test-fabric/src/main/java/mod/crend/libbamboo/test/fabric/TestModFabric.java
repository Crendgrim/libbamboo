package mod.crend.libbamboo.test.fabric;

import mod.crend.libbamboo.auto.ConfigValidator;
import mod.crend.libbamboo.test.Config;
import net.fabricmc.api.ClientModInitializer;

public class TestModFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		assert(ConfigValidator.validate(Config.class, Config.CONFIG_STORE.config()));
	}
}

package mod.crend.yaclx.test.fabric;

import mod.crend.yaclx.auto.ConfigValidator;
import mod.crend.yaclx.test.Config;
import net.fabricmc.api.ClientModInitializer;

public class TestModFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		assert(ConfigValidator.validate(Config.class, Config.CONFIG_STORE.config()));
	}
}

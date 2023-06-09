package mod.crend.yaclx.test.fabric;

import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.crend.yaclx.test.Config;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
		return Config.CONFIG_STORE::makeScreen;
	}
}

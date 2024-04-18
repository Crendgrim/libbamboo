package mod.crend.libbamboo.test.fabric;

import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.crend.libbamboo.test.Config;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
		return Config.CONFIG_STORE::makeScreen;
	}
}

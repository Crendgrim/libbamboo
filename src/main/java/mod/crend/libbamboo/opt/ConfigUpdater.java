package mod.crend.libbamboo.opt;

import com.google.gson.JsonObject;

public interface ConfigUpdater {
	boolean updateConfigFile(JsonObject json);
}

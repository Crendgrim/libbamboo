package mod.crend.libbamboo.opt;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import mod.crend.libbamboo.*;
import mod.crend.libbamboo.auto.ConfigValidator;
import mod.crend.libbamboo.auto.annotation.AutoYaclConfig;
import mod.crend.libbamboo.type.*;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Wraps the config reading by using YetAnotherConfigLib, if present, or directly deserializes it from the config file
 * otherwise. This allows for an optional dependency on YACL for GUI configuration.
 *
 * <p>Example Usage:
 * <pre>
 * static final ConfigStore&lt;Config&gt; CONFIG_STORE = new ConfigStore<>(Config.class);
 * </pre>
 *
 * And then access the config using {@code CONFIG_STORE.config()}.
 *
 * @param <T> config class
 */
public class ConfigStore<T> {

	// Static helpers to set up a GSON deserializer in the same way that YACL does.
	// This allows us to read a config in the absence of YACL.
	public static GsonBuilder getGsonBuilder() {
		return new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.setPrettyPrinting()
				.registerTypeHierarchyAdapter(Identifier.class, new IdentifierTypeAdapter())
				.registerTypeHierarchyAdapter(Color.class, new ColorTypeAdapter())
				.registerTypeHierarchyAdapter(Item.class, new ItemTypeAdapter())
				.registerTypeHierarchyAdapter(ItemOrTag.class, new ItemOrTagTypeAdapter())
				.registerTypeHierarchyAdapter(Block.class, new BlockTypeAdapter())
				.registerTypeHierarchyAdapter(BlockOrTag.class, new BlockOrTagTypeAdapter())
				.serializeNulls();
	}

	// This field is only populated if YACL is not loaded.
	// If YACL is loaded, we defer to its config storage in AutoYacl.
	private T configInstance;
	private String modId;
	private Class<T> configClass;
	private WithYacl<T> yaclWrapper;
	private Path path;

	/**
	 * Sets up the config parsing, and takes the filename from the @AutoYaclConfig annotation on the specified config
	 * class.
	 * @param configClass the class referring to T, with fields marked as @SerialEntry.
	 */
	public ConfigStore(Class<T> configClass) {
		this(configClass, null);
	}
	/**
	 * Sets up the config parsing, and takes the filename from the @AutoYaclConfig annotation on the specified config
	 * class.
	 * @param configClass the class referring to T, with fields marked as @SerialEntry.
	 * @param updater a class specifying a config updater to prepare outdated configs before parsing.
	 */
	public ConfigStore(Class<T> configClass, ConfigUpdater updater) {
		AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
		if (ayc == null) {
			throw new RuntimeException("No file specified for config class " + configClass);
		}
		String filename = (ayc.filename().isBlank() ? ayc.modid() + ".json" : ayc.filename());
		Path path = PlatformUtils.resolveConfigFile(filename);

		init(configClass, ayc.modid(), path, updater);
		validate();
	}

	/**
	 * Sets up the config parsing, and takes the filename from the given argument.
	 * @param configClass the class referring to T, with fields marked as @SerialEntry.
	 * @param modId the mod ID used to generate translation entries.
	 * @param path the path to the config file.
	 */
	public ConfigStore(Class<T> configClass, String modId, Path path) {
		init(configClass, modId, path, null);
	}

	/**
	 * Sets up the config parsing, and takes the filename from the given argument.
	 * @param configClass the class referring to T, with fields marked as @SerialEntry.
	 * @param modId the mod ID used to generate translation entries.
	 * @param path the path to the config file.
	 * @param updater a class specifying a config updater to prepare outdated configs before parsing.
	 */
	public ConfigStore(Class<T> configClass, String modId, Path path, ConfigUpdater updater) {
		init(configClass, modId, path, updater);
	}

	private void createNewConfig(Class<T> configClass) {
		try {
			configInstance = configClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException f) {
			throw new RuntimeException(f);
		}
	}

	private void init(Class<T> configClass, String modId, Path path, ConfigUpdater updater) {
		this.configClass = configClass;
		this.modId = modId;
		this.path = path;
		ConfigJsonValidator<T> jsonValidator = new ConfigJsonValidator<>(configClass);
		boolean doSave = false;
		try {
			JsonObject json = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
			boolean update = false;
			if (updater != null) {
				update |= updater.updateConfigFile(json);
			}
			update |= jsonValidator.validate(json);
			if (update) {
				StringWriter stringWriter = new StringWriter();
				JsonWriter jsonWriter = new JsonWriter(stringWriter);
				jsonWriter.setIndent("  ");
				jsonWriter.setLenient(true);
				Streams.write(json, jsonWriter);
				Files.writeString(path, stringWriter.toString());
				doSave = true;
			}
		} catch (IOException e) {
			createNewConfig(configClass);
			doSave = true;
		}
		if (LibBamboo.HAS_YACL) {
			yaclWrapper = new WithYacl<>(configClass, Identifier.of(modId, configClass.getSimpleName().toLowerCase()), path);
		} else {
			try {
				configInstance = getGsonBuilder().create().fromJson(Files.readString(path), configClass);
			} catch (IOException e) {
				createNewConfig(configClass);
				doSave = true;
			}
		}
		if (doSave) {
			save();
		}
	}

	/**
	 * Access the configuration, either through YACL or static variable
	 * @return Config class object
	 */
	public T config() {
		if (LibBamboo.HAS_YACL) {
			return yaclWrapper.getConfig();
		} else {
			return configInstance;
		}
	}

	/**
	 * Saves the current configuration, either through YACL or static variable
	 */
	public void save() {
		if (LibBamboo.HAS_YACL) {
			yaclWrapper.save();
		} else {
			try {
				Files.writeString(path, getGsonBuilder().create().toJson(configInstance));
			} catch (IOException ignored) {
			}
		}
	}

	/**
	 * Validates the current configuration based on field annotations.
	 * Requires AutoYacl annotations to be present.
	 */
	public void validate() {
		if (!ConfigValidator.validate(configClass, config())) {
			save();
		}
	}

	public WithYacl<T> withYacl() {
		assert(LibBamboo.HAS_YACL);
		return yaclWrapper;
	}

	public Class<T> getConfigClass() {
		return configClass;
	}

	public Screen makeScreen(Screen parent) {
		if (LibBamboo.HAS_YACL) {
			return withYacl().makeScreen(parent);
		} else {
			AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
			String translationKey = modId + ".title";
			if (ayc != null && !ayc.translationKey().isBlank()) {
				translationKey = ayc.translationKey();
			}
			return new NoticeScreen(
					() -> MinecraftClient.getInstance().setScreen(parent),
					Text.translatable(translationKey),
					Text.translatable("libbamboo.requireYaclForConfigScreen")
			);
		}
	}
}

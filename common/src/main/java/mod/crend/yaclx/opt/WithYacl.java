package mod.crend.yaclx.opt;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.GsonConfigInstance;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import mod.crend.yaclx.type.ItemOrTag;
import mod.crend.yaclx.type.ItemOrTagTypeAdapter;
import mod.crend.yaclx.type.ItemTypeAdapter;
import mod.crend.yaclx.auto.AutoYacl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.nio.file.Path;

/**
 * Config wrapper for YACL. This class should only be referenced in a context where it is ensured that YACL has been
 * loaded, such as your ConfigScreenFactory. Use YaclX.HAS_YACL or ConfigStore to get a safe context.
 */
public class WithYacl<T> {
	public final ConfigClassHandler<T> instance;
	@SuppressWarnings("CanBeFinal")
	public ConfigChangeListener configChangeListener = () -> { };
	public final AutoYacl<T> autoYacl;
	public T dummyConfig = null;

	public WithYacl(Class<T> configClass, Identifier id, Path path) {
		instance = ConfigClassHandler.createBuilder(configClass)
				.id(id)
				.serializer(config -> GsonConfigSerializerBuilder.create(config)
						.setPath(path)
						.appendGsonBuilder(builder -> builder
									.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
									.registerTypeHierarchyAdapter(ItemOrTag.class, new ItemOrTagTypeAdapter())
						)
						.build())
				.build();

		instance.load();

		autoYacl = new AutoYacl<>(configClass, instance.defaults(), instance.instance());
	}

	public T getConfig() {
		return instance.instance();
	}

	public void save() {
		instance.save();
	}

	public void registerDummyConfig(T dummyConfig) {
		this.dummyConfig = dummyConfig;
		autoYacl.dummyConfig(dummyConfig);
	}

	public YetAnotherConfigLib setupScreen() {
		return YetAnotherConfigLib.create(instance,
				(defaults, config, builder) -> autoYacl
						.parse(builder)
						.save(() -> {
							instance.save();
							configChangeListener.onConfigChange();
						})
		);
	}

	public Screen makeScreen(Screen parent) {
		var yacl = setupScreen();
		return yacl.generateScreen(parent);
	}

	public interface ConfigChangeListener {
		@SuppressWarnings("EmptyMethod")
		void onConfigChange();
	}
}

//? if yacl {
package mod.crend.libbamboo.opt;

import com.google.gson.FieldNamingPolicy;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import mod.crend.libbamboo.event.ClientEvent;
import mod.crend.libbamboo.event.ClientEventFactory;
import mod.crend.libbamboo.type.*;
import mod.crend.libbamboo.auto.AutoYacl;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

/**
 * Config wrapper for YACL. This class should only be referenced in a context where it is ensured that YACL has been
 * loaded, such as your ConfigScreenFactory. Use LibBamboo.HAS_YACL or ConfigStore to get a safe context.
 */
public class WithYacl<T> {
	public final ConfigClassHandler<T> instance;
	public ClientEvent<ConfigChangeListener> configChangeEvent = ClientEventFactory.createArrayBacked();
	public final AutoYacl<T> autoYacl;
	public T dummyConfig = null;

	public WithYacl(Class<T> configClass, Identifier id, Path path) {
		instance = ConfigClassHandler.createBuilder(configClass)
				.id(id)
				.serializer(config -> GsonConfigSerializerBuilder.create(config)
						.setPath(path)
						.appendGsonBuilder(builder -> builder
									.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
									.registerTypeHierarchyAdapter(Identifier.class, new IdentifierTypeAdapter())
									.registerTypeHierarchyAdapter(ItemOrTag.class, new ItemOrTagTypeAdapter())
									.registerTypeHierarchyAdapter(Block.class, new BlockTypeAdapter())
									.registerTypeHierarchyAdapter(BlockOrTag.class, new BlockOrTagTypeAdapter())
						)
						.build())
				.build();

		instance.load();

		autoYacl = new AutoYacl<>(instance);
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
							configChangeEvent.invoker().onConfigChange();
						})
		);
	}

	public Screen makeScreen(Screen parent) {
		var yacl = setupScreen();
		return yacl.generateScreen(parent);
	}

	public interface ConfigChangeListener {
		void onConfigChange();
	}
}
//?}

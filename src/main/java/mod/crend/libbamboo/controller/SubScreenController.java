package mod.crend.libbamboo.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import mod.crend.libbamboo.auto.AutoYacl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
public class SubScreenController<T> implements Controller<T> {
	String modId;
	ConfigClassHandler<?> outerHandler;
	Option<T> option;
	Class<T> clazz;

	public SubScreenController(Option<T> option, Class<T> clazz, String modId, ConfigClassHandler<?> outerHandler) {
		this.option = option;
		this.clazz = clazz;
		this.modId = modId;
		this.outerHandler = outerHandler;
	}

	@Override
	public Option<T> option() {
		return option;
	}

	@Override
	public Text formatValue() {
		return Text.empty();
	}


	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> dim) {
		return new SubScreenControllerElement(this, screen, dim);
	}

	protected YetAnotherConfigLib createYacl() {
		return new AutoYacl<>(new DummyConfigClassHandler<>(option.pendingValue(), option.binding().defaultValue(), clazz), modId)
				.parse(YetAnotherConfigLib.createBuilder())
				.save(() -> outerHandler.save())
				.build();
	}

	public Screen generateScreen(Screen parent) {
		return createYacl().generateScreen(parent);
	}

	protected record DummyConfigClassHandler<T>(
			T instance,
			T defaults,
			Class<T> configClass
	) implements ConfigClassHandler<T> {
		@Override
		public ConfigField<?>[] fields() {
			return new ConfigField[0];
		}

		@Override
		public Identifier id() {
			return null;
		}

		@Override
		public YetAnotherConfigLib generateGui() {
			return null;
		}

		@Override
		public boolean supportsAutoGen() {
			return false;
		}

		@Override
		public boolean load() {
			return false;
		}

		@Override
		public void save() {
		}

		@Override
		public ConfigSerializer<T> serializer() {
			return null;
		}

	}

	public interface SubScreenControllerBuilder<T> extends ControllerBuilder<T> {
		SubScreenControllerBuilder<T> clazz(Class<T> clazz);
		SubScreenControllerBuilder<T> modId(String modId);
		SubScreenControllerBuilder<T> outerHandler(ConfigClassHandler<?> outerHandler);

		static <T> SubScreenControllerBuilderImpl<T> create(Option<T> option) {
			return new SubScreenControllerBuilderImpl<>(option);
		}
	}

	public static class SubScreenControllerBuilderImpl<T> extends AbstractControllerBuilderImpl<T> implements SubScreenControllerBuilder<T> {
		protected SubScreenControllerBuilderImpl(Option<T> option) {
			super(option);
		}

		Class<T> clazz;
		String modId;
		ConfigClassHandler<?> outerHandler;

		@Override
		public SubScreenControllerBuilder<T> clazz(Class<T> clazz) {
			this.clazz = clazz;
			return this;
		}

		@Override
		public SubScreenControllerBuilder<T> modId(String modId) {
			this.modId = modId;
			return this;
		}

		@Override
		public SubScreenControllerBuilder<T> outerHandler(ConfigClassHandler<?> outerHandler) {
			this.outerHandler = outerHandler;
			return this;
		}

		@Override
		public Controller<T> build() {
			return new SubScreenController<>(option, clazz, modId, outerHandler);
		}
	}
}

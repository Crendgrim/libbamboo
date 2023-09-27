package mod.crend.yaclx.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.cycling.CyclingControllerElement;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Function;

public class DecoratedEnumController <T extends Enum<T>> extends EnumController<T> {
	protected final Decorator<T> renderer;

	public DecoratedEnumController(Option<T> option, ValueFormatter<T> valueFormatter, T[] availableValues, Decorator<T> renderer) {
		super(option, valueFormatter::format, availableValues);
		this.renderer = renderer;
	}

	public void callRenderer(DrawContext graphics, int x, int y) {
		renderer.render(option().pendingValue(), graphics, x, y);
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new DecoratedEnumElement(this, screen, widgetDimension);
	}

	public interface DecoratedEnumControllerBuilder<T extends Enum<T>> extends EnumControllerBuilder<T> {
		DecoratedEnumControllerBuilder<T> decorator(Decorator<T> renderer);
		DecoratedEnumControllerBuilder<T> enumClass(Class<T> enumClass);

		static <T extends Enum<T>> DecoratedEnumControllerBuilder<T> create(Option<T> option) {
			return new DecoratedEnumControllerBuilderImpl<>(option);
		}
	}

	@FunctionalInterface
	public interface Decorator <T> {
		void render(T value, DrawContext context, int x, int y);
	}

	public static class DecoratedEnumControllerBuilderImpl<T extends Enum<T>> extends AbstractControllerBuilderImpl<T> implements DecoratedEnumControllerBuilder<T> {
		private Class<T> enumClass;
		private ValueFormatter<T> formatter = null;
		private Decorator<T> renderer = (value, context, x, y) -> {};

		public DecoratedEnumControllerBuilderImpl(Option<T> option) {
			super(option);
		}

		@Override
		public DecoratedEnumControllerBuilder<T> enumClass(Class<T> enumClass) {
			this.enumClass = enumClass;
			return this;
		}

		@Override
		public DecoratedEnumControllerBuilder<T> formatValue(ValueFormatter<T> valueFormatter) {
			this.formatter = valueFormatter;
			return this;
		}

		@Override
		public DecoratedEnumControllerBuilder<T> decorator(Decorator<T> renderer) {
			this.renderer = renderer;
			return this;
		}

		@Override
		public Controller<T> build() {
			ValueFormatter<T> formatter = this.formatter;
			if (formatter == null) {
				Function<T, Text> formatFunction = EnumController.getDefaultFormatter();
				Objects.requireNonNull(formatFunction);
				formatter = formatFunction::apply;
			}

			return new DecoratedEnumController<>(option, formatter, enumClass.getEnumConstants(), renderer);
		}
	}

	public static class DecoratedEnumElement extends CyclingControllerElement {
		private final DecoratedEnumController<?> decoratedEnumController;

		public DecoratedEnumElement(DecoratedEnumController<?> control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			this.decoratedEnumController = control;
		}

		protected int getDecorationPadding() {
			return 15;
		}

		@Override
		protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
			Text valueText = getValueText();
			graphics.drawText(textRenderer, valueText, getDimension().xLimit() - textRenderer.getWidth(valueText) - getXPadding() - getDecorationPadding(), getTextY(), getValueColor(), true);
			decoratedEnumController.callRenderer(graphics, getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2, getDimension().y() + 2);
		}

		@Override
		protected int getControlWidth() {
			return super.getControlWidth() + getDecorationPadding();
		}

	}
}

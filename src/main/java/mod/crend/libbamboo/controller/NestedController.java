//? if yacl {
package mod.crend.libbamboo.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class NestedController<T> implements Controller<T> {
	protected final Option<T> option;
	protected final Controller<T> nestedControl;

	public NestedController(Option<T> option, Controller<T> nestedControl) {
		this.option = option;
		this.nestedControl = nestedControl;
	}

	@Override
	public Option<T> option() {
		return option;
	}

	@Override
	public Text formatValue() {
		return nestedControl.formatValue();
	}

	public Controller<T> nestedControl() {
		return nestedControl;
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> dim) {
		return new NestedControllerElement<>(this, screen, Dimension.ofInt(0, 0, 0, 0));
	}

	public static class NestedControllerElement<T> extends ControllerWidget<NestedController<T>> {
		public NestedControllerElement(NestedController<T> control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
		}

		@Override
		public void setDimension(Dimension<Integer> dim) {
			super.setDimension(Dimension.ofInt(0, 0, 0, 0));
		}

		@Override
		public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
		}

		@Override
		protected int getHoveredControlWidth() {
			return 0;
		}

		@Override
		protected int getYPadding() {
			return 0;
		}

		@Override
		public boolean canReset() {
			return false;
		}
	}

	public interface NestedControllerBuilder<T> extends ControllerBuilder<T> {
		NestedControllerBuilder<T> nestedControl(Controller<T> nestedControl);

		static <T> NestedControllerBuilderImpl<T> create(Option<T> option) {
			return new NestedControllerBuilderImpl<>(option);
		}
	}

	public static class NestedControllerBuilderImpl<T> extends AbstractControllerBuilderImpl<T> implements NestedControllerBuilder<T> {
		protected NestedControllerBuilderImpl(Option<T> option) {
			super(option);
		}

		Controller<T> nestedControl;

		@Override
		public NestedControllerBuilder<T> nestedControl(Controller<T> nestedControl) {
			this.nestedControl = nestedControl;
			return this;
		}

		@Override
		public Controller<T> build() {
			return new NestedController<>(option, nestedControl);
		}
	}
}
//?}

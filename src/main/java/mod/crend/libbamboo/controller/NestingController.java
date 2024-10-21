package mod.crend.libbamboo.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class NestingController implements Controller<Boolean> {
	private final Option<Boolean> option;
	private Option<?> nestedOption;

	/**
	 * Constructs a tickbox controller
	 *
	 * @param option bound option
	 */
	public NestingController(Option<Boolean> option, Option<?> nestedOption) {
		this.option = option;
		this.nestedOption = nestedOption;
		if (nestedOption != null) {
			nestedOption.setAvailable(option.pendingValue());
		}
	}

	public void setNestedOption(Option<?> nestedOption) {
		this.nestedOption = nestedOption;
		nestedOption.setAvailable(option.pendingValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Option<Boolean> option() {
		return option;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Text formatValue() {
		return Text.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new ToggleableControllerElement(this, screen, widgetDimension);
	}

	public static class ToggleableControllerElement extends ControllerWidget<NestingController> {
		protected final AbstractWidget nestedElement;
		protected Dimension<Integer> tickboxDim;

		public ToggleableControllerElement(NestingController control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			NestedController<?> nestedController = (NestedController<?>) control.nestedOption.controller();
			this.nestedElement = nestedController.nestedControl().provideWidget(screen, dim);
			setDimension(dim);
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return tickboxDim.isPointInside((int) mouseX, (int) mouseY);
		}

		@Override
		public void setDimension(Dimension<Integer> dim) {
			super.setDimension(dim);
			nestedElement.setDimension(Dimension.ofInt(dim.x() + dim.height() + 2, dim.y(), dim.width() - dim.height() - 2, dim.height()));
			this.tickboxDim = dim.withWidth(dim.height());
		}

		@Override
		public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
			hovered = isMouseOver(mouseX, mouseY);

			drawButtonRect(graphics, tickboxDim.x(), tickboxDim.y(), tickboxDim.xLimit(), tickboxDim.yLimit(), hovered || focused, isAvailable());

			drawHoveredControl(graphics, mouseX, mouseY, delta);

			nestedElement.render(graphics, mouseX, mouseY, delta);
		}

		@Override
		protected void drawHoveredControl(DrawContext graphics, int mouseX, int mouseY, float delta) {
			int outlineSize = 10;
			int outlineX1 = tickboxDim.xLimit() - getXPadding() - outlineSize;
			int outlineY1 = tickboxDim.centerY() - outlineSize / 2;
			int outlineX2 = tickboxDim.xLimit() - getXPadding();
			int outlineY2 = tickboxDim.centerY() + outlineSize / 2;

			int color = getValueColor();
			int shadowColor = multiplyColor(color, 0.25f);

			drawOutline(graphics, outlineX1 + 1, outlineY1 + 1, outlineX2 + 1, outlineY2 + 1, 1, shadowColor);
			drawOutline(graphics, outlineX1, outlineY1, outlineX2, outlineY2, 1, color);
			if (control.option().pendingValue()) {
				graphics.fill(outlineX1 + 3, outlineY1 + 3, outlineX2 - 1, outlineY2 - 1, shadowColor);
				graphics.fill(outlineX1 + 2, outlineY1 + 2, outlineX2 - 2, outlineY2 - 2, color);
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (isMouseOver(mouseX, mouseY) && isAvailable()) {
				toggleSetting();
				return true;
			}
			return nestedElement.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		protected int getHoveredControlWidth() {
			return 10;
		}

		@Override
		protected int getUnhoveredControlWidth() {
			return 10;
		}

		public void toggleSetting() {
			control.option().requestSet(!control.option().pendingValue());
			control.nestedOption.setAvailable(control.option.pendingValue());
			playDownSound();
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (!focused) {
				return false;
			}

			if (keyCode == InputUtil.GLFW_KEY_ENTER || keyCode == InputUtil.GLFW_KEY_SPACE || keyCode == InputUtil.GLFW_KEY_KP_ENTER) {
				toggleSetting();
				return true;
			}

			return false;
		}
	}

	public interface NestingControllerBuilder extends ControllerBuilder<Boolean> {
		NestingControllerBuilder toggledOption(Option<?> toggledOption);

		static NestingControllerBuilderImpl create(Option<Boolean> option) {
			return new NestingControllerBuilderImpl(option);
		}
	}

	public static class NestingControllerBuilderImpl extends AbstractControllerBuilderImpl<Boolean> implements NestingControllerBuilder {
		protected NestingControllerBuilderImpl(Option<Boolean> option) {
			super(option);
		}

		Option<?> toggledOption;

		@Override
		public NestingControllerBuilder toggledOption(Option<?> toggledOption) {
			this.toggledOption = toggledOption;
			return this;
		}

		@Override
		public Controller<Boolean> build() {
			return new NestingController(option, toggledOption);
		}
	}
}


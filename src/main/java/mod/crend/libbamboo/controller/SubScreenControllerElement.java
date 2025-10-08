//? if yacl {
package mod.crend.libbamboo.controller;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

//? if >1.21.8 {
/*import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
*///?}

public class SubScreenControllerElement extends ControllerWidget<SubScreenController<?>> {
	public SubScreenControllerElement(SubScreenController<?> control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
	}

	protected void openScreen() {
		playDownSound();
		client.setScreen(control.generateScreen(screen));
	}

	@Override
	//? if <=1.21.8 {
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
	//?} else {
	/*public boolean mouseClicked(Click mouseButtonEvent, boolean doubleClick) {
		double mouseX = mouseButtonEvent.x();
		double mouseY = mouseButtonEvent.y();
		int button = mouseButtonEvent.button();
	*///?}
		if (!isMouseOver(mouseX, mouseY)
				|| (button != GLFW.GLFW_MOUSE_BUTTON_1 && button != GLFW.GLFW_MOUSE_BUTTON_2)
				|| !isAvailable()
		)
			return false;

		openScreen();

		return true;
	}

	@Override
	//? if <=1.21.8 {
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
	//?} else {
	/*public boolean keyPressed(KeyInput keyEvent) {
		int keyCode = keyEvent.getKeycode();
	*///?}
		if (!focused)
			return false;

		switch (keyCode) {
			case InputUtil.GLFW_KEY_ENTER, InputUtil.GLFW_KEY_SPACE, InputUtil.GLFW_KEY_KP_ENTER -> openScreen();
			default -> {
				return false;
			}
		}

		return true;
	}
	@Override
	protected int getHoveredControlWidth() {
		return getUnhoveredControlWidth();
	}
}
//?}

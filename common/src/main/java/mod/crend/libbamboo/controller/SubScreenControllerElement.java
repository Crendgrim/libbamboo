package mod.crend.libbamboo.controller;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SubScreenControllerElement extends ControllerWidget<SubScreenController<?>> {
	public SubScreenControllerElement(SubScreenController<?> control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
	}

	protected void openScreen() {
		playDownSound();
		client.setScreen(control.generateScreen(screen));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!isMouseOver(mouseX, mouseY)
				|| (button != GLFW.GLFW_MOUSE_BUTTON_1 && button != GLFW.GLFW_MOUSE_BUTTON_2)
				|| !isAvailable()
		)
			return false;

		openScreen();

		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
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

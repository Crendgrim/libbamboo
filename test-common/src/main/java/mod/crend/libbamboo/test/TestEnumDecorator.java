package mod.crend.libbamboo.test;

import mod.crend.libbamboo.controller.DecoratedEnumController;
import net.minecraft.client.gui.DrawContext;

public class TestEnumDecorator implements DecoratedEnumController.Decorator<TestEnum> {
	@Override
	public void render(TestEnum value, DrawContext context, int x, int y) {
		int color = switch (value) {
			case DEFAULT -> 0x808080FF;
			case VALUE -> 0x0000A0FF;
			case DIFFERENT -> 0x00A000FF;
			case MAGIC -> 0x808000FF;
		};
		context.fill(x, y, x + 12, y + 12, color);
	}
}

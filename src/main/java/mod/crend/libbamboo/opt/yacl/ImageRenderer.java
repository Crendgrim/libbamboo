package mod.crend.libbamboo.opt.yacl;

import net.minecraft.client.gui.DrawContext;

public interface ImageRenderer {
	int render(DrawContext var1, int var2, int var3, int var4, float var5);

	void close();

	default void tick() {
	}
}

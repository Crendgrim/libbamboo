package mod.crend.libbamboo.render;

import dev.isxander.yacl3.gui.image.ImageRenderer;

import java.util.List;

public interface ListImageRenderer<T> extends ImageRenderer {
	void setList(List<T> list);
}

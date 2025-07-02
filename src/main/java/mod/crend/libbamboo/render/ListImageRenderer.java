package mod.crend.libbamboo.render;

//? if yacl {
import dev.isxander.yacl3.gui.image.ImageRenderer;
//?} else
/*import mod.crend.libbamboo.opt.yacl.ImageRenderer;*/

import java.util.List;

public interface ListImageRenderer<T> extends ImageRenderer {
	void setList(List<T> list);
}

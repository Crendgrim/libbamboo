package mod.crend.libbamboo.test;

import mod.crend.libbamboo.type.NameableEnum;
import net.minecraft.text.Text;

public enum TestEnum implements NameableEnum {
	DEFAULT,
	VALUE,
	DIFFERENT,
	MAGIC;

	@Override
	public Text getDisplayName() {
		return Text.translatable("libbambootest.testEnum." + name());
	}
}

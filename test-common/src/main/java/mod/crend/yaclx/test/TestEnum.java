package mod.crend.yaclx.test;

import mod.crend.yaclx.type.NameableEnum;
import net.minecraft.text.Text;

public enum TestEnum implements NameableEnum {
	DEFAULT,
	VALUE,
	DIFFERENT,
	MAGIC;

	@Override
	public Text getDisplayName() {
		return Text.translatable("yaclxtest.testEnum." + name());
	}
}

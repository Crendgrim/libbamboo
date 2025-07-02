package mod.crend.libbamboo.type;

import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;

import java.util.function.Function;

/**
 * Only reference from context in which YACL is present!
 */
public class EnumFormatter {
	public static <T extends Enum<T>> Function<T, Text> getEnumFormatter() {
		return value -> {
			// Support both YACL and LibBamboo's NameableEnum interfaces
			if (value instanceof NameableEnum nameableEnum)
				return nameableEnum.getDisplayName();
			//? if yacl {
			if (value instanceof dev.isxander.yacl3.api.NameableEnum nameableEnum)
				return nameableEnum.getDisplayName();
			//?}
			if (value instanceof TranslatableOption translatableOption)
				return translatableOption.getText();
			return Text.literal(value.toString());
		};
	}
}

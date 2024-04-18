package mod.crend.libbamboo.type;

import net.minecraft.text.Text;

/**
 * This is functionally the same as YACL's internal NameableEnum.
 * However, if a mod's config includes an enum inheriting from YACL's NameableEnum, it becomes impossible to instantiate
 * without YACL installed. Therefore, if LibBamboo's OptionalYacl facilities are to be used, enums should inherit from this
 * interface instead to allow loading the config without YACL present while still providing translations the same way.
 *
 * <p>AutoYacl supports both variants of NameableEnum through EnumFormatter.
 */
public interface NameableEnum {
	Text getDisplayName();
}

package mod.crend.libbamboo;

import net.minecraft.util.Identifier;

public class VersionUtils {
	public static Identifier getIdentifier(String path) {
		//? if <1.21 {
		return new Identifier(path);
		 //?} else {
		/*return Identifier.of(path);
		*///?}
	}

	public static Identifier getIdentifier(String namespace, String path) {
		//? if <1.21 {
		return new Identifier(namespace, path);
		 //?} else {
		/*return Identifier.of(namespace, path);
		*///?}
	}
}

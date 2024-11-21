package mod.crend.libbamboo;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class PlatformUtils {
	public enum Platform {
		FABRIC,
		FORGE,
		NEOFORGE
	}

	@ExpectPlatform
	public static Platform getCurrentPlatform() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean isYaclLoaded() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean isModLoaded(String modId) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean isModPresent(String modId) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Path resolveConfigFile(String configName) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Class<?> getModdedItemTagsClass() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Set<Identifier> getItemsFromTag(TagKey<Item> itemTagKey) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Class<?> getModdedBlockTagsClass() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Set<Identifier> getBlocksFromTag(TagKey<Block> blockTagKey) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static HashSet<Path> getResourcePaths(String path) {
		throw new AssertionError();
	}
}

package mod.crend.libbamboo;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class PlatformUtils {
	public enum Platform {
		FABRIC,
		FORGE,
		NEOFORGE
	}

	//? if fabric {
	static PlatformUtilsFabric platform = new PlatformUtilsFabric();
	//?} else if forge {
	/*static PlatformUtilsForge platform = new PlatformUtilsForge();
	*///?} else if neoforge
	/*static PlatformUtilsNeoforge platform = new PlatformUtilsNeoforge();*/

	public static Platform getCurrentPlatform() {
		return platform.getCurrentPlatform();
	}

	public static boolean isYaclLoaded() {
		return isModLoaded(LibBamboo.YACL_MOD_ID);
	}

	public static boolean isModLoaded(String modId) {
		return platform.isModLoaded(modId);
	}

	public static boolean isModPresent(String modId) {
		return platform.isModPresent(modId);
	}

	public static Path resolveConfigFile(String configName) {
		return platform.resolveConfigFile(configName);
	}

	public static Class<?> getModdedItemTagsClass() {
		return platform.getModdedItemTagsClass();
	}

	public static Set<Identifier> getItemsFromTag(TagKey<Item> itemTagKey) {
		return platform.getItemsFromTag(itemTagKey);
	}

	public static Class<?> getModdedBlockTagsClass() {
		return platform.getModdedBlockTagsClass();
	}

	public static Set<Identifier> getBlocksFromTag(TagKey<Block> blockTagKey) {
		return platform.getBlocksFromTag(blockTagKey);
	}

	public static HashSet<Path> getResourcePaths(String path) {
		return platform.getResourcePaths(path);
	}
}

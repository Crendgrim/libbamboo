package mod.crend.libbamboo.fabric;

import mod.crend.libbamboo.LibBamboo;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
//? if <1.20.6 {
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
//?} else {
/*import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
*///?}
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isYaclLoaded() {
		return FabricLoader.getInstance().isModLoaded(LibBamboo.YACL_MOD_ID);
	}

	public static Path resolveConfigFile(String configName) {
		return FabricLoader.getInstance().getConfigDir().resolve(configName);
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean isModPresent(String modId) {
		return isModLoaded(modId);
	}

	public static Class<?> getModdedItemTagsClass() {
		return ConventionalItemTags.class;
	}

	public static Set<Identifier> getItemsFromTag(TagKey<Item> itemTagKey) {
		return ClientTags.getOrCreateLocalTag(itemTagKey);
	}

	public static Class<?> getModdedBlockTagsClass() {
		return ConventionalBlockTags.class;
	}

	public static Set<Identifier> getBlocksFromTag(TagKey<Block> blockTagKey) {
		return ClientTags.getOrCreateLocalTag(blockTagKey);
	}

	public static HashSet<Path> getResourcePaths(String path) {
		HashSet<Path> out = new HashSet<>();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			mod.findPath(path).ifPresent(out::add);
		}

		return out;
	}
}

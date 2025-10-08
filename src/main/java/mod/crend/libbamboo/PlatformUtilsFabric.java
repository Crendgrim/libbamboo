//? if fabric {
package mod.crend.libbamboo;

import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/*? if <1.20.5 {*/
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
//?} else {
/*import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
*///?}

public class PlatformUtilsFabric {
	public PlatformUtils.Platform getCurrentPlatform() {
		return PlatformUtils.Platform.FABRIC;
	}

	public Path resolveConfigFile(String configName) {
		return FabricLoader.getInstance().getConfigDir().resolve(configName);
	}

	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public boolean isModPresent(String modId) {
		return isModLoaded(modId);
	}

	public Class<?> getModdedItemTagsClass() {
		return ConventionalItemTags.class;
	}

	public Set<Identifier> getItemsFromTag(TagKey<Item> itemTagKey) {
		return ClientTags.getOrCreateLocalTag(itemTagKey);
	}

	public Class<?> getModdedBlockTagsClass() {
		return ConventionalBlockTags.class;
	}

	public Set<Identifier> getBlocksFromTag(TagKey<Block> blockTagKey) {
		return ClientTags.getOrCreateLocalTag(blockTagKey);
	}

	public HashSet<Path> getResourcePaths(String path) {
		HashSet<Path> out = new HashSet<>();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			mod.findPath(path).ifPresent(out::add);
		}

		return out;
	}
}
//?}

package mod.crend.yaclx.neoforge;

import mod.crend.yaclx.YaclX;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.Tags;

import java.nio.file.Path;
import java.util.Set;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isYaclLoaded() {
		return ModList.get().isLoaded(YaclX.YACL_MOD_ID);
	}

	public static Path resolveConfigFile(String configName) {
		return FMLPaths.CONFIGDIR.get().resolve(configName);
	}

	public static Class<?> getModdedItemTagsClass() {
		return Tags.Items.class;
	}

	public static Set<Identifier> getItemsFromTag(TagKey<Item> itemTagKey) {
		if (ModList.get().isLoaded("fabric_api")) {
			return ClientTags.getOrCreateLocalTag(itemTagKey);
		}
		return Set.of();
	}

	public static Class<?> getModdedBlockTagsClass() {
		return Tags.Blocks.class;
	}

	public static Set<Identifier> getBlocksFromTag(TagKey<Block> blockTagKey) {
		if (ModList.get().isLoaded("fabric_api")) {
			return ClientTags.getOrCreateLocalTag(blockTagKey);
		}
		return Set.of();
	}
}

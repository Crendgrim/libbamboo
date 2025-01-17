package mod.crend.libbamboo.forge;

import mod.crend.libbamboo.LibBamboo;
import mod.crend.libbamboo.PlatformUtils;
//? if forgified_fabric_api_forge
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.forgespi.language.IModFileInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static PlatformUtils.Platform getCurrentPlatform() {
		return PlatformUtils.Platform.FORGE;
	}

	public static boolean isYaclLoaded() {
		return ModList.get().isLoaded(LibBamboo.YACL_MOD_ID);
	}

	public static boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	public static boolean isModPresent(String modId) {
		return LoadingModList.get().getModFileById(modId) != null;
	}

	public static Path resolveConfigFile(String configName) {
		return FMLPaths.CONFIGDIR.get().resolve(configName);
	}

	public static Class<?> getModdedItemTagsClass() {
		return Tags.Items.class;
	}

	public static Set<Identifier> getItemsFromTag(TagKey<Item> itemTagKey) {
		if (ModList.get().isLoaded("fabric_api")) {
			//? if forgified_fabric_api_forge
			return ClientTags.getOrCreateLocalTag(itemTagKey);
		}
		return Set.of();
	}

	public static Class<?> getModdedBlockTagsClass() {
		return Tags.Blocks.class;
	}

	public static Set<Identifier> getBlocksFromTag(TagKey<Block> blockTagKey) {
		if (ModList.get().isLoaded("fabric_api")) {
			//? if forgified_fabric_api_forge
			return ClientTags.getOrCreateLocalTag(blockTagKey);
		}
		return Set.of();
	}

	public static HashSet<Path> getResourcePaths(String path) {
		HashSet<Path> out = new HashSet<>();

		for (IModFileInfo mod : ModList.get().getModFiles()) {
			Path resource = mod.getFile().findResource(path);
			if (Files.exists(resource)) {
				out.add(resource);
			}
		}

		return out;
	}
}

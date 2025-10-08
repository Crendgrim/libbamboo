//? if neoforge {
/*package mod.crend.libbamboo;

//? if forgified_fabric_api
/^import net.fabricmc.fabric.api.tag.client.v1.ClientTags;^/
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public class PlatformUtilsNeoforge {
	public PlatformUtils.Platform getCurrentPlatform() {
		return PlatformUtils.Platform.NEOFORGE;
	}

	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	public boolean isModPresent(String modId) {
		//? if <1.21.9 {
		return LoadingModList.get().getModFileById(modId) != null;
		//?} else
		/^return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;^/
	}

	public Path resolveConfigFile(String configName) {
		return FMLPaths.CONFIGDIR.get().resolve(configName);
	}

	public Class<?> getModdedItemTagsClass() {
		return Tags.Items.class;
	}

	public Set<Identifier> getItemsFromTag(TagKey<Item> itemTagKey) {
		if (ModList.get().isLoaded("fabric_api")) {
			//? if forgified_fabric_api
			/^return ClientTags.getOrCreateLocalTag(itemTagKey);^/
		}
		return Set.of();
	}

	public Class<?> getModdedBlockTagsClass() {
		return Tags.Blocks.class;
	}

	public Set<Identifier> getBlocksFromTag(TagKey<Block> blockTagKey) {
		if (ModList.get().isLoaded("fabric_api")) {
			//? if forgified_fabric_api
			/^return ClientTags.getOrCreateLocalTag(blockTagKey);^/
		}
		return Set.of();
	}

	public HashSet<Path> getResourcePaths(String path) {
		HashSet<Path> out = new HashSet<>();

		for (IModFileInfo mod : ModList.get().getModFiles()) {
			//? if <1.21.9 {
			Path resource = mod.getFile().findResource(path);
			//?} else {
			/^Optional<URI> uri = mod.getFile().getContents().findFile(path);
			if (uri.isEmpty()) continue;
			Path resource = Paths.get(uri.get().getPath());
			^///?}
			if (Files.exists(resource)) {
				out.add(resource);
			}
		}

		return out;
	}
}
*///?}

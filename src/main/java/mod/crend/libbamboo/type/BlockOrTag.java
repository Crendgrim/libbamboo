package mod.crend.libbamboo.type;

import mod.crend.libbamboo.BlockRegistryHelper;
import mod.crend.libbamboo.PlatformUtils;
import mod.crend.libbamboo.VersionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Wrapper class that represents either a Block or a BlockTag.
 * In serialized form, block tags gain a # symbol in front of their identifier
 * to differentiate between the two.
 */
public class BlockOrTag {
	private final static List<TagKey<Block>> BUILTIN_BLOCK_TAGS = new ArrayList<>();

	@SuppressWarnings("unchecked")
	private static void loadBlockTags(Class<?> blockTagsClass) {
		// Manually read block tags based on their fields. This is a hack to allow listing potential block tags without
		// a world being loaded.
		for (Field field : blockTagsClass.getDeclaredFields()) {
			try {
				if (!field.isAnnotationPresent(Deprecated.class)) {
					BUILTIN_BLOCK_TAGS.add((TagKey<Block>) field.get(null));
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static {
		loadBlockTags(BlockTags.class);
		loadBlockTags(PlatformUtils.getModdedBlockTagsClass());
	}

	/**
	 * Returns a list of all known block tags.
	 *
	 * <p>When we open a config screen from the main menu, no world is loaded. Block tags are world-based, so we cannot
	 * give any hints here. To circumvent this, we read out the block tags manually to show suggestions that will be
	 * correct under most circumstances. These will show both vanilla and modloader specific tags, though. When opening
	 * from within a world, the list will instead be populated by the server-provided block tags.
	 *
	 * <p>Do note also that, without a world, these block tags are not actually loaded, and we only cache their names here.
	 * Other mods' block tags will also appear here only if a world is loaded.
	 *
	 * @return A list of all known block tags
	 */
	public static List<TagKey<Block>> getBlockTags() {
		List<TagKey<Block>> currentlyLoadedTags = Registries.BLOCK.streamTags().toList();
		if (currentlyLoadedTags.isEmpty()) {
			// No elements in the stream; use the default tags as read by reflection
			return BUILTIN_BLOCK_TAGS;
		}
		return currentlyLoadedTags;
	}

	/**
	 * Static helper to query whether the given String names an identifier to a known block tag.
	 * If the given String contains no namespace, the default namespace ("minecraft") is checked.
	 *
	 * <p>This method follows the same rules as {@link BlockOrTag#getBlockTags()}.
	 *
	 * @param value a String; should be of the format "namespace:path" or "path".
	 * @return true if the given String names a known block tag, false otherwise.
	 */
	public static boolean isBlockTag(String value) {
		String namespace, blockTag;
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		value = value.toLowerCase();
		if (sep == -1) {
			namespace = Identifier.DEFAULT_NAMESPACE;
			blockTag = value;
		} else {
			namespace = value.substring(0, sep);
			blockTag = value.substring(sep + 1);
		}
		try {
			TagKey<Block> tagKey = TagKey.of(RegistryKeys.BLOCK, VersionUtils.getIdentifier(namespace, blockTag));
			return getBlockTags().contains(tagKey);
		} catch (InvalidIdentifierException e) {
			return false;
		}
	}

	/**
	 * Helper to query a collection of BlockOrTag entries whether any of them match the given block.
	 * Short-circuits on the first match.
	 *
	 * @see BlockOrTag#matches(Block)
	 * @param block The block to search.
	 * @param collection A collection of BlockOrTag elements.
	 * @return true if any of them match, false otherwise.
	 */
	public static boolean isContainedIn(Block block, Collection<BlockOrTag> collection) {
		return collection.stream().anyMatch(blockOrTag -> blockOrTag.matches(block));
	}

	/**
	 * Factory helper to build an BlockOrTag from a String identifier.
	 *
	 * <p>The identifier should be of one of the following formats:<br>
	 * - #namespace:path<br>
	 * - #path<br>
	 * - namespace:path<br>
	 * - path<br>
	 *
	 * <p>If namespace is not given, the default namespace ("minecraft") is used.
	 *
	 * <p>If the identifier starts with a #, construct a block tag variant. Otherwise, constructs a block variant.
	 *
	 * @param identifier The String to identify with.
	 * @param ensureBlockTagExists If true, only allow construction of a block tag variant if the given variant exists.
	 * @return An BlockOrTag object if the construction was successful (identifier exists, or block tag variant given
	 *         ensureBlockTagExists is false); None otherwise.
	 */
	public static Optional<BlockOrTag> fromString(String identifier, boolean ensureBlockTagExists) {
		if (identifier.startsWith("#")) {
			if (identifier.contains(":")) {
				String[] segments = identifier.split(":");
				if (!Identifier.isNamespaceValid(segments[0]) || !Identifier.isPathValid(segments[1])) {
					return Optional.empty();
				}
			} else {
				if (!Identifier.isPathValid(identifier)) {
					return Optional.empty();
				}
			}
			if (ensureBlockTagExists && !isBlockTag(identifier.substring(1))) {
				return Optional.empty();
			}
		} else if (!BlockRegistryHelper.isRegisteredBlock(identifier)) {
			return Optional.empty();
		}
		return Optional.of(new BlockOrTag(identifier));
	}

	private Block block = null;
	private TagKey<Block> blockTag = null;
	private final boolean isBlock;

	public BlockOrTag(Block block) {
		this.block = block;
		this.isBlock = true;
	}
	public BlockOrTag(TagKey<Block> tag) {
		this.blockTag = tag;
		this.isBlock = false;
	}
	protected BlockOrTag(String identifier) {
		if (identifier.startsWith("#")) {
			String tagName = identifier.substring(1);
			this.blockTag = TagKey.of(RegistryKeys.BLOCK, VersionUtils.getIdentifier(tagName));
			this.isBlock = false;
		} else {
			this.block = BlockRegistryHelper.getBlockFromName(identifier);
			this.isBlock = true;
		}
	}

	public boolean isBlock() {
		return isBlock;
	}
	public boolean isBlockTag() {
		return !isBlock;
	}

	public Block getBlock() {
		assert(isBlock);
		return block;
	}

	public TagKey<Block> getBlockTag() {
		assert(!isBlock);
		return blockTag;
	}

	/**
	 * Gets a relevant block from this BlockOrTag object.
	 *
	 * <p>If this is a block variant, returns the block itself.
	 *
	 * <p>Else, returns the first block registered under the tag if it is loaded, or Air otherwise.
	 *
	 * @return a valid block, or `Blocks.AIR`
	 */
	public Block getAnyBlock() {
		if (isBlock) return block;
		var tagEntries = Registries.BLOCK.getEntryList(blockTag);
		if (tagEntries.isPresent() && tagEntries.get().size() > 0) {
			return tagEntries.get().get(0).value();
		}
		return PlatformUtils.getBlocksFromTag(blockTag)
				.stream()
				.findFirst()
				.map(Registries.BLOCK::get)
				.orElse(Blocks.AIR);
	}

	public Collection<Block> getAllBlocks() {
		if (isBlock) return List.of(block);
		return Registries.BLOCK.getEntryList(blockTag)
				// Extract list of identifiers from loaded tag registry, if present
				.map(registryEntries -> registryEntries.stream().map(RegistryEntry::value).toList())
				// Or, if empty, manually force-load from declaration classes
				.orElseGet(() -> PlatformUtils.getBlocksFromTag(blockTag)
				.stream()
				.map(Registries.BLOCK::get)
				.toList());
	}

	/**
	 * Checks whether this BlockOrTag matches the given block.
	 *
	 * <p>If this is a block variant, checks for equality.
	 *
	 * <p>Otherwise, checks whether this block is part of this block tag.
	 * Note: This only works if tags are loaded.
	 *
	 * @param other The block to check
	 * @return true if it matches according to above rules, false otherwise
	 */
	public boolean matches(Block other) {
		if (isBlock) {
			return other.equals(block);
		} else {
			for (var it : Registries.BLOCK.iterateEntries(blockTag)) {
				if (other.equals(it.value())) return true;
			}
			return false;
		}
	}

	public String toString() {
		if (isBlock) {
			return id().toString();
		} else {
			return "#" + id().toString();
		}
	}

	public Text getName() {
		if (isBlock) {
			return block.getName();
		} else {
			return Text.literal("#" + blockTag.id().toString());
		}
	}

	public Identifier id() {
		if (isBlock) {
			return Registries.BLOCK.getId(block);
		} else {
			return blockTag.id();
		}
	}

}

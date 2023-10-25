package mod.crend.yaclx;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class BlockRegistryHelper {
	private static Block getBlockFromNameImpl(String value) {
		String namespace, blockName;
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		value = value.toLowerCase();
		if (sep == -1) {
			namespace = Identifier.DEFAULT_NAMESPACE;
			blockName = value;
		} else {
			namespace = value.substring(0, sep);
			blockName = value.substring(sep + 1);
		}
		Identifier identifier = new Identifier(namespace, blockName);
		if (Registries.BLOCK.containsId(identifier)) {
			return Registries.BLOCK.get(identifier);
		} else {
			return null;
		}
	}

	/**
	 * Checks whether the given string is an identifier referring to a known block
	 * @param identifier Block identifier, either of the format "namespace:path" or "path". If no namespace is included,
	 *                   the default vanilla namespace "minecraft" is used.
	 * @return true if the identifier refers to a registered block, false otherwise
	 */
	public static boolean isRegisteredBlock(String identifier) {
		return getBlockFromNameImpl(identifier) != null;
	}

	public static Block getBlockFromName(String identifier, Block defaultBlock) {
		try {
			Identifier blockIdentifier = new Identifier(identifier.toLowerCase());
			if (Registries.BLOCK.containsId(blockIdentifier)) {
				return Registries.BLOCK.get(blockIdentifier);
			}
		} catch (InvalidIdentifierException ignored) { }
		return defaultBlock;
	}
	/**
	 * Looks up the block of the given identifier string.
	 * @param identifier Block identifier, either of the format "namespace:path" or "path". If no namespace is included,
	 *                   the default vanilla namespace "minecraft" is used.
	 * @return The block identified by the given string, or `Blocks.AIR` if the identifier is not known.
	 */
	public static Block getBlockFromName(String identifier) {
		return getBlockFromName(identifier, Blocks.AIR);
	}


	/**
	 * Returns a list of block identifiers matching the given string. The value matches an identifier if:
	 *  <li>No namespace is provided in the value and the value is a substring of the path segment of any identifier,
	 *      regardless of namespace.</li>
	 *  <li>A namespace is provided, equals the identifier's namespace, and the value is the begin of the identifier's
	 *      path segment.</li>
	 * @param value (partial) identifier, either of the format "namespace:path" or "path".
	 * @return list of matching block identifiers; empty if the given string does not correspond to any known identifiers
	 */
	public static Stream<Identifier> getMatchingBlockIdentifiers(String value) {
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		Predicate<Identifier> filterPredicate;
		if (sep == -1) {
			filterPredicate = identifier ->
					identifier.getPath().contains(value)
							|| Registries.BLOCK.get(identifier).getName().getString().toLowerCase().contains(value.toLowerCase());
		} else {
			String namespace = value.substring(0, sep);
			String path = value.substring(sep + 1);
			filterPredicate = identifier -> identifier.getNamespace().equals(namespace) && identifier.getPath().startsWith(path);
		}
		return Registries.BLOCK.getIds().stream()
				.filter(filterPredicate)
				.sorted((id1, id2) -> {
					String path = (sep == -1 ? value : value.substring(sep + 1));
					boolean id1StartsWith = id1.getPath().startsWith(path);
					boolean id2StartsWith = id2.getPath().startsWith(path);
					if (id1StartsWith) {
						if (id2StartsWith) {
							return id1.compareTo(id2);
						}
						return -1;
					}
					if (id2StartsWith) {
						return 1;
					}
					return id1.compareTo(id2);
				});
	}
}

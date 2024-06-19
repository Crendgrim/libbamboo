package mod.crend.libbamboo;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ItemRegistryHelper {

	/**
	 * Checks whether the given string is an identifier referring to a known item
	 * @param identifier Item identifier, either of the format "namespace:path" or "path". If no namespace is included,
	 *                   the default vanilla namespace "minecraft" is used.
	 * @return true if the identifier refers to a registered item, false otherwise
	 */
	public static boolean isRegisteredItem(String identifier) {
		Identifier itemIdentifier = Identifier.tryParse(identifier.toLowerCase());
		if (itemIdentifier != null) {
			return Registries.ITEM.containsId(itemIdentifier);
		}
		return false;
	}

	/**
	 * Looks up the item of the given identifier string.
	 * @param identifier Item identifier, either of the format "namespace:path" or "path". If no namespace is included,
	 *                   the default vanilla namespace "minecraft" is used.
	 * @param defaultItem Fallback item that gets returned if the identifier does not name a registered item.
	 * @return The item identified by the given string, or the fallback if the identifier is not known.
	 */
	public static Item getItemFromName(String identifier, Item defaultItem) {
		Identifier itemIdentifier = Identifier.tryParse(identifier.toLowerCase());
		if (itemIdentifier != null && Registries.ITEM.containsId(itemIdentifier)) {
			return Registries.ITEM.get(itemIdentifier);
		}
		return defaultItem;
	}

	/**
	 * Looks up the item of the given identifier string.
	 * @param identifier Item identifier, either of the format "namespace:path" or "path". If no namespace is included,
	 *                   the default vanilla namespace "minecraft" is used.
	 * @return The item identified by the given string, or `Items.AIR` if the identifier is not known.
	 */
	public static Item getItemFromName(String identifier) {
		return getItemFromName(identifier, Items.AIR);
	}

	/**
	 * Returns a list of item identifiers matching the given string. The value matches an identifier if:
	 *  <li>No namespace is provided in the value and the value is a substring of the path segment of any identifier,
	 *      regardless of namespace.</li>
	 *  <li>A namespace is provided, equals the identifier's namespace, and the value is the begin of the identifier's
	 *      path segment.</li>
	 * @param value (partial) identifier, either of the format "namespace:path" or "path".
	 * @return list of matching item identifiers; empty if the given string does not correspond to any known identifiers
	 */
	public static Stream<Identifier> getMatchingItemIdentifiers(String value) {
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		Predicate<Identifier> filterPredicate;
		if (sep == -1) {
			filterPredicate = identifier ->
					identifier.getPath().contains(value)
							|| Registries.ITEM.get(identifier).getName().getString().toLowerCase().contains(value.toLowerCase());
		} else {
			String namespace = value.substring(0, sep);
			String path = value.substring(sep + 1);
			filterPredicate = identifier -> identifier.getNamespace().equals(namespace) && identifier.getPath().startsWith(path);
		}
		return Registries.ITEM.getIds().stream()
				.filter(filterPredicate)
				/*
				 Sort items as follows based on the given "value" string's path:
				 - if both items' paths begin with the entered string, sort the identifiers (including namespace)
				 - otherwise, if either of the items' path begins with the entered string, sort it to the left
				 - else neither path matches: sort by identifiers again

				 This allows the user to enter "diamond_ore" and match "minecraft:diamond_ore" before
				 "minecraft:deepslate_diamond_ore", even though the second is lexicographically smaller
				 */
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

package mod.crend.libbamboo.type;

import com.google.gson.*;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.lang.reflect.Type;

public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
	public ItemTypeAdapter() {
	}

	public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		return ItemRegistryHelper.getItemFromName(jsonElement.getAsString());
	}

	public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
		return new JsonPrimitive(Registries.ITEM.getId(item).toString());
	}
}

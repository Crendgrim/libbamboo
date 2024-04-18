package mod.crend.libbamboo.type;

import com.google.gson.*;
import mod.crend.libbamboo.BlockRegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

import java.lang.reflect.Type;

public class BlockTypeAdapter implements JsonSerializer<Block>, JsonDeserializer<Block> {
	public BlockTypeAdapter() {
	}

	public Block deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		return BlockRegistryHelper.getBlockFromName(jsonElement.getAsString());
	}

	public JsonElement serialize(Block block, Type type, JsonSerializationContext jsonSerializationContext) {
		return new JsonPrimitive(Registries.BLOCK.getId(block).toString());
	}
}

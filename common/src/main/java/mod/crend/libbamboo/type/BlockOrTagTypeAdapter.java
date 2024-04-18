package mod.crend.libbamboo.type;

import com.google.gson.*;
import net.minecraft.block.Blocks;

import java.lang.reflect.Type;

public class BlockOrTagTypeAdapter implements JsonSerializer<BlockOrTag>, JsonDeserializer<BlockOrTag> {
	public BlockOrTagTypeAdapter() {
	}

	public BlockOrTag deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		return BlockOrTag.fromString(jsonElement.getAsString(), false).orElseGet(() -> new BlockOrTag(Blocks.AIR));
	}

	public JsonElement serialize(BlockOrTag blockOrTag, Type type, JsonSerializationContext jsonSerializationContext) {
		return new JsonPrimitive(blockOrTag.toString());
	}
}

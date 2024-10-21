package mod.crend.libbamboo.type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;

public class IdentifierTypeAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {

	@Override
	public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return Identifier.tryParse(json.getAsString());
	}

	@Override
	public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}

package mod.crend.yaclx.opt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mod.crend.yaclx.type.ItemOrTag;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ConfigJsonValidator <T> {
	Class<T> configClass;
	T defaultConfig;

	public ConfigJsonValidator(Class<T> configClass) {
		this.configClass = configClass;
		try {
			this.defaultConfig = configClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private <U> JsonPrimitive createPrimitive(Class<U> clazz, Object object) {
		if (clazz == boolean.class) return new JsonPrimitive((Boolean) object);
		if (clazz == int.class
				|| clazz == long.class
				|| clazz == float.class
				|| clazz == double.class) {
			return new JsonPrimitive((Number) object);
		}
		if (clazz.isEnum()) return new JsonPrimitive(object.toString());
		if (clazz == Color.class)
			return new JsonPrimitive(((Color) object).getRGB());
		if (clazz == Item.class)
			return new JsonPrimitive(Registries.ITEM.getId((Item) object).toString());
		if (clazz == ItemOrTag.class)
			return new JsonPrimitive(object.toString());
		return new JsonPrimitive((String) object);
	}

	private <U> void insertDefault(Class<U> clazz, Object configObject, Field field, JsonObject container) {
		try {
				container.add(field.getName(), createPrimitive(clazz, field.get(configObject)));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private <U> boolean checkField(Class<U> fieldType, Field field, Object defaultContainer, JsonObject container, JsonElement element) {
		if (element.isJsonNull()) {
			insertDefault(fieldType, defaultContainer, field, container);
			return true;
		}
		if (fieldType == String.class) {
			if (element instanceof JsonPrimitive) {
				return false;
			}
			insertDefault(String.class, defaultContainer, field, container);
			return true;
		}
		if (fieldType == boolean.class) {
			if (element instanceof JsonPrimitive primitive && primitive.isBoolean()) {
				return false;
			}
			insertDefault(boolean.class, defaultContainer, field, container);
			return true;
		}
		if (fieldType == int.class || fieldType == long.class || fieldType == float.class || fieldType == double.class) {
			if (element instanceof JsonPrimitive primitive && primitive.isNumber()) {
				return false;
			}
			insertDefault(fieldType, defaultContainer, field, container);
			return true;
		}
		if (fieldType == List.class) {
			if (element.isJsonArray()) {
				// TODO inner type
				return false;
			}
			// TODO add primitive as list element if fitting datatype
			container.remove(field.getName());
			return true;
		}
		if (fieldType == Color.class) {
			if (!(element instanceof JsonPrimitive primitive) || !primitive.isNumber()) {
				insertDefault(fieldType, defaultContainer, field, container);
				return true;
			}
			return false;
		}
		if (fieldType == Item.class || fieldType == ItemOrTag.class || fieldType.isEnum()) {
			if (!(element instanceof JsonPrimitive primitive) || !primitive.isString()) {
				insertDefault(fieldType, defaultContainer, field, container);
				return true;
			}
			if (fieldType.isEnum()) {
				String value = primitive.getAsString();
				if (Arrays.stream(fieldType.getEnumConstants()).noneMatch(t -> t.toString().equals(value))) {
					insertDefault(fieldType, defaultContainer, field, container);
					return true;
				}
			}
			return false;
		}
		if (element instanceof JsonObject jsonObject) {
			if (!element.isJsonObject()) {
				container.remove(field.getName());
			}
			boolean result = false;
			for (Field member : fieldType.getFields()) {
				if (jsonObject.has(member.getName())) {
					try {
						result |= checkField(member.getType(), member, field.get(defaultContainer), jsonObject, jsonObject.get(member.getName()));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
			return result;
		}
		return false;
	}

	public boolean validate(JsonObject json) {
		boolean modified = false;

		for (Field field : configClass.getFields()) {
			if (json.has(field.getName())) {
				modified |= checkField(field.getType(), field, defaultConfig, json, json.get(field.getName()));
			}
		}

		return modified;
	}
}

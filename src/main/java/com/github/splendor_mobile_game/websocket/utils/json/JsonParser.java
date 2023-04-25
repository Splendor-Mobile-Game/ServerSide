package com.github.splendor_mobile_game.websocket.utils.json;

import java.lang.reflect.Field;

import com.github.splendor_mobile_game.websocket.utils.json.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ToNumberPolicy;

/**
 * A utility class for parsing JSON strings into Java objects.
 */
public class JsonParser {

    /**
     * Parses a JSON string into a Java object of the specified class.
     *
     * @param jsonString the JSON string to parse
     * @param clazz      the class of the Java object to create
     * @param <T>        the type of the Java object to create
     * 
     * @return the Java object created from the JSON string
     * 
     * @throws JsonParserException      if there is an error parsing the JSON string
     * @throws JsonIsNotValidJsonObject if the JSON string is not a valid JSON object
     * @throws JsonIsNullException     if the JSON string is null or empty
     * @throws JsonMissingFieldException if a required field is missing from the JSON object
     */
    public static <T> T parseJson(String jsonString, Class<T> clazz) throws JsonParserException {
        Gson gson = (new GsonBuilder()).setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

        // Parse the JSON string into a JsonObject
        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(jsonString, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new JsonIsNotValidJsonObject("Received string is not valid json object <= " + e.getMessage(), e);
        }

        checkJsonObject(jsonObject, clazz);

        // Parse the JsonObject into an object of the specified class
        T object = gson.fromJson(jsonObject, clazz);
        //  gson.fromJson(jsonObject, clazz);
        return object;
    }

    private static void checkJsonObject(JsonObject jsonObject, Class<?> clazz) throws JsonParserException {
        // Check if the JsonObject is null or empty
        if (jsonObject == null)
            throw new JsonIsNullException("Provided json string is null or is empty!");

        // Check if all required fields are present in the JsonObject
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder();

        for (Field field : fields) {
            if (field.getName() == "this$0")
                continue;

            if (field.isAnnotationPresent(Optional.class))
                continue;

            if (!jsonObject.has(field.getName())) {
                stringBuilder.append("Missing required field: " + field.getName() + "\n");
            }

            if (field.getType().isArray()) {
                if (!jsonObject.get(field.getName()).isJsonArray())
                    throw new JsonParserException("Field `" + field.getName() + "` should be the array");
                checkJsonArray(jsonObject.get(field.getName()).getAsJsonArray(), field.getType().arrayType());
            }

            if (field.getType().isPrimitive()) {
                if (!jsonObject.get(field.getName()).isJsonPrimitive())
                    throw new JsonParserException("Field `" + field.getName() + "` should be the primitive type of `" + field.getType().getSimpleName() + "`");
                checkJsonPrimitive(jsonObject.get(field.getName()).getAsJsonPrimitive(), field.getType());
            }

            // TODO Semi-Primitive types (ie. UUID)
            // TODO Enums
        }

        if (stringBuilder.length() != 0)
            throw new JsonMissingFieldException(stringBuilder.toString().strip());
    }

    private static void checkJsonArray(JsonArray jsonArray, Class<?> clazz) throws JsonParserException {
        for (JsonElement e : jsonArray) {
            if (e.isJsonNull()) {
                throw new JsonIsNullException("Provided json string is null or is empty!");
            }  
            else if (e.isJsonObject()) {
                checkJsonObject(e.getAsJsonObject(), clazz);
            }
            else if (e.isJsonPrimitive()) {
                checkJsonPrimitive(e.getAsJsonPrimitive(), clazz);
            }
        }
    }

    private static void checkJsonPrimitive(JsonPrimitive jsonPrimitive, Class<?> clazz) throws JsonParserException {
        try {
            (new Gson()).fromJson(jsonPrimitive, clazz);
        } catch (Exception e) {
            throw new JsonParserException("Cannot convert value `" + jsonPrimitive.getAsString() + "` to " + clazz.getSimpleName() + ". Cause: " + e.getMessage());
        }
    }

}

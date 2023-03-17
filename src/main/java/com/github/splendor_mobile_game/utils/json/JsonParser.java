package com.github.splendor_mobile_game.utils.json;

import com.github.splendor_mobile_game.utils.json.exceptions.JsonIsNotValidJsonObject;
import com.github.splendor_mobile_game.utils.json.exceptions.JsonIsNullException;
import com.github.splendor_mobile_game.utils.json.exceptions.JsonMissingFieldException;
import com.github.splendor_mobile_game.utils.json.exceptions.JsonParserException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;

public class JsonParser {

    public static <T> T parseJson(String jsonString, Class<T> clazz) throws JsonParserException {
        Gson gson = new Gson();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(jsonString, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new JsonIsNotValidJsonObject("Received string is not valid json object!", e);
        }

        if (jsonObject == null) {
            throw new JsonIsNullException("Provided json string is null or is empty!");
        }

        Field[] fields = clazz.getDeclaredFields(); // TODO: SecurityException
        StringBuilder stringBuilder = new StringBuilder();

        for (Field field : fields) {
            if (field.getName() == "this$0") // TODO: SecurityException
                continue;

            if (field.isAnnotationPresent(Optional.class)) // TODO: java.lang.reflect.AccessibleObject.isAnnotationPresent
                continue;

            if (!jsonObject.has(field.getName())) // TODO: SecurityException
                stringBuilder.append("Missing required field: " + field.getName() + "\n"); // TODO: SecurityException
        }

        if (!stringBuilder.isEmpty())
            throw new JsonMissingFieldException(stringBuilder.toString().strip());

        T object = gson.fromJson(jsonString, clazz); // TODO: Possible null
        return object;
    }

}

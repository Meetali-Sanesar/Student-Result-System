package com.srms.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * JSON utility class wrapping Gson with custom configuration.
 */
public class JsonUtil {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(Timestamp.class,
                        (JsonSerializer<Timestamp>) (src, typeOfSrc, context) ->
                                new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(src)))
                .registerTypeAdapter(Timestamp.class,
                        (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
                            try {
                                return Timestamp.valueOf(json.getAsString());
                            } catch (Exception e) {
                                return null;
                            }
                        })
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    private JsonUtil() {}

    /**
     * Convert object to JSON string.
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * Parse JSON string to object of given class.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * Parse JSON string to object of given type (for generic types like List).
     */
    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    /**
     * Get the configured Gson instance.
     */
    public static Gson getGson() {
        return GSON;
    }
}

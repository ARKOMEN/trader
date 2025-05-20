package org.ttrader.util;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.List;

public final class JsonUtil {
    private JsonUtil() {}
    public static JsonArrayBuilder ofStrings(String[] strings) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (String string : strings) {
            jsonArrayBuilder.add(string);
        }
        return jsonArrayBuilder;
    }

    public static JsonArrayBuilder ofStrings(List<String> strings) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (String string : strings) {
            jsonArrayBuilder.add(string);
        }
        return jsonArrayBuilder;
    }
}

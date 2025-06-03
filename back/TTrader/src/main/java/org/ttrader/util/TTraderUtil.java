package org.ttrader.util;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public final class TTraderUtil {
    private TTraderUtil() {}
    public static JsonArrayBuilder ofStrings(String[] strings) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (String string : strings) {
            jsonArrayBuilder.add(string);
        }
        return jsonArrayBuilder;
    }

    public static JsonArrayBuilder ofStrings(Collection<String> strings) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (String string : strings) {
            jsonArrayBuilder.add(string);
        }
        return jsonArrayBuilder;
    }
    public static JsonArrayBuilder ofObjects(Collection<JsonObject> objects) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (JsonObject object : objects) {
            jsonArrayBuilder.add(object);
        }
        return jsonArrayBuilder;
    }
    public static String getStringDate(int dayShift) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayShift);
        Date day = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(day);
    }
}

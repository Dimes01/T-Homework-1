package com.example.utilities;

import com.example.models.Event;
import org.example.homework9.models.EventDate;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventDeserializer extends JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        int id = node.get("id").asInt();
        String name = node.get("title").asText();

        String priceStr = node.get("price").asText();
        int[] prices = parsePrices(priceStr);
        int minCost = prices[0];
        int maxCost = prices[1];

        JsonNode datesNode = node.get("dates");
        List<EventDate> datesList = new ArrayList<>();
        for (JsonNode dateNode : datesNode) {
            long fromDateMillis = dateNode.get("start").asLong() * 1000L;
            long toDateMillis = dateNode.get("end").asLong() * 1000L;
            Date fromDate = new Date(fromDateMillis);
            Date toDate = new Date(toDateMillis);
            datesList.add(new EventDate(fromDate, toDate));
        }
        EventDate[] dates = datesList.toArray(new EventDate[0]);

        int favoritesCount = node.get("favorites_count").asInt();

        return new Event(id, name, minCost, maxCost, dates, favoritesCount);
    }

    private int[] parsePrices(String priceStr) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(priceStr);

        int[] prices = new int[2];
        int index = 0;

        while (matcher.find() && index < 2) {
            prices[index] = Integer.parseInt(matcher.group());
            index++;
        }

        // Если найдено меньше двух чисел, заполняем оставшиеся нулями
        while (index < 2) {
            prices[index] = 0;
            index++;
        }

        return prices;
    }
}

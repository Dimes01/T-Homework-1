package com.example.tinkoff.utilities;

import com.example.tinkoff.models.Valute;
import com.example.tinkoff.models.ValuteCurs;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ValuteCursDeserializer extends StdDeserializer<ValuteCurs> {
    @Autowired
    private Environment env;

    protected ValuteCursDeserializer() {
        this(null);
    }

    protected ValuteCursDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ValuteCurs deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        var format = DateTimeFormatter
                .ofPattern(env.getProperty("spring.jackson.date-format", "dd/MM/yyyy"));

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        LocalDate date = LocalDate.parse(node.get("date").textValue(), format);
        String name = node.get("name").textValue();
        JsonNode valutesNode = node.get("valutes");
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        mapper.registerModule(new JavaTimeModule());
        List<Valute> valutes = mapper.readValue(valutesNode.traverse(mapper), new TypeReference<>() {  });

        return new ValuteCurs(date, name, valutes);
    }
}

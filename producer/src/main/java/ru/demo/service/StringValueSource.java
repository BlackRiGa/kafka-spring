package ru.demo.service;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.demo.model.StringValue;

public class StringValueSource implements ValueSource {
    private static final Logger log = LoggerFactory.getLogger(StringValueSource.class);
    private final AtomicLong nextValue = new AtomicLong(1);
    private final DataSender valueConsumer;

    public StringValueSource(DataSender dataSender) {
        this.valueConsumer = dataSender;
    }

    @Override
    public void generate() {
        var executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> valueConsumer.send(makeValue()), 0, 1, TimeUnit.HOURS);
        log.info("generation started");
    }

    private StringValue makeValue() {
        var id = nextValue.getAndIncrement();
        return new StringValue(id, "{\n" +
                "  \"url\":\"directions\",\n" +
                "  \"status\": 200,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 65,\n" +
                "      \"parent_id\": 6,\n" +
                "      \"name\": \"ООО \\\"Бирюч\\\"\",\n" +
                "      \"prefix\": \"Bir\",\n" +
                "      \"is_not_active\": \"0\",\n" +
                "      \"is_processed\": \"0\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"string\"\n" +
                "    },\n" +
                "    \"first\": {\n" +
                "      \"href\": \"string\"\n" +
                "    },\n" +
                "    \"last\": {\n" +
                "      \"href\": \"string\"\n" +
                "    },\n" +
                "    \"prev\": {\n" +
                "      \"href\": \"string\"\n" +
                "    },\n" +
                "    \"next\": {\n" +
                "      \"href\": \"string\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"_meta\": {\n" +
                "    \"totalCount\": 0,\n" +
                "    \"pageCount\": 0,\n" +
                "    \"currentPage\": 0,\n" +
                "    \"perPage\": 0\n" +
                "  }\n" +
                "}");
    }
}

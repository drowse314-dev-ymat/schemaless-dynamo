package ymat.practice.schemalessdynamo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;


@SpringBootApplication
@ComponentScan(basePackages = { "ymat.practice" })
public class Main {

    private static Optional<ConfigurableApplicationContext> applicationContext = Optional.empty();

    synchronized private ConfigurableApplicationContext initializeApplicationContext() {
        ConfigurableApplicationContext springContext =
                applicationContext.orElseGet(() -> SpringApplication.run(Main.class));
        applicationContext = Optional.of(springContext);
        return springContext;
    }


    public void handler(
            InputStream inputStream, OutputStream outputStream, Context lambdaContext) throws IOException {

        ConfigurableApplicationContext springContext = initializeApplicationContext();
        Gson gson = springContext.getBean(Gson.class);

        AnonymNestedModel model;
        try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"))) {
            model = gson.fromJson(reader, AnonymNestedModel.class);
        }

        save(model);

        String queryHashKey = StringUtils.isEmpty(model.getQueryHashKey()) ?
                model.getHashKey() : model.getQueryHashKey();
        Long queryTimestamp = StringUtils.isEmpty(model.getQueryTimestamp()) ?
                model.getTimestamp() : model.getQueryTimestamp();

        String response =
                load(queryHashKey, queryTimestamp)
                        // needs Type param, actually
                        .map(loaded -> gson.toJson(loaded, AnonymNestedModel.class).toUpperCase())
                        .orElse("not found");

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8")) {
            writer.write(response);
        }
    }

    private void save(AnonymNestedModel model) {
        ConfigurableApplicationContext springContext = initializeApplicationContext();
        Table streamdata = springContext.getBean(DynamoDB.class).getTable("streamdata");
        Gson gson = springContext.getBean(Gson.class);

        streamdata.putItem(
                new Item()
                    .withPrimaryKey("hashKey", model.getHashKey())
                    .withLong("timestamp", model.getTimestamp())
                    .withJSON("data", gson.toJson(model.getData())));
    }

    private Optional<AnonymNestedModel> load(String hashKey, Long timestamp) {
        ConfigurableApplicationContext springContext = initializeApplicationContext();
        Table streamdata = springContext.getBean(DynamoDB.class).getTable("streamdata");
        Gson gson = springContext.getBean(Gson.class);

        Item item = streamdata.getItem(
                new GetItemSpec()
                    .withPrimaryKey("hashKey", hashKey, "timestamp", timestamp));

        if (item == null) { return Optional.empty(); }

        AnonymNestedModel model = new AnonymNestedModel() {{
            setHashKey(item.getString("hashKey"));
            setTimestamp(item.getLong("timestamp"));
            setData(gson.fromJson(item.getJSON("data"), Object.class));
        }};

        return Optional.of(model);
    }
}
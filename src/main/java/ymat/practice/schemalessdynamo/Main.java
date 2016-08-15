package ymat.practice.schemalessdynamo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


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

        String response =
                load(model.getHashKey(), model.getTimestamp())
                        .map(loaded -> gson.toJson(loaded).toUpperCase())
                        .orElse("not found");

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8")) {
            writer.write(response);
        }
    }

    private void save(AnonymNestedModel model) {
        IDynamoDBMapper dynamoDBMapper = initializeApplicationContext().getBean(IDynamoDBMapper.class);
        dynamoDBMapper.save(model);
    }

    private Optional<AnonymNestedModel> load(String hashKey, Long timestamp) {
        IDynamoDBMapper dynamoDBMapper = initializeApplicationContext().getBean(IDynamoDBMapper.class);

        Map<String, String> aliases = new HashMap<String, String>() {
            { put("#timestamp", "timestamp"); }
        };
        Map<String, AttributeValue> params = new HashMap<String, AttributeValue>() {
            {
                put(":hashKey", new AttributeValue().withS(hashKey));
                put(":timestamp", new AttributeValue().withN(timestamp.toString()));
            }
        };

        PaginatedList<AnonymNestedModel> models = dynamoDBMapper.query(
                AnonymNestedModel.class,
                new DynamoDBQueryExpression<AnonymNestedModel>()
                        .withExpressionAttributeNames(aliases)
                        .withKeyConditionExpression(
                                "hashKey = :hashKey and #timestamp = :timestamp")
                        .withExpressionAttributeValues(params));

        return models.isEmpty() ?
                Optional.empty() :
                Optional.of(models.get(0));
    }
}
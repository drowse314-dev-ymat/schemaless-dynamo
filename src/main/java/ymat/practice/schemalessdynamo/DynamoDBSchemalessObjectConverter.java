package ymat.practice.schemalessdynamo;

import com.google.gson.Gson;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;


public class DynamoDBSchemalessObjectConverter implements DynamoDBTypeConverter<String, Object> {

    private static final Gson gson = new GsonConfig().gson();


    @Override
    public String convert(Object object) {
        return gson.toJson(object);
    }

    @Override
    public Object unconvert(String repr) {
        return gson.fromJson(repr, Object.class);
    }

}

package ymat.practice.schemalessdynamo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;


@DynamoDBTable(tableName = "streamdata")
@NoArgsConstructor
public class AnonymNestedModel {

    @DynamoDBHashKey
    @Getter
    @Setter
    private String hashKey;

    @DynamoDBRangeKey
    @Getter
    @Setter
    private Long timestamp;

    @DynamoDBTypeConverted(converter = DynamoDBSchemalessObjectConverter.class)
    @Getter
    @Setter
    private Object data;

}

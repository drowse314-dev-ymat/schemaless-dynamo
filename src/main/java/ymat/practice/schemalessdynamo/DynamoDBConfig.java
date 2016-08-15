package ymat.practice.schemalessdynamo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;


@Configuration
public class DynamoDBConfig {

    @Autowired
    AmazonDynamoDB dynamoDBClient;

    @Bean
    DynamoDB dynamoDB() {
        return new DynamoDB(dynamoDBClient);
    }

}

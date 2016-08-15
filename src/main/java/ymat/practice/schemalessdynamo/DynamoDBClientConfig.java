package ymat.practice.schemalessdynamo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;


@Configuration
public class DynamoDBClientConfig {

    @Bean
    AmazonDynamoDB dynamoDBClient() {
        AmazonDynamoDB dynamoDB = new AmazonDynamoDBAsyncClient(
                new BasicAWSCredentials(
                        "chage &",
                        "aska"));
        dynamoDB.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
        return dynamoDB;
    }

}

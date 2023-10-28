package org.example;


import org.example.operation.KinesisConsumer;
import org.example.operation.KinesisProducer;

import java.util.List;

public class KinesisProducerEncryptedRecords {
    public static void main(String[] args) {
        KinesisProducer producer = new KinesisProducer();

        String streamName = "myjava";
        String keyId = "7dfb3ea9-7120-4b8b-9438-b36b2aa4d4d7";
        String filePath = "/Users/shuvamoy/Documents/mylearning/src/main/resources/test.json";

        producer.parseJsonAndSendToKinesis(filePath, streamName, keyId);



    }
}

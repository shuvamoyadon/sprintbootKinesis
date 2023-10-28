package org.example.operation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import java.util.UUID;

public class KinesisProducer {

    private KmsClient kmsClient;
    private KinesisClient kinesisClient;

    String partitionKey = UUID.randomUUID().toString();

    public KinesisProducer() {
        this.kmsClient = KmsClient.builder()
                .region(Region.US_EAST_1) // Set your region
                .build();

        this.kinesisClient = KinesisClient.builder()
                .region(Region.US_EAST_1) // Set your region
                .build();
    }

    public byte[] encryptData(String keyId, byte[] plaintext) {

        EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(SdkBytes.fromByteArray(plaintext))
                .build();

        return kmsClient.encrypt(encryptRequest).ciphertextBlob().asByteArray();
    }


    public void putEncryptedRecordToKinesis(String streamName, String keyId, byte[] plaintext) {
        byte[] encryptedData = encryptData(keyId, plaintext);

        PutRecordRequest putRecordRequest = PutRecordRequest.builder()
                .streamName(streamName)
                .partitionKey(partitionKey) // Adjust as necessary
                .data(SdkBytes.fromByteArray(encryptedData))
                .build();

        kinesisClient.putRecord(putRecordRequest);
    }

    public void parseJsonAndSendToKinesis(String filePath, String streamName, String keyId) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    String jsonRecord = node.toString();
                    putEncryptedRecordToKinesis(streamName, keyId, jsonRecord.getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

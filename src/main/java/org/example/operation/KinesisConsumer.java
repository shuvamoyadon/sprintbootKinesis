package org.example.operation;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.Record;

import java.util.List;

public class KinesisConsumer {

    private KmsClient kmsClient;
    private KinesisClient kinesisClient;

    public KinesisConsumer() {
        this.kmsClient = KmsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        this.kinesisClient = KinesisClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    public byte[] decryptData(String keyId, byte[] ciphertext) {
        DecryptRequest decryptRequest = DecryptRequest.builder()
                .keyId(keyId)
                .ciphertextBlob(SdkBytes.fromByteArray(ciphertext))
                .build();

        return kmsClient.decrypt(decryptRequest).plaintext().asByteArray();
    }

    public void consumeAndDecryptData(String streamName, List<String> shardIds, String keyId) {
        for (String shardId : shardIds) {
            GetShardIteratorRequest getShardIteratorRequest = GetShardIteratorRequest.builder()
                    .streamName(streamName)
                    .shardId(shardId)
                    .shardIteratorType("TRIM_HORIZON")
                    .build();

            GetShardIteratorResponse getShardIteratorResponse = kinesisClient.getShardIterator(getShardIteratorRequest);
            String shardIterator = getShardIteratorResponse.shardIterator();

            while (shardIterator != null) {
                GetRecordsRequest getRecordsRequest = GetRecordsRequest.builder()
                        .shardIterator(shardIterator)
                        .limit(1000)
                        .build();

                kinesisClient.getRecords(getRecordsRequest).records().forEach(record -> {
                    byte[] decryptedData = decryptData(keyId, record.data().asByteArray());
                    System.out.println(new String(decryptedData));
                });

                shardIterator = kinesisClient.getRecords(getRecordsRequest).nextShardIterator();
            }
        }
    }


}

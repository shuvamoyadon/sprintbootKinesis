package org.example;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KinesisConsumer {

    private static final String STREAM_NAME = "myjava"; // Replace with your stream name
    private static final String KMS_KEY_ID = "7dfb3ea9-7120-4b8b-9438-b36b2aa4d4d7"; // Replace with your KMS key ID

    public static void main(String[] args) {
        KinesisClient kinesisClient = KinesisClient.builder().region(Region.US_EAST_1).build(); // Use the appropriate region

        DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder()
                .streamName(STREAM_NAME)
                .build();

        List<Shard> shards = kinesisClient.describeStream(describeStreamRequest).streamDescription().shards();

        ExecutorService executorService = Executors.newFixedThreadPool(shards.size());

        for (Shard shard : shards) {
            executorService.submit(new ShardConsumer(shard.shardId(), STREAM_NAME, KMS_KEY_ID));
        }

        executorService.shutdown();
    }
}

class ShardConsumer implements Runnable {

    private final String shardId;
    private final String streamName;
    private final String kmsKeyId;

    public ShardConsumer(String shardId, String streamName, String kmsKeyId) {
        this.shardId = shardId;
        this.streamName = streamName;
        this.kmsKeyId = kmsKeyId;
    }

    @Override
    public void run() {
        KinesisClient kinesisClient = KinesisClient.builder().region(Region.US_EAST_1).build(); // Use the appropriate region
        KmsClient kmsClient = KmsClient.builder().region(Region.US_EAST_1).build(); // Use the appropriate region

        try {
            GetShardIteratorRequest getShardIteratorRequest = GetShardIteratorRequest.builder()
                    .streamName(streamName)
                    .shardId(shardId)
                    .shardIteratorType(ShardIteratorType.TRIM_HORIZON)
                    .build();

            String shardIterator = kinesisClient.getShardIterator(getShardIteratorRequest).shardIterator();

            while (true) {
                GetRecordsRequest getRecordsRequest = GetRecordsRequest.builder()
                        .shardIterator(shardIterator)
                        .build();

                List<Record> records = kinesisClient.getRecords(getRecordsRequest).records();

                for (Record record : records) {
                    ByteBuffer encryptedData = record.data().asByteBuffer();

                    DecryptRequest decryptRequest = DecryptRequest.builder()
                            .ciphertextBlob(SdkBytes.fromByteBuffer(encryptedData))
                            .keyId(kmsKeyId)
                            .build();

                    ByteBuffer decryptedData = kmsClient.decrypt(decryptRequest).plaintext().asByteBuffer();

                    byte[] decryptedBytes = new byte[decryptedData.remaining()];
                    decryptedData.get(decryptedBytes);
                    String decryptedString = new String(decryptedBytes);


                    System.out.println("Shard: " + shardId + " - Decrypted Data: " + decryptedString + " - Sequence Number: " + record.sequenceNumber());
                }

                shardIterator = kinesisClient.getRecords(getRecordsRequest).nextShardIterator();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kinesisClient.close();
            kmsClient.close();
        }
    }
}

package org.example;

import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SharedConsumer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //props.put(ConsumerConfig.GROUP_ID_CONFIG, "shared-group");
        props.put("group.id", "shared-group-2");        // required
        props.put("share.group.id", "shared-group-2");  // enables share mode

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaShareConsumer<String, String> consumer = new KafkaShareConsumer<>(props);
        consumer.subscribe(Collections.singletonList("demo-topic-2"));

        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Shared Consumer: "
                        + record.value()
                        + " Partition: " + record.partition());
            }
        }
    }
}

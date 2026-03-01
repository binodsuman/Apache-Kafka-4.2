package org.example;


import org.apache.kafka.clients.producer.*;
import java.util.Properties;
import java.util.Random;

/**
bin/kafka-topics.sh --create --topic demo-topic --bootstrap-server localhost:9092 --partitions 1
 bin/kafka-topics.sh --list  --bootstrap-server localhost:9092
 bin/kafka-topics.sh --describe --topic demo-topic --bootstrap-server localhost:9092

**/

public class ProducerApp {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);



        for (int i = 1; i <= 100; i++) {
            //ProducerRecord<String, String> record = new ProducerRecord<>("demo-topic-1", "Message-" + i);


            String key = "Key-"+i;
            ProducerRecord<String, String> record = new ProducerRecord<>("demo-topic-2", key, "Message-" + i);

            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    //System.out.println("Sent Message: " + metadata.offset());
                    System.out.println("Sent Message: " + metadata.offset()+" Key : "+key);
                } else {
                    exception.printStackTrace();
                }
            });

            try {
                Thread.sleep(500);   // add delay
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        producer.close();
    }
}

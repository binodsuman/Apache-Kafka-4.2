package org.example;

// File: RebalanceDemo.java
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.*;
import java.util.*;

 public class RebalanceDemo {


    public static void main(String[] args) {
        String clientId = args[0];
        String protocol = args[1];  // "classic" or "consumer"

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("group.id", "test-group");
        props.setProperty("client.id", clientId);
        props.setProperty("group.protocol", protocol);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test-topic"));

        System.out.println(clientId + " started with " + protocol + " protocol");

        while (true) {
            consumer.poll(Duration.ofMillis(1000));
            // Just keep running, no processing needed
        }
    }
}


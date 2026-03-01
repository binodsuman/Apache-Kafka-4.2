package org.example;



import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

/**
 *
 * cd /Users/binod/Documents/software/kafka_2.13-4.2.0
 * bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic input-topic --partitions 1 --replication-factor 1
 *
 * bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic output-topic --partitions 1 --replication-factor 1
 *
 * bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic dlq-topic --partitions 1 --replication-factor 1
 *
 *
 */




import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Branched;

import java.util.Properties;

public class DLQDemo {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "custom-dlq-demo");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> stream = builder.stream("input-topic");
        // Just to input stream to show on console.
        stream.peek((key, value) ->
                System.out.println("INPUT => key=" + key + ", value=" + value)
        );

        // ✅ Kafka 4.x style branching
        stream.split()
                .branch(
                        (key, value) -> value != null &&
                                value.toUpperCase(Locale.ROOT).contains("BAD"),
                        Branched.withConsumer(ks -> ks.to("dlq-topic"))
                )
                .defaultBranch(
                        Branched.withConsumer(ks ->
                                ks.mapValues(value -> value.toUpperCase(Locale.ROOT))
                                        .to("output-topic")
                        )
                );

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        streams.start();

        System.out.println("Custom DLQ Demo Started...");
    }
}
package com.example.benchmark;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openjdk.jmh.annotations.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class KafkaBenchmark {

    private KafkaProducer<String, String>[] producers;
    private KafkaConsumer<String, String>[] consumers;
    private static final String TOPIC = "test_topic";

    @Param({"1", "3", "10"})
    private int producersCount;

    @Param({"1", "3", "10"})
    private int consumersCount;

    @Setup(Level.Trial)
    public void setup() {
        producers = new KafkaProducer[producersCount];
        consumers = new KafkaConsumer[consumersCount];

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        for (int i = 0; i < producersCount; i++) {
            producers[i] = new KafkaProducer<>(producerProps);
        }

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        for (int i = 0; i < consumersCount; i++) {
            consumers[i] = new KafkaConsumer<>(consumerProps);
            consumers[i].subscribe(Collections.singletonList(TOPIC));
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        for (KafkaProducer<String, String> producer : producers) {
            producer.close();
        }
        for (KafkaConsumer<String, String> consumer : consumers) {
            consumer.close();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Group("simple")
    public void testProducerSimple() {
        producers[0].send(new ProducerRecord<>(TOPIC, "key", "Hello World!"));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Group("simple")
    public void testConsumerSimple() {
        ConsumerRecords<String, String> records = consumers[0].poll(Duration.ofMillis(100));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Group("loadBalancing")
    public void testProducerLoadBalancing() {
        int producerId = (int) (Math.random() * producersCount);
        producers[producerId].send(new ProducerRecord<>(TOPIC, "key", "Hello World!"));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Group("loadBalancing")
    public void testConsumerLoadBalancing() {
        int consumerId = (int) (Math.random() * consumersCount);
        ConsumerRecords<String, String> records = consumers[consumerId].poll(Duration.ofMillis(100));
    }
}

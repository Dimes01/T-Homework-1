package com.example.benchmark;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openjdk.jmh.annotations.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@Fork(5)
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 20, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
public class KafkaBenchmark {

    private static final String TOPIC_NAME = "benchmarkTopic";
    private static final int COUNT = 3;
    private static final int MESSAGE_COUNT = 1000;
    private static final String MESSAGE = "Message";

    private Properties producerProperties;
    private Properties consumerProperties;

    @Setup(Level.Trial)
    public void setup() {
        producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092");
        producerProperties.put("key.serializer", StringSerializer.class.getName());
        producerProperties.put("value.serializer", StringSerializer.class.getName());

        consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", "localhost:9092");
        consumerProperties.put("group.id", "benchmarkGroup");
        consumerProperties.put("key.deserializer", StringDeserializer.class.getName());
        consumerProperties.put("value.deserializer", StringDeserializer.class.getName());
        consumerProperties.put("auto.offset.reset", "earliest");
    }

    private CompletableFuture<Void> singleProducer() {
        return CompletableFuture.runAsync(() -> {
            try (Producer<String, String> producer = new KafkaProducer<>(producerProperties)) {
                for (int i = 0; i < MESSAGE_COUNT; ++i) {
                    producer.send(new ProducerRecord<>(TOPIC_NAME, MESSAGE));
                }
            }
        });
    }

    private CompletableFuture<Void> singleConsumer() {
        return CompletableFuture.runAsync(() -> {
            try (Consumer<String, String> consumer = new KafkaConsumer<>(consumerProperties)) {
                consumer.subscribe(Collections.singletonList(TOPIC_NAME));
                int messagesProcessed = 0;
                while (messagesProcessed < MESSAGE_COUNT) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    messagesProcessed += records.count();
                }
            }
        });
    }

    private CompletableFuture<Void>[] multipleProducers(int count) {
        CompletableFuture<Void>[] producerFutures = new CompletableFuture[count];
        for (int i = 0; i < count; ++i) {
            producerFutures[i] = singleProducer();
        }
        return producerFutures;
    }

    private CompletableFuture<Void>[] multipleConsumers(int count) {
        CompletableFuture<Void>[] consumerFutures = new CompletableFuture[count];
        for (int i = 0; i < count; ++i) {
            consumerFutures[i] = singleConsumer();
        }
        return consumerFutures;
    }

    @Benchmark
    public void testSingleProducerSingleConsumer() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> producerFuture = singleProducer();
        CompletableFuture<Void> consumerFuture = singleConsumer();
        producerFuture.get();
        consumerFuture.get();
    }

    @Benchmark
    public void testMultipleProducersSingleConsumer() throws InterruptedException, ExecutionException {
        CompletableFuture<Void>[] producerFutures = multipleProducers(COUNT);
        CompletableFuture<Void> consumerFuture = singleConsumer();

        CompletableFuture.allOf(producerFutures).get();
        consumerFuture.get();
    }

    @Benchmark
    public void testSingleProducerMultipleConsumers() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> producerFuture = singleProducer();
        CompletableFuture<Void>[] consumerFutures = multipleConsumers(COUNT);

        producerFuture.get();
        CompletableFuture.allOf(consumerFutures).get();
    }

    @Benchmark
    public void testMultipleProducersMultipleConsumers() throws InterruptedException, ExecutionException {
        CompletableFuture<Void>[] producerFutures = multipleProducers(COUNT);
        CompletableFuture<Void>[] consumerFutures = multipleConsumers(COUNT);

        CompletableFuture.allOf(producerFutures).get();
        CompletableFuture.allOf(consumerFutures).get();
    }

    @Benchmark
    public void testTenProducersTenConsumers() throws InterruptedException, ExecutionException {
        CompletableFuture<Void>[] producerFutures = multipleProducers(10);
        CompletableFuture<Void>[] consumerFutures = multipleConsumers(10);

        CompletableFuture.allOf(producerFutures).get();
        CompletableFuture.allOf(consumerFutures).get();
    }

}

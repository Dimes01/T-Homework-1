package com.example.benchmark;

import com.rabbitmq.client.*;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(5)
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 20, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
public class RabbitMQBenchmark {

    private static final String QUEUE_NAME = "benchmarkQueue";
    private static final String EXCHANGE_NAME = "benchmarkExchange";

    // COUNT - количество продюсеров или консюмеров
    private static final int COUNT = 3;
    private static final int MESSAGE_COUNT = 1000;
    private static final String MESSAGE = "Message";
    private static final byte[] MESSAGE_BYTES = MESSAGE.getBytes();

    private Connection connection;
    private Channel channel;

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("user");
        factory.setPassword("password");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "benchmarkKey");
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    private CompletableFuture<Void> SingleProducer(int countMessages) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < countMessages; ++i)
                    channel.basicPublish(EXCHANGE_NAME, "benchmarkKey", null, MESSAGE_BYTES);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private CompletableFuture<Void> SingleConsumer() {
        return CompletableFuture.runAsync(() -> {
            try {
                channel.basicConsume(QUEUE_NAME, true, (consumerTag, delivery) -> {}, consumerTag -> {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private CompletableFuture<Void>[] MultipleProducer(int countProducer) {
        int countMessages = MESSAGE_COUNT / countProducer;
        CompletableFuture<Void>[] producerFutures = new CompletableFuture[countProducer];
        for (int i = 0; i < countProducer; ++i) {
            producerFutures[i] = SingleProducer(countMessages);
        }
        return producerFutures;
    }

    private CompletableFuture<Void>[] MultipleConsumer(int count) {
        CompletableFuture<Void>[] consumerFutures = new CompletableFuture[count];
        for (int i = 0; i < count; ++i) {
            consumerFutures[i] = SingleConsumer();
        }
        return consumerFutures;
    }

    @Benchmark
    public void testSingleProducerSingleConsumer() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> producerFuture = SingleProducer(MESSAGE_COUNT);
        CompletableFuture<Void> consumerFuture = SingleConsumer();
        producerFuture.get();
        consumerFuture.get();
    }

    @Benchmark
    public void testMultipleProducersSingleConsumer() throws InterruptedException, ExecutionException {
        CompletableFuture<?>[] producerFutures = MultipleProducer(COUNT);
        CompletableFuture<?> consumerFuture = SingleConsumer();

        CompletableFuture.allOf(producerFutures).get();
        consumerFuture.get();
    }

    @Benchmark
    public void testSingleProducerMultipleConsumers() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> producerFuture = SingleProducer(MESSAGE_COUNT);
        CompletableFuture<Void>[] consumerFutures = MultipleConsumer(COUNT);

        producerFuture.get();
        CompletableFuture.allOf(consumerFutures).get();
    }

    @Benchmark
    public void testMultipleProducersMultipleConsumers() throws InterruptedException, ExecutionException {
        CompletableFuture<Void>[] producerFutures = MultipleProducer(COUNT);
        CompletableFuture<Void>[] consumerFutures = MultipleConsumer(COUNT);

        CompletableFuture.allOf(producerFutures).get();
        CompletableFuture.allOf(consumerFutures).get();
    }

    @Benchmark
    public void testTenProducersTenConsumers() throws InterruptedException, ExecutionException {
        CompletableFuture<Void>[] producerFutures = MultipleProducer(10);
        CompletableFuture<Void>[] consumerFutures = MultipleConsumer(10);

        CompletableFuture.allOf(producerFutures).get();
        CompletableFuture.allOf(consumerFutures).get();
    }
}

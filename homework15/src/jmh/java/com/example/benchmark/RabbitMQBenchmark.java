package com.example.benchmark;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@State(Scope.Benchmark)
public class RabbitMQBenchmark {

    private static final String QUEUE_NAME = "test_queue";
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("user");
        factory.setPassword("password");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testProducer() throws IOException {
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testConsumer() throws IOException, InterruptedException {
        GetResponse response = channel.basicGet(QUEUE_NAME, true);
        if (response == null) {
            Thread.sleep(1);
        }
    }
}
package com.example.benchmark;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class RabbitMQBenchmark {
    private static final String QUEUE_NAME = "test_queue";
    private ConnectionFactory factory;
    private Connection[] connections;
    private Channel[] channels;

    @Param({"1", "3", "10"})
    private int producersCount;

    @Param({"1", "3", "10"})
    private int consumersCount;

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("user");
        factory.setPassword("password");

        connections = new Connection[producersCount + consumersCount];
        channels = new Channel[producersCount + consumersCount];

        for (int i = 0; i < producersCount + consumersCount; ++i) {
            connections[i] = factory.newConnection();
            channels[i] = connections[i].createChannel();
            channels[i].queueDeclare(QUEUE_NAME, false, false, false, null);
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException, TimeoutException {
        for (int i = 0; i < producersCount + consumersCount; ++i) {
            channels[i].close();
            connections[i].close();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Group("simple")
    public void testProducerSimple() throws IOException {
        String message = "Hello World!";
        channels[0].basicPublish("", QUEUE_NAME, null, message.getBytes());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Group("simple")
    public void testConsumerSimple() throws IOException, InterruptedException {
        GetResponse response = channels[0].basicGet(QUEUE_NAME, true);
        if (response == null) {
            Thread.sleep(1);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Group("loadBalancing")
    public void testProducerLoadBalancing() throws IOException {
        String message = "Hello World!";
        int producerId = (int) (Math.random() * producersCount);
        channels[producerId].basicPublish("", QUEUE_NAME, null, message.getBytes());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Group("loadBalancing")
    public void testConsumerLoadBalancing() throws IOException, InterruptedException {
        int consumerId = (int) (Math.random() * consumersCount);
        GetResponse response = channels[producersCount + consumerId].basicGet(QUEUE_NAME, true);
        if (response == null) {
            Thread.sleep(1);
        }
    }
}
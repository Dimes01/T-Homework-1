import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaExample {

    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topic = "example-topic";
        String groupId = "example-group";

        // Настройки продюсера
        Properties producerProperties = new Properties();
        producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Настройки потребителя
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Создание продюсера и потребителя
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);

        // Подписка потребителя на топик
        consumer.subscribe(Collections.singletonList(topic));

        // Отправка сообщения
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "key", "hello, kafka!");
        for (int i = 0; i < 10; ++i) {
            producer.send(record);
        }

        // Чтение сообщений
        boolean running = true;
        while (running) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> consumerRecord : records) {
                System.out.printf("Received message: key = %s, value = %s, partition = %d, offset = %d%n",
                    consumerRecord.key(), consumerRecord.value(), consumerRecord.partition(), consumerRecord.offset());
                running = false; // Завершаем после получения первого сообщения
            }
        }

        // Закрытие продюсера и потребителя
        producer.close();
        consumer.close();
    }
}


/**
 * 
 * @author (Nimai Kumar, Shashanth)
 * @version (v1.0 10/15/2026)
 *
 */



package messaging;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class MessageConsumer {
    public static void main(String[] args) {
        Properties settings = new Properties();

        settings.put("bootstrap.servers", "localhost:9092");
        settings.put("group.id", "navychat-console-demo");
        settings.put("key.deserializer",
                StringDeserializer.class.getName());
        settings.put("value.deserializer",
                StringDeserializer.class.getName());
        settings.put("auto.offset.reset", "earliest");
        settings.put("enable.auto.commit", "true");

        try (KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(settings)) {

            consumer.subscribe(List.of("chat-msg"));

            System.out.println("Waiting for messages. Press Ctrl+C to stop.");

            while (true) {
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(
                            record.key() + ": " + record.value()
                    );
                }
            }
        }
    }
}

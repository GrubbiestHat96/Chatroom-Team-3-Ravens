
/**
 *
 * @author (Nimai K, Shashanth)
 * @version (v1.0 10/15/2026)
 */

package messaging;

import java.util.Properties;
import model.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class MessageQueue implements AutoCloseable {
    private final KafkaProducer<String, String> producer;
    private final String topic = "chat-msg";

    public MessageQueue() {
        Properties settings = new Properties();

        settings.put("bootstrap.servers", "localhost:9092");
        settings.put("key.serializer", StringSerializer.class.getName());
        settings.put("value.serializer", StringSerializer.class.getName());
        settings.put("acks", "all");

        producer = new KafkaProducer<>(settings);
    }

    public void sendMessage(Message message) throws Exception {
        ProducerRecord<String, String> record = new ProducerRecord<>(
            topic,
            message.getSender(),
            message.getTxt()
        );

        producer.send(record).get();
        System.out.println("Sent: " + message);
    }

    @Override
    public void close() {
        producer.close();
    }
}

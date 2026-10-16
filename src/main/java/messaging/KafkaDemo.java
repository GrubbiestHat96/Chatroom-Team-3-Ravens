

/**
 *
 *
 * @author (Nimai Kumar, Shashanth)
 * @version (v1.0, 10/15/2026)
 *
 */

package messaging;

import model.Message;

public class KafkaDemo {
    public static void main(String[] args) throws Exception {
        Message message = new Message("Nimai", "Hello NavyChat!");

        System.out.println(message);

        try (MessageQueue queue = new MessageQueue()) {
            queue.sendMessage(message);
        }
    }
}

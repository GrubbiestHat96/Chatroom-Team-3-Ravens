# Chatroom-Team-3-Ravens

## Requirements

Install JDK 25 and Apache Maven. Check `java -version`, `javac -version`,
and `mvn -version`. Run the following commands from your cloned project folder.
The first build needs internet access to download dependencies.

## Build (each group member)

```powershell
mvn clean package dependency:copy-dependencies
```

This creates compiled files and dependency jars in `target`. No `work` folder
or files from another person's computer are needed. Rebuild after source edits.

## Run locally

In one terminal, start the server:

```powershell
java -cp "target/classes;target/dependency/*" server.Server
```

In two separate terminals, start clients:

```powershell
java -cp target/classes client.Client localhost
```

Each client displays its assigned name. To send to `client 1`, type:

```text
Hello>client 1
```

Put the message before `>` and the exact connected recipient name after it.
Type `logout` to leave. Stop the server with Ctrl+C.
Names increment on each connection and reset when the server restarts.

On macOS/Linux, replace the semicolon in the server classpath with a colon.

## Group members on different computers

Only one person runs the server. Everyone else runs a client using the server
computer's reachable hostname or IP address instead of `localhost`:

```powershell
java -cp target/classes client.Client SERVER_ADDRESS
```

Replace `SERVER_ADDRESS` with the actual address. Port 5000 must be reachable
between the computers. `localhost` always refers to your own computer.

## Kafka status

The server constructs a Kafka producer configured for `localhost:9092`, but the
current chat handler does not call `sendMessage`. Chat delivery currently uses
sockets. Kafka publishing/consuming is not yet integrated into chat delivery.
Running the separate demo requires Kafka at that address and the `chat-msg` topic:

```powershell
java -cp "target/classes;target/dependency/*" messaging.MessageConsumer
java -cp "target/classes;target/dependency/*" messaging.KafkaDemo
```

Run those two commands in separate terminals. This verifies the Kafka demo,
not the chat handler's Kafka integration.

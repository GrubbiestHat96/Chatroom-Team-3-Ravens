package server;
import java.net.*;
import java.io.*;
public class FeedbackCheck {
 public static void main(String[] args) throws Exception {
  try(ServerSocket listener = new ServerSocket(0); Socket client = new Socket("localhost",listener.getLocalPort()); Socket peer = listener.accept()) {
   client.setSoTimeout(2000);
   clientHandler h = new clientHandler(peer,"client 0",new DataInputStream(peer.getInputStream()),new DataOutputStream(peer.getOutputStream()),null);
   Server.active.add(h); Thread t = new Thread(h);t.start();
   DataInputStream in = new DataInputStream(client.getInputStream());DataOutputStream out = new DataOutputStream(client.getOutputStream());
   if(!in.readUTF().contains("Connected as client 0"))throw new AssertionError();
   out.writeUTF("hello");if(!in.readUTF().contains("Use:"))throw new AssertionError();
   out.writeUTF("hello>client 99");if(!in.readUTF().contains("Recipient not connected"))throw new AssertionError();
   out.writeUTF("hello>client 0");if(!in.readUTF().equals("client 0 : hello"))throw new AssertionError();
   out.writeUTF("logout");t.join(2000);if(t.isAlive() || !Server.active.isEmpty())throw new AssertionError();
   System.out.println("PASS: identity, format feedback, unknown recipient feedback, delivery, logout cleanup");
  }
 }
}

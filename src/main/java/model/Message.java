
/**
 *
 * @author (Nimai Kumar, Shashanth) 
 * @version (v1.0 10/15/2026)
 *
 */

package model;

public class Message {

	private final String sender;
	private final String txt;

	public Message(String sender, String txt) {
		this.sender = sender;
		this.txt = txt;
	}

	public String getSender() {
		return sender;
	}

	public String getTxt() {
		return txt;
	}

	@Override
	public String toString() {
			return sender + ": " + txt;
		}
	}

package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;

public class ReceivedPacketExtension implements PacketExtension {

	public int id;
	public String uid;

	public ReceivedPacketExtension(String uid) {
		super();
		this.uid = uid;
	}

	public String getElementName() {
		return "received";
	}

	public String getNamespace() {
		return "received";
	}

	public String toXML() {
		return "<received unique_id=\"" + uid + "\">"
				+ System.currentTimeMillis() / 1000 + "</received>";
	}

}
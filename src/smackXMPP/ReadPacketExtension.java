package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;

public class ReadPacketExtension implements PacketExtension {

	public int id;
	public String uid;

	public ReadPacketExtension(String uid) {
		super();
		this.uid = uid;
	}

	public String getElementName() {
		return "read";
	}

	public String getNamespace() {
		return "read";
	}

	public String toXML() {
		return "<read unique_id=\"" + uid + "\">"
				+ System.currentTimeMillis() / 1000 + "</read>";
	}
}
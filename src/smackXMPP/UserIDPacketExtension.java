package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;

public class UserIDPacketExtension implements PacketExtension {

	public int id;
	public String value;

	public UserIDPacketExtension(String value) {
		super();
		this.value = value.trim();
	}

	public String getElementName() {
		return "user_id";
	}

	public String getNamespace() {
		return "user_id";
	}

	public String getValue(){
		return value;
	}

	public String toXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(getElementName()).append(">");
		buf.append(value);
		buf.append("</").append(getElementName()).append(">");
		return buf.toString();
	}

}
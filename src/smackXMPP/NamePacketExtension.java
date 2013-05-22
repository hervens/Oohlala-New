package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;

public class NamePacketExtension implements PacketExtension {

	public int id;
	public String value;

	public NamePacketExtension(String value) {
		super();
		this.value = value.trim();
	}

	public String getElementName() {
		return "name";
	}

	public String getNamespace() {
		return "name";
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
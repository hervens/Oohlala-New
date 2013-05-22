package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;

public class ImagePacketExtension implements PacketExtension {

	public int id;
	public String value;

	public ImagePacketExtension(String value) {
		super();
		this.value = value.trim();
	}

	public String getElementName() {
		return "image";
	}

	public String getNamespace() {
		return "image";
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
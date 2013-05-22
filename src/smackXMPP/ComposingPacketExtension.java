package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;

public class ComposingPacketExtension implements PacketExtension {

	public ComposingPacketExtension() {
		super();
	}

	public String getElementName() {
		return "composing";
	}

	public String getNamespace() {
		return "composing";
	}

	public String toXML() {
		return "<composing></composing>";
	}

}
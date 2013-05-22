package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;

public class PausedPacketExtension implements PacketExtension {

	public PausedPacketExtension() {
		super();
	}

	public String getElementName() {
		return "paused";
	}

	public String getNamespace() {
		return "paused";
	}

	public String toXML() {
		return "<paused></paused>";
	}

}
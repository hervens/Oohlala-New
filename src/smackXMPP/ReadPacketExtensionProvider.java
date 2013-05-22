package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class ReadPacketExtensionProvider implements PacketExtensionProvider {
	
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		ReadPacketExtension ex = new ReadPacketExtension(
				parser.getAttributeValue(parser.getAttributeCount() - 1));
		return ex;	
	}
}

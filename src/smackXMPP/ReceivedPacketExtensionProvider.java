package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

public class ReceivedPacketExtensionProvider implements PacketExtensionProvider {
	
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		ReceivedPacketExtension ex = new ReceivedPacketExtension(parser.getAttributeValue(parser.getAttributeCount() - 1));
		return ex;	
	}
}

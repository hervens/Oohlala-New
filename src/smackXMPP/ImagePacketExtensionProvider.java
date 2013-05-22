package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

public class ImagePacketExtensionProvider implements PacketExtensionProvider {
	
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		ImagePacketExtension ex = new ImagePacketExtension(parser.nextText());
		return ex;	
	}
}

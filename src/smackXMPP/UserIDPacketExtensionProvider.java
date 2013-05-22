package smackXMPP;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

public class UserIDPacketExtensionProvider implements PacketExtensionProvider {
	
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		UserIDPacketExtension ex = new UserIDPacketExtension(parser.nextText());
		return ex;	
	}
}

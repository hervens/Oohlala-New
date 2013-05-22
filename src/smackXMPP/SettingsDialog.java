package smackXMPP;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import user.Profile;

import com.gotoohlala.R;

/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class SettingsDialog extends Dialog {
    private XMPPClient xmppClient;

    public SettingsDialog(XMPPClient xmppClient) {
        super(xmppClient);
        this.xmppClient = xmppClient;
    }

    public void connect() {
        //-------------- testing xmpp -------------------
        String host = "host.oohlaladeals.com";
        String port = "5222";
        String service = "host.oohlaladeals.com";
        String username = Profile.jid + "@" + host;
        String password = Profile.xmppPass;
        //------------------------------------------------
        
        // Create a connection
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration(host, Integer.parseInt(port), service);
        
        XMPPConnection connection = new XMPPConnection(connConfig);
        
	        try {
	            connection.connect();
	            Log.i("XMPPClient", "[SettingsDialog] Connected to " + connection.getHost());
	        } catch (XMPPException ex) {
	            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + connection.getHost());
	            Log.e("XMPPClient", ex.toString());
	            xmppClient.setConnection(null);
	        }
	        try {
	            connection.login(username, password);
	            Log.i("XMPPClient", "Logged in as " + connection.getUser());
	
	            // Set the status to available
	            Presence presence = new Presence(Presence.Type.available);
	            connection.sendPacket(presence);
	            xmppClient.setConnection(connection);
	        } catch (XMPPException ex) {
	            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as " + username);
	                
	            connect();
	        }
	        dismiss();
	        
    }
    
    public void disconnect() {
        //-------------- testing xmpp -------------------
        String host = "host.oohlaladeals.com";
        String port = "5222";
        String service = "host.oohlaladeals.com";
        String username = Profile.jid + "@" + host;
        String password = Profile.xmppPass;
        //------------------------------------------------
        
        // Create a connection
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration(host, Integer.parseInt(port), service);
        XMPPConnection connection = new XMPPConnection(connConfig);
      
        connection.disconnect();
        Log.i("XMPPClient", "[SettingsDialog] disconnected to " + connection.getHost());
     
        dismiss();
    }
    
    public void connectGmail(){
    	ConnectionConfiguration cc = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
    	XMPPConnection connection = new XMPPConnection(cc);
    	try {
    	     connection.connect();
    	    
    	     // You have to specify your gmail addres WITH @gmail.com at the end
    	     connection.login("jameswang218@gmail.com", "password", "resource");
    	 
    	     // See if you are authenticated
    	     System.out.println(connection.isAuthenticated());
    	 
    	} catch (XMPPException e1) {
    	     e1.printStackTrace();
    	}
    }
}

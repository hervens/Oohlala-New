package smackXMPP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import network.RetrieveData;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

public class XMPPChatModel implements Serializable {
	/**
	 * 
	 */
	private final long serialVersionUID = 1L;
	
	public String message_id;
	public String msg;
	public String imageUrl;
	public String time;
	public int type;
	public int realtime;
	public int read;
	public String unique_id;
	public Bitmap bitmap;
	
	public XMPPChatModel(String message_id, String msg, String imageUrl, String time, int type, int realtime, int read, String unique_id, Bitmap bitmap) {
		this.message_id = message_id;
		this.msg = msg;
		this.imageUrl = imageUrl;
		this.time = time;
		this.type = type;
		this.realtime = realtime;
		this.read = read;
		this.unique_id = unique_id;
		this.bitmap = bitmap;
	}
	
}

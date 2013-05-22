package smackXMPP;

import inbox.Inbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import network.RetrieveData;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import profile.Badge;
import profile.OthersProfile;
import user.Profile;
import ManageThreads.TaskQueueImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import campuswall.CampusWall;
import campuswall.CampusWallImage;
import campuswall.CampusWallPostThread;

import com.gotoohlala.R;
import com.gotoohlala.SetXMPPConnection;

import datastorage.Base64;
import datastorage.CacheInternalStorage;
import datastorage.ConvertDpsToPixels;
import datastorage.DeviceDimensions;
import datastorage.Fonts;
import datastorage.ImageLoader;
import datastorage.Rest;
import datastorage.RestClient;
import datastorage.TimeCounter;
import datastorage.Rest.request;

public class XMPPClient extends Activity {
	
    private List<XMPPChatModel> messages = new ArrayList<XMPPChatModel>();
    private List<XMPPChatModel> messagesCache = new ArrayList<XMPPChatModel>();
    private List<XMPPChatModel> messagesSaved = new ArrayList<XMPPChatModel>();
    private Handler mHandler = new Handler();
    
    private EditText mSendText;
    private ListView mList;
    private ImageView ivComposing;
    private ProgressBar pbChatLoading;
    
    Button bUploadPicture;
    
    private XMPPConnection connection;
    private long time, timeActionDown;
    private boolean composing = false;
    
    //YOU CAN EDIT THIS TO WHATEVER YOU WANT
    private static final int SELECT_PICTURE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int CAMERA_PIC_REQUEST = 1337;  

    private String selectedImagePath, filemanagerstring, jid, first_name, last_name, to, avatar;
    private int user_id;
    private String last_message_id = "0";
    
    XMPPChatArrayAdapter adapter;
    boolean adapterSetUp = false;
    
    updateTimeCounter counter;
    int time_until_end = 99999;
    boolean counter_on = true;
    
    private Uri mImageCaptureUri;
    
    loadBitmapThreadAgain loadBitmapThreadAgain;
    PacketListener pl;
    
    Bitmap photoUpload;
    Button bLoadEarlierButton;
    RelativeLayout rlLoadEarlierButton;
    
    int cut = 0;
	int number = 0;
	boolean lastcut = false;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.i("XMPPClient", "onCreate called");
        setContentView(R.layout.chat);
        
        TextView header = (TextView) findViewById(R.id.header);
		header.setTypeface(Fonts.getOpenSansBold(XMPPClient.this));
        
        Bundle b = getIntent().getExtras();
        jid = b.getString("jid");
        first_name = b.getString("first_name");
        last_name = b.getString("last_name");
        avatar = b.getString("avatar");
        user_id = b.getInt("user_id");
        
        to = jid + "@host.oohlaladeals.com";
        
        ProviderManager.getInstance().addExtensionProvider("image",
				"jabber:client", new ImagePacketExtensionProvider());
        ProviderManager.getInstance().addExtensionProvider("name",
				"jabber:client", new NamePacketExtensionProvider());
        ProviderManager.getInstance().addExtensionProvider("user_id",
				"jabber:client", new UserIDPacketExtensionProvider());
        ProviderManager.getInstance().addExtensionProvider("received",
				"jabber:client", new ReceivedPacketExtensionProvider());
        ProviderManager.getInstance().addExtensionProvider("read",
				"jabber:client", new ReadPacketExtensionProvider());
        ProviderManager.getInstance().addExtensionProvider("composing",
				"jabber:client", new ComposingPacketExtensionProvider());
        ProviderManager.getInstance().addExtensionProvider("paused",
				"jabber:client", new PausedPacketExtensionProvider());
        
        Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    			imm.hideSoftInputFromWindow(mSendText.getWindowToken(), 0);
    			
				onBackPressed();
			}
	    }); 
		
		rlLoadEarlierButton = (RelativeLayout) findViewById(R.id.rlLoadEarlierButton);
		bLoadEarlierButton = (Button) findViewById(R.id.bLoadEarlierButton);
		bLoadEarlierButton.setTypeface(Fonts.getOpenSansBold(XMPPClient.this));
		bLoadEarlierButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	pbChatLoading.setVisibility(View.VISIBLE);
            	cut++;
            	
            	TaskQueueImage.addTask(new getUserChatHistoryThread2(), XMPPClient.this);
            }
        });
		
		mSendText = (EditText) findViewById(R.id.etSendText);
        mList = (ListView) findViewById(R.id.lvListMessages);
        TextView tvChatName = (TextView) findViewById(R.id.tvChatName);
        bUploadPicture = (Button) findViewById(R.id.bUploadPicture);
        Button bSend = (Button) findViewById(R.id.bSend);
        Button bBlock = (Button) findViewById(R.id.bBlock);
        Button bViewProfile = (Button) findViewById(R.id.bViewProfile);
        ImageView ivThumb = (ImageView) findViewById(R.id.ivThumb);
        ivComposing = (ImageView) findViewById(R.id.ivComposing);
        pbChatLoading = (ProgressBar) findViewById(R.id.pbChatLoading);
        RelativeLayout chatHeader = (RelativeLayout) findViewById(R.id.chatHeader);
        
        if (avatar != null){
        	ivThumb.setImageBitmap(ImageLoader.chatImageStoreAndLoad(avatar, getApplicationContext(), 0));
        }
		
        tvChatName.setText(first_name + " " + last_name);
        
        setListAdapter();
        
        //Thread thread = new TimeThread();
        //thread.start();
        counter = new updateTimeCounter(time_until_end, 200);
        counter.start();
        
        //get user chat cache from internal cache dir
        /*
        if(CacheInternalStorage.getObject(getApplicationContext(), first_name + last_name + "_" + Profile.firstName + Profile.lastName + "_chat.log") != null){
        	messagesSaved.addAll(CacheInternalStorage.getObject(getApplicationContext(), first_name + last_name + "_" + Profile.firstName + Profile.lastName + "_chat.log"));
        		
        	Log.i("found chat log:", "yes");
        	
        	last_message_id = messagesSaved.get(messagesSaved.size()-1).message_id;
        	Log.i("last_message_id:", last_message_id);
        } else {
        	Log.i("found chat log:", "no");
        }
        */
        
        /*
        mList.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
             // TODO Auto-generated method stub
            	if(mSendText.isFocused()){
            		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    			imm.hideSoftInputFromWindow(mSendText.getWindowToken(), 0);
	    			mSendText.clearFocus();
            	} else {
            		//InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    			//imm.showSoftInput(mSendText, InputMethodManager.SHOW_FORCED);
	    			//mSendText.requestFocus();
            	}
            	return true;
            }
        });
        */
        
        //set a listener to check whether user is typing or not
        mSendText.setOnKeyListener(new EditText.OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
				
				if (keyCode == KeyEvent.KEYCODE_ENTER){
					String text = mSendText.getText().toString();
		                
					if (text.trim().length() > 0) {
			        	//Log.i("XMPPClient", "Sending text [" + text + "] to [" + to + "]");
			        	Message msg = new Message(to, Message.Type.chat);
			          	msg.setBody(text);
			          	msg.addExtension(new NamePacketExtension(Profile.displayName));
			          	msg.addExtension(new UserIDPacketExtension(String.valueOf(Profile.userId)));
			          	if (connection != null){
        	            	if (connection.isConnected()){
        	            		connection.sendPacket(msg);
        	            	}
			          	}
			          	
			          	//Log.i("send packetID:", msg.getPacketID());
			          	messages.add(new XMPPChatModel(msg.getPacketID(), text, null, null, 0, getTime(), 0, msg.getPacketID(), null));
			                
			          	//setListAdapter();
			          	updateListAdapter();
			          	scrollToBottom();
			                
			          	mSendText.setText(null);
		          	}
		        	return true;
				}
				return false;				
			}
		});
		
		mSendText.addTextChangedListener(new TextWatcher() {
			  public void afterTextChanged(Editable s) {
			       //do something
			  }

			  public void beforeTextChanged(CharSequence s, int start, int count, int after){
			       //do something
			  }

			  public void onTextChanged(CharSequence s, int start, int before, int count) { 
			       //do something
				  timeActionDown = System.currentTimeMillis();
			  }
		});
        
        bUploadPicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	getPicture();
            }
        });

        // Set a listener to send a chat text message
        bSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String text = mSendText.getText().toString();
                
                if (text.trim().length() > 0) {
	                //Log.i("XMPPClient", "Sending text [" + text + "] to [" + to + "]");
	                Message msg = new Message(to, Message.Type.chat);
	                msg.setBody(text);
	                msg.addExtension(new NamePacketExtension(Profile.displayName));
	                msg.addExtension(new UserIDPacketExtension(String.valueOf(Profile.userId)));
	                
	                if (connection != null){
    	            	if (connection.isConnected()){
    	            		connection.sendPacket(msg);
    	            	}
	                }
	                messages.add(new XMPPChatModel(msg.getPacketID(), text, null, null, 0, getTime(), 0, msg.getPacketID(), null));
	                
	                //setListAdapter();
	                updateListAdapter();
	                scrollToBottom();
	                
	                mSendText.setText(null);
                }
            }
        });
        
        bBlock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	showAlertDialog(user_id);
            }
        }); 
        
        chatHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	/*
            	Bundle extras = new Bundle();
				extras.putInt("user_id", user_id);
				Intent i = new Intent(getApplicationContext(), OthersProfile.class);
				i.putExtras(extras);
				startActivity(i);
				*/
            }
        }); 
        
        bViewProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	/*
            	Bundle extras = new Bundle();
				extras.putInt("user_id", user_id);
				Intent i = new Intent(getApplicationContext(), OthersProfile.class);
				i.putExtras(extras);
				startActivity(i);
				*/
            }
        }); 
        
    	SetXMPPConnection.setJidCheck(jid);
    	
    	if (SetXMPPConnection.connection == null){
    		runOnUiThread(new Runnable() {
			    public void run() {
			    	XMPPConnectionConnect();
			    }
    		});
    		
    		new ReSetConnection().execute();
    	} else if (!SetXMPPConnection.connection.isConnected()){
    		runOnUiThread(new Runnable() {
			    public void run() {
			    	XMPPConnectionConnect();
			    }
    		});
    		
    		new ReSetConnection().execute();
    	} else {
    		setConnection(SetXMPPConnection.connection);
    	}   	
    }
    
    class getUserChatHistoryThread2 extends Thread {
	    // This method is called when the thread runs
    	List<XMPPChatModel> messagesTemp = new ArrayList<XMPPChatModel>();
    	
	    public void run() {
	    	int start = cut*25 + 1;
			int end = cut*25 + 25;
	    	RestClient result = null;
			try {
				result = new request().execute(Rest.CHAT_MESSAGE + start + ";" + end + "?user_id=" + user_id, Rest.OSESS + Profile.sk, Rest.GET).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				JSONArray chatMsgs = new JSONArray(result.getResponse());
				Log.i("chatMsgs: ", chatMsgs.toString());
				
				for (int i = 0; i < chatMsgs.length(); i++){
					int message_id = chatMsgs.getJSONObject(i).getInt("id");
					int from_user_id = chatMsgs.getJSONObject(i).getInt("from_user_id");
					int to_user_id = chatMsgs.getJSONObject(i).getInt("to_user_id");
					String message = chatMsgs.getJSONObject(i).getString("message");
					int time = chatMsgs.getJSONObject(i).getInt("time");
					String image = null;
					if (chatMsgs.getJSONObject(i).getBoolean("message_has_image")){
						image = chatMsgs.getJSONObject(i).getString("image_url");
					} 
					String msgTime = TimeCounter.convertDate(time);
					
					if (to_user_id == Profile.userId && from_user_id == user_id){
						if(!chatMsgs.getJSONObject(i).getBoolean("is_read")){
							Message msg = new Message(to, Message.Type.chat);
	        	            msg.addExtension(new ReadPacketExtension(String.valueOf(message_id)));
	        	            if (connection != null){
	        	            	if (connection.isConnected()){
	        	            		connection.sendPacket(msg); 
	        	            	}
	        	            }
	        	            Log.i("read history message send", msg.toXML());
						}
						
						messagesTemp.add(new XMPPChatModel(String.valueOf(message_id), message, image, null, 1, time, 0, null, null));
						number++;
					} else if (to_user_id == user_id && from_user_id == Profile.userId){
						int read = 0;
						if(chatMsgs.getJSONObject(i).getBoolean("is_read")){
							read = 2;
						} else {
							read = 1;
						}
						
						messagesTemp.add(new XMPPChatModel(String.valueOf(message_id), message, image, null, 0, time, read, String.valueOf(message_id), null));
						number++;
					}		
				}
				
				if (chatMsgs.length() == 25){
					
				} else {
					lastcut = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mHandler.post(new Runnable() {
        		public void run() {
        			if (lastcut){
        				rlLoadEarlierButton.setVisibility(View.GONE);
        			}
			    	pbChatLoading.setVisibility(View.GONE);
			    	
			    	Collections.sort(messagesTemp, new TimeComparator());
			    	
			    	messagesTemp.addAll(messages);
			    	messages.clear();
					messages.addAll(messagesTemp);
					
			    	updateListAdapter();
			    	
			    	preLoadImages();
			    	
			    	messagesTemp.clear();
			    	messagesTemp = null;
			    }
			});
	    }
    }
    
    public void preLoadImages() {
		// TODO Auto-generated method stub
		new loadBitmapThread().execute(0, messages.size());
	}
    
    class loadBitmapThread extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	for (int i = start; i < stop; i++){
	    		if (i < messages.size()){
	    			if (messages.get(i).imageUrl != null && messages.get(i).bitmap == null){
		    			Bitmap bitmap = ImageLoader.campusWallImageStoreAndLoad(messages.get(i).imageUrl, getApplicationContext(), ConvertDpsToPixels.getDps((DeviceDimensions.getWidth(getApplicationContext())*4)/10, getApplicationContext()));
		    			if (bitmap != null){
		    				messages.get(i).bitmap = ImageLoader.ImageCrop(bitmap);
		    			}
					}
	    		} else {
	    			break;
	    		}
	    	}
	    	
	    	mHandler.post(new Runnable() {
        		public void run() {
        			mList.invalidateViews();
				}
			});
	    	
	    	return null;
	    }
	}
    
    private class ReSetConnection extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			if (SetXMPPConnection.connection == null){
				new ReSetConnection().execute();
			} else if (SetXMPPConnection.connection.getUser() != null){
				Log.i("ReSetConnection", "yes");
				setConnection(SetXMPPConnection.connection);
			} else {
				new ReSetConnection().execute();
			}
			
			return null;
		}
    }
    
    class TimeThread extends Thread {
        // This method is called when the thread runs
    	public void run() {
	       while(true){
	    	   time = System.currentTimeMillis();
	    	   if (time >= timeActionDown + 1000 && composing){
	    		   Message msg = new Message(to, Message.Type.chat);
	    		   msg.addExtension(new PausedPacketExtension());
	    		   if (connection != null){
   	            		if (connection.isConnected()){
   	            			connection.sendPacket(msg);
   	            		}
	    		   }
	    		   composing = false;
	    		   //Log.i("composing", String.valueOf(composing));
	    	   } else if(time < timeActionDown + 1000 && !composing){
	    		   Message msg = new Message(to, Message.Type.chat);
	    		   msg.addExtension(new ComposingPacketExtension());
	    		   if (connection != null){
   	            		if (connection.isConnected()){
   	            			connection.sendPacket(msg);
   	            		}
	    		   }
	    		   composing = true;
	    		   //Log.i("composing", String.valueOf(composing));
	    	   }
	        }
    	}
    }
    
    class updateTimeCounter extends CountDownTimer {
        public updateTimeCounter(long millisInFuture, long countDownInterval) {
          super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            //dialog.dismiss();
           // Use Intent to Navigate from this activity to another
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
        	time_until_end += 200;
        	
        	time = System.currentTimeMillis();
	    	if (time >= timeActionDown + 1000 && composing){
	    		   Message msg = new Message(to, Message.Type.chat);
	    		   msg.addExtension(new PausedPacketExtension());
	    		   if (connection != null){
   	            		if (connection.isConnected()){
   	            			connection.sendPacket(msg);
   	            		}
	    		   }
	    		   composing = false;
	    		   //Log.i("composing", String.valueOf(composing));
	    	} else if(time < timeActionDown + 1000 && !composing){
	    		   Message msg = new Message(to, Message.Type.chat);
	    		   msg.addExtension(new ComposingPacketExtension());
	    		   if (msg != null){
	    			   if (connection != null){
       	            		if (connection.isConnected()){
       	            			connection.sendPacket(msg);
       	            		}
	    			   }
	    		   }
	    		   composing = true;
	    		   //Log.i("composing", String.valueOf(composing));
	    	}
        }
    }

    /**
     * Called by Settings dialog when a connection is establised with the XMPP server
     *
     * @param connection
     */
    public void setConnection(final XMPPConnection cnt) {
    	Log.i("setConnection", "yes");
    	connection = cnt;
       
        if (connection != null) {
        	 runOnUiThread(new Runnable() {
     		    public void run() {
     		    	TaskQueueImage.addTask(new getUserChatHistoryThread2(), XMPPClient.this);
     		        Log.i("getUserChatHistoryThread2", "yes");
     		    }
             });
        	 
        	 if (pl == null){
        		 addPacketListener();
        	 }
        }
    }
    
    public void addPacketListener(){
    	 // Add a packet listener to get messages sent to us
        	pl = new PacketListener() {
            public void processPacket(Packet packet) {
            	//Log.i("XMPPClient got packets", packet.getExtensions().toString());
            	Message message = (Message) packet;
            	
            	Log.i("got packets xml", message.toXML().toString());
            	if (getFromJid(message).contains(jid)){
                    if (message.getBody() != null && message.getBody().trim().length() > 0 && !message.toXML().contains("<image") && !message.toXML().contains("<received") && !message.toXML().contains("<read")) {
                    	messages.add(new XMPPChatModel(message.getPacketID(), message.getBody(), null, null, 1, getTime(), 0, null, null));
	                     
	                    // Add the incoming message to the list view
                    	mHandler.post(new Runnable() {
                    		public void run() {
                    			//setListAdapter();
                    			updateListAdapter();
                    			
                    			scrollToBottom();
                    		}
                    	});
                    	
                        Message msg = new Message(to, Message.Type.chat);
        	            msg.addExtension(new ReadPacketExtension(getUniqueID(message)));
        	            if (connection != null){
        	            	if (connection.isConnected()){
        	            		connection.sendPacket(msg); 
        	            	}
        	            }
        	            //Log.i("read message send", msg.toXML());
                    }
                    if(message.toXML().contains("<image") && !message.toXML().contains("<received") && !message.toXML().contains("<read")){
                    	String imageString = "<image>";
                    	String image_url = null;
    					image_url = message.toXML().substring(
    								message.toXML().indexOf(imageString) + imageString.length());
    					image_url = image_url.substring(0, image_url.indexOf("</image>"));
    					messages.add(new XMPPChatModel(message.getPacketID(), null, image_url, null, 1, getTime(), 0, null, null));
    					new loadImage2().execute(message.getPacketID());
    					
    					//Log.i("message image_url:", image_url);
    					scrollToBottom();
    					
                        Message msg = new Message(to, Message.Type.chat);
        	            msg.addExtension(new ReadPacketExtension(getUniqueID(message)));
        	            if (connection != null){
        	            	if (connection.isConnected()){
        	            		connection.sendPacket(msg); 
        	            	}
        	            }
        	            //Log.i("read message send", msg.toXML());
                    } 
                    if(message.toXML().contains("<received")){
                    	String unique_idString2 = "unique_id=\"";
                    	String uid2 = null;
    					if (message.toXML().contains("unique_id=\"")) {
    						uid2 = message.toXML().substring(
    								message.toXML().indexOf(unique_idString2) + unique_idString2.length());
    						uid2 = uid2.substring(0, uid2.indexOf("\""));
    						Log.i("unique_id received:", uid2);
    					}
    					setMessageReceived(uid2);
    					mHandler.post(new Runnable() {
                    		public void run() {
                    			updateListAdapter();
                    		}
                    	});
                    }
                    if(message.toXML().contains("<read")){
                    	String unique_idString = "unique_id=\"";
                    	String uid = null;
    					if (message.toXML().contains("unique_id=\"")) {
    						uid = message.toXML().substring(
    								message.toXML().indexOf(unique_idString) + unique_idString.length());
    						uid = uid.substring(0, uid.indexOf("\""));
    						Log.i("unique_id read:", uid);
    					}
    					setMessageRead(uid);
    					mHandler.post(new Runnable() {
                    		public void run() {
                    			updateListAdapter();
                    		}
                    	});
                    } 
                    if(message.toXML().contains("<composing")){
                    	//Log.i("message status:", "composing");
                    	mHandler.post(new Runnable() {
                    		public void run() {
                    			ivComposing.setVisibility(View.VISIBLE);
                    			ivComposing.setBackgroundDrawable(getResources().getDrawable(R.drawable.bubble_incoming_typing));	
                    			
                    			scrollToBottom();
                    		}
                    	});
                    } 
                    if(message.toXML().contains("<paused")){
                    	//Log.i("message status:", "paused");
                    	mHandler.post(new Runnable() {
                    		public void run() {
                    			ivComposing.setVisibility(View.GONE);
                    			ivComposing.setBackgroundDrawable(null);	
                    		}
                    	});	
                    } 
                    if(message.toXML().contains("<blocked")){
                    	String unique_idString3 = "unique_id=\"";
                    	String uid3 = null;
    					if (message.toXML().contains("unique_id=\"")) {
    						uid3 = message.toXML().substring(
    								message.toXML().indexOf(unique_idString3) + unique_idString3.length());
    						uid3 = uid3.substring(0, uid3.indexOf("\""));
    						Log.i("unique_id blocked:", uid3);
    					}
                    	//Log.i("message status:", "blocked");
                    }  
            	} 
            	/*
            	else if (getFromJid(message).contains(jid)){
            		if(message.toXML().contains("<received")){
                    	String unique_idString = "unique_id=\"";
                    	String uid = null;
    					if (message.toXML().contains("unique_id=\"")) {
    						uid = message.toXML().substring(
    								message.toXML().indexOf(unique_idString) + unique_idString.length());
    						uid = uid.substring(0, uid.indexOf("\""));
    						Log.i("unique_id received2:", uid);
    					}
    					setMessageReceived(uid);
    					mHandler.post(new Runnable() {
                    		public void run() {
                    			updateListAdapter();
                    		}
                    	});
                    }
            		if(message.toXML().contains("<read")){
                    	String unique_idString = "unique_id=\"";
                    	String uid = null;
    					if (message.toXML().contains("unique_id=\"")) {
    						uid = message.toXML().substring(
    								message.toXML().indexOf(unique_idString) + unique_idString.length());
    						uid = uid.substring(0, uid.indexOf("\""));
    						Log.i("unique_id read2:", uid);
    					}
    					setMessageRead(uid);
    					mHandler.post(new Runnable() {
                    		public void run() {
                    			updateListAdapter();
                    		}
                    	});
                    } 
            	}
            	*/
            }
        };
        	
        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
        connection.addPacketListener(pl, filter);
    }
    
    public void XMPPConnectionConnect(){
    	Log.i("re-setup xmpp connection", "------------- yes -----------------");
    	
    	String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(ns);
		
		new SetXMPPConnection(getApplicationContext(), mNotificationManager).connect();
    }
    
    public String getFromJid(Message message){
    	
    	if(message.toXML().contains("<message")){
        	String jidString = "from=\"";
        	String jID = null;
			if (message.toXML().contains("from=\"")) {
				jID = message.toXML().substring(
						message.toXML().indexOf(jidString) + jidString.length());
				jID = jID.substring(0, jID.indexOf("\""));
				//Log.i("jID:", jID);
			}
			
			return jID;
        } else {
        	return null;
        }
    }
    
    public String getToJid(Message message){
    	
    	if(message.toXML().contains("<message")){
        	String jidString = "to=\"";
        	String jID = null;
			if (message.toXML().contains("to=\"")) {
				jID = message.toXML().substring(
						message.toXML().indexOf(jidString) + jidString.length());
				jID = jID.substring(0, jID.indexOf("\""));
				//Log.i("jID:", jID);
			}
			return jID;
        } else {
        	return null;
        }
    }
    
    public String getUniqueID(Message message){
    	
    	if(message.toXML().contains("<message")){
        	String uniqueIDString = "id=\"";
        	String uniqueID = null;
			if (message.toXML().contains("id=\"")) {
				uniqueID = message.toXML().substring(
						message.toXML().indexOf(uniqueIDString) + uniqueIDString.length());
				uniqueID = uniqueID.substring(0, uniqueID.indexOf("\""));
				//Log.i("uniqueID:", uniqueID);
			}
			return uniqueID;
        } else {
        	return null;
        }
    }
    
    public void setMessageReceived(final String unique_id){    	
    	for(int i = 0; i < messages.size(); i++){
    		if (messages.get(i).unique_id != null && messages.get(i).unique_id.contentEquals(unique_id)){
    			Log.i("find it RECEIVED", messages.get(i).unique_id);
    			messages.get(i).read = 1;	
    		}
    	}   	
    }
    
    public void setMessageRead(final String unique_id){
    	for(int i = 0; i < messages.size(); i++){
    		messages.get(i).read = 2;
    		if (messages.get(i).unique_id != null && messages.get(i).unique_id.contentEquals(unique_id)){
    			Log.i("find it READ", messages.get(i).unique_id);
    			
    			break;
    		}
    	}	
    }

    private void setListAdapter() {
    	adapter = new XMPPChatArrayAdapter(XMPPClient.this, messages);
    	mList.setAdapter(adapter);
    	/*
    	mList.setOnScrollListener(new OnScrollListener(){
			
			public int first, last;
			
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		        // TODO Auto-generated method stub
		    	this.first = firstVisibleItem;
		    	this.last = firstVisibleItem + visibleItemCount - 1;
		    }
		 	
		    public void onScrollStateChanged(AbsListView view, int scrollState) {
		    	// TODO Auto-generated method stub
			   	if(scrollState == 0){
			   		if (last <= messages.size()){
				   		//free the older bitmaps in the list
				   		Thread freeBitmapThread = new freeBitmapThread(first, last);
				   		freeBitmapThread.setPriority(Thread.MIN_PRIORITY);
				   		freeBitmapThread.start();	
				    	
				   		loadBitmapThreadAgain = new loadBitmapThreadAgain();
				    	if (first > 0){
				    		loadBitmapThreadAgain.execute(first, last);
				    	} else if (first == 0){
				    		loadBitmapThreadAgain.execute(0, last);
				    	}
			   		}
			    }
			}
		});
		*/
    }
    
    private void updateListAdapter(){
    	mList.invalidateViews();
    	adapter.notifyDataSetChanged();
    }
    
    private void scrollToBottom() {
    	mList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
            	mList.setSelection(adapter.getCount() - 1);
            }
        });
    }
    
    public String getXMLValue(String tagNameStart, String tagNameStop, String xml){
    	String START_TAG = "<"+tagNameStart+">";
    	String END_TAG = "</"+tagNameStop+">";
    	
    	String value = xml.substring(START_TAG.length(), xml.indexOf(END_TAG));
    	//Log.i("regex", value);
    	return value;
    }
    
    public int getTime(){
		int now = (int) (System.currentTimeMillis()/1000);
		return now;
	}
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.i("ccc", String.valueOf(requestCode) + "/" + String.valueOf(resultCode));
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
            	Log.i("aaa", String.valueOf(SELECT_PICTURE));
                
                Uri selectedImageUri = data.getData();
                int selectedImageWidth = 0;
                int selectedImageHeight = 0;
                
                Log.i("bb", String.valueOf(selectedImageWidth));
                
                Bitmap bitmapSizeTest = CacheInternalStorage.decodeCampusWallBitmapFromInputStream(selectedImageUri, getApplicationContext(), 512);
                if (bitmapSizeTest != null) {
                	selectedImageWidth = bitmapSizeTest.getWidth();
    				selectedImageHeight = bitmapSizeTest.getHeight();
    				selectedImageHeight = 512*selectedImageHeight/selectedImageWidth;
    				selectedImageWidth = 512;
    					
    				Log.i("selectedImageWidth", String.valueOf(selectedImageWidth));
    				Log.i("selectedImageHeight", String.valueOf(selectedImageHeight));
    				
                	Bitmap photo = Bitmap.createScaledBitmap(bitmapSizeTest, selectedImageWidth, selectedImageHeight, false);             
   					
   					String encodedImage = BitMapToString(photo);
   					encodedImage = encodedImage.replace("+", "%2B");
   					//Log.i("encoded bitmap", encodedImage);
   					
   					new uploadImage(photo).execute(SaveToPNG(photo));
   				}   
            }
            
            if (requestCode == CROP_PICTURE) {
                Bundle extras = data.getExtras();  
                if (extras != null) {
                	Bitmap photo = Bitmap.createScaledBitmap((Bitmap) extras.getParcelable("data"), 512, 512, false);             
   					
   					String encodedImage = BitMapToString(photo);
   					encodedImage = encodedImage.replace("+", "%2B");
   					//Log.i("encoded bitmap", encodedImage);
   					
   					new uploadImage(photo).execute(SaveToPNG(photo));
   				} 
                //if cancelled the crop
                else { 
   					
   				}
            }
            
            if (requestCode == CAMERA_PIC_REQUEST) {
            	int selectedImageWidth = 0;
                int selectedImageHeight = 0;
    	
                if (mImageCaptureUri != null) {
                	Bitmap bitmapSizeTest = CacheInternalStorage.decodeCampusWallBitmapFromInputStream(mImageCaptureUri, getApplicationContext(), 512);
                	selectedImageWidth = bitmapSizeTest.getWidth();
    				selectedImageHeight = bitmapSizeTest.getHeight();
    				selectedImageHeight = 512*selectedImageHeight/selectedImageWidth;
    				selectedImageWidth = 512;
    				
    				Log.i("selectedImageWidth", String.valueOf(selectedImageWidth));
    				Log.i("selectedImageHeight", String.valueOf(selectedImageHeight));
    				
                	Bitmap photo = Bitmap.createScaledBitmap(bitmapSizeTest, selectedImageWidth, selectedImageHeight, false);       
   					
   					String encodedImage = BitMapToString(photo);
   					encodedImage = encodedImage.replace("+", "%2B");
   					//Log.i("encoded bitmap", encodedImage);
   					
   					new uploadImage(photo).execute(SaveToPNG(photo));
   				} 
            }    
            
        }
    }
    
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encode(b, Base64.DEFAULT, b.length);
        return temp;
    }
    
    public String SaveToPNG(Bitmap bitmap){
    	ContextWrapper cw = new ContextWrapper(XMPPClient.this);
		File directory = cw.getDir("OohlalaCache", Context.MODE_PRIVATE);
		File mypath = new File(directory, "temp.png");
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(mypath);
		    // Use the compress method on the BitMap object to write image to the OutputStream
		    if (bitmap != null){
		    	//new BitmapCompress(bitmap, fos).execute();
		    	bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		    }
		} catch (FileNotFoundException e) {
		 	e.printStackTrace();
		} 
		
		return mypath.getPath();
    }

    //UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
	
    public void startPhotoCrop(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, "image/*");  
        intent.putExtra("crop", "true");  
        // aspectX aspectY
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        // outputX outputY
        intent.putExtra("outputX", 350);  
	    intent.putExtra("outputY", 350);  
        
        intent.putExtra("return-data", true);  
        startActivityForResult(Intent.createChooser(intent, getString(R.string.Crop_Picture)), CROP_PICTURE);  
    }
    
    public class TimeComparator implements Comparator<XMPPChatModel> {
        public int compare(XMPPChatModel o1, XMPPChatModel o2) {
            return String.valueOf(o1.realtime).compareTo(String.valueOf(o2.realtime));
        }
    }
    
    private void showAlertDialog(final int userid) {
		// TODO Auto-generated method stub
    	AlertDialog alert = new AlertDialog.Builder(XMPPClient.this).create();
		alert.setTitle(getString(R.string.Block_User));
		alert.setMessage(getString(R.string.Block_all_messages_From_this_user));
		alert.setButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alert.setButton2(getString(R.string.Yes), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				RestClient result = null;
				try {
					result = new Rest.requestBody().execute(Rest.USER + userid, Rest.OSESS + Profile.sk, Rest.PUT, "1", "block", "1").get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
            	if (result.getResponseCode() == 200){
            		Intent i = new Intent(getBaseContext(), Inbox.class);
	            	startActivity(i);
            	}
			}
		});
		
		alert.show();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	//finish();
        	SetXMPPConnection.setJidCheck("");
        	onBackPressed();
        	//Log.i("back button pressed", "");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
	public void onResume() {
		super.onResume();
		SetXMPPConnection.setJidCheck(jid);
		
		if (!counter_on){
			counter.start();
		}
		
		new resumeBitmap().execute();

        if (connection != null && pl == null){
       	 	addPacketListener();
        } 
    }
    
    class resumeBitmap extends AsyncTask<Void, Void, Void> {
	    // This method is called when the thread runs
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < messages.size(); i++){
				if (messages.get(i).imageUrl != null && messages.get(i).bitmap == null){
					Bitmap bitmap = ImageLoader.campusWallImageStoreAndLoad(messages.get(i).imageUrl, getApplicationContext(), ConvertDpsToPixels.getDps((DeviceDimensions.getWidth(getApplicationContext())*4)/10, getApplicationContext()));
					if (bitmap != null){
						messages.get(i).bitmap = ImageLoader.ImageCrop(bitmap);
					}
				}
			}
			runOnUiThread(new Runnable() {
				public void run() {
					mList.invalidateViews();
				}
			});
			
			return null;
		}
	}
    
    @Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SetXMPPConnection.setJidCheck("");
		
		counter.cancel();
		counter_on = false;
		
		for (int i = 0; i < messages.size(); i++){
			if (messages.get(i).bitmap != null){
				messages.get(i).bitmap.recycle();
				messages.get(i).bitmap = null;
			}
		}
		
		if (!messagesSaved.isEmpty()){
			messagesSaved.clear();
		}
		if (!messagesCache.isEmpty()){
			messagesCache.clear();
		}

		if (connection != null) {
			connection.removePacketListener(pl);
			pl = null;
		}
	}
    
    public void getPicture(){
    	final CharSequence[] items = {getString(R.string.Upload_Picture), getString(R.string.Take_Picture), getString(R.string.Cancel)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(XMPPClient.this);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch(item){
		    	case 0:
		    		Intent intent = new Intent();
	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                startActivityForResult(Intent.createChooser(intent, getString(R.string.Select_Picture)), SELECT_PICTURE);
		    		break;
		    	case 1:
		    		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);  
		    		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
		    				"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".png"));

		    		cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		    		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  
		    		break;
		    	case 2:
		    		
		    		break;
		    	}
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    
    private class loadImage extends AsyncTask<String, Void, Void> {
    	
		@Override
		protected Void doInBackground(String... params) {
			for (int i = 0; i < messages.size(); i++){
				if (messages.get(i).message_id.contentEquals(params[0]) && messages.get(i).imageUrl != null && messages.get(i).bitmap == null){
					//Bitmap bitmap = ImageLoader.thumbImageStoreAndLoad(messages.get(i).imageUrl, getApplicationContext());
					if (photoUpload != null){
						messages.get(i).bitmap = ImageLoader.ImageCrop(photoUpload);
						mHandler.post(new Runnable() {
			        		public void run() {
								updateListAdapter();
								mSendText.setText(null);
								
								bUploadPicture.setClickable(true);
								bUploadPicture.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.input_btn_attach));
							}
						});
					}
				}
			}
			
			return null;
		}
    }
    
    private class loadImage2 extends AsyncTask<String, Void, Void> {
    	
		@Override
		protected Void doInBackground(String... params) {
			for (int i = 0; i < messages.size(); i++){
				if (messages.get(i).message_id.contentEquals(params[0]) && messages.get(i).imageUrl != null && messages.get(i).bitmap == null){
					Bitmap bitmap = ImageLoader.campusWallImageStoreAndLoad(messages.get(i).imageUrl, getApplicationContext(), ConvertDpsToPixels.getDps((DeviceDimensions.getWidth(getApplicationContext())*4)/10, getApplicationContext()));
					if (bitmap != null){
						messages.get(i).bitmap = ImageLoader.ImageCrop(bitmap);
						mHandler.post(new Runnable() {
			        		public void run() {
								updateListAdapter();
								mSendText.setText(null);
								
								bUploadPicture.setClickable(true);
								bUploadPicture.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.input_btn_attach));
							}
						});
					}
				}
			}
			
			return null;
		}
    }
    
    private class uploadImage extends AsyncTask<String, Void, Void> {
    	
    	public uploadImage(Bitmap photo){
    		photoUpload = photo;
    	}
    	
		@Override
		protected Void doInBackground(String... filepath) {
			runOnUiThread(new Runnable() {
				public void run() {
					bUploadPicture.setClickable(false);
					AnimationDrawable animationDrawable = (AnimationDrawable) getApplicationContext().getResources().getDrawable(R.drawable.loading);
					bUploadPicture.setBackgroundDrawable(animationDrawable);
					animationDrawable.start();
				}
			});
			
			RestClient result = null;
			try {
				result = new Rest.requestBody().execute(Rest.IMAGE + "?image_type=1", Rest.OSESS + Profile.sk, Rest.POST2, "1", "path", filepath[0]).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("upload picture: ", result.getResponse());
			
			String image_url = null;
			try {
				image_url = (new JSONObject(result.getResponse())).getString("image_url");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (image_url != null){
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody("-");
				msg.addExtension(new ImagePacketExtension(image_url));
	            msg.addExtension(new NamePacketExtension(Profile.displayName));
	            msg.addExtension(new UserIDPacketExtension(String.valueOf(Profile.userId)));
	            if (connection != null){
	            	if (connection.isConnected()){
	            		connection.sendPacket(msg);
	            	}
	            }
	            Log.i("image message send", msg.toXML());
	            
	            messages.add(new XMPPChatModel(msg.getPacketID(), null, image_url, null, 0, getTime(), 0, msg.getPacketID(), null));
	            new loadImage().execute(msg.getPacketID());
	
	            mHandler.post(new Runnable() {
	        		public void run() {
						updateListAdapter();
						mSendText.setText(null);
					}
				});
			}
			
			return null;
		}
    }
    
    class loadBitmapThreadAgain extends AsyncTask<Integer, Void, Void> {
	    // This method is called when the thread runs
		public int start, stop;

		protected Void doInBackground(Integer... num) {
	    	//get the all the current games
			this.start = num[0];
			this.stop = num[1];
	    	Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	
	    	
	    	if (stop <= messages.size()){
	    		
	    	} else {
	    		stop = messages.size();
	    	}
	    	
	    	for (int i = start; i < stop; i++){
	    		if (messages.get(i).imageUrl != null && messages.get(i).bitmap == null){
	    			Bitmap bitmap = ImageLoader.campusWallImageStoreAndLoad(messages.get(i).imageUrl, getApplicationContext(), ConvertDpsToPixels.getDps((DeviceDimensions.getWidth(getApplicationContext())*4)/10, getApplicationContext()));
	    			if (bitmap != null){
	    				messages.get(i).bitmap = ImageLoader.ImageCrop(bitmap);
	    				mHandler.post(new Runnable() {
	    	        		public void run() {
	    						mList.invalidateViews();
	    					}
	    	    		});
	    			}
				}
			}
	    	
	    	return null;
	    }
    }
    
    public class freeBitmapThread extends Thread {
	    // This method is called when the thread runs
		public int start, stop;
		
		public freeBitmapThread(int start, int stop){
			this.start = start;
			this.stop = stop;
		}
		
	    public void run() { 
	    	//Log.i("start stop", String.valueOf(start) + " " + String.valueOf(stop));
	    	if (start > 0){
		    	for (int i = 0; i < start; i++){
					if (messages.get(i).bitmap != null){
						messages.get(i).bitmap.recycle();
						messages.get(i).bitmap = null;
					}
				}
	    	} 
	    	if (stop < messages.size()){
		    	for (int i = stop; i < messages.size(); i++){
					if (messages.get(i).bitmap != null){
						messages.get(i).bitmap.recycle();
						messages.get(i).bitmap = null;
					}
				}
	    	} 
	   }
    }
    
    public class XMPPChatArrayAdapter extends ArrayAdapter<XMPPChatModel> {
    	private final Context context;
    	private final List<XMPPChatModel> list;
    	
    	class ViewHolder {
    		public ImageView ivImageOther;
    		public ImageView ivImageUser;
    		public ImageView ivReadImageUser;
    		public ImageView ivReadMessageUser;
    		public TextView tvTimeImageOther;
    		public TextView tvTimeImageUser;
    		public TextView tvMessageOther;
    		public TextView tvTimeMessageOther;
    		public TextView tvMessageUser;
    		public TextView tvTimeMessageUser;
    		public TextView tvDate;
    		
    	}

    	public XMPPChatArrayAdapter(Context context, List<XMPPChatModel> list) {
    		super(context, R.layout.xmppchatrow, list);
    		this.context = context;
    		this.list = list;
    	}
    	
    	@Override
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		View rowView = convertView;
    		if (rowView == null){
    			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			rowView = inflater.inflate(R.layout.xmppchatrow, null);
    			ViewHolder viewHolder = new ViewHolder();
    			viewHolder.ivImageOther = (ImageView) rowView.findViewById(R.id.ivImageOther);
    			viewHolder.ivImageUser = (ImageView) rowView.findViewById(R.id.ivImageUser);
    			viewHolder.ivReadImageUser = (ImageView) rowView.findViewById(R.id.ivReadImageUser);
    			viewHolder.ivReadMessageUser = (ImageView) rowView.findViewById(R.id.ivReadMessageUser);
    			
    			viewHolder.tvTimeImageOther = (TextView) rowView.findViewById(R.id.tvTimeImageOther);
    			viewHolder.tvTimeImageUser = (TextView) rowView.findViewById(R.id.tvTimeImageUser);
    			viewHolder.tvMessageOther = (TextView) rowView.findViewById(R.id.tvMessageOther);
    			viewHolder.tvTimeMessageOther = (TextView) rowView.findViewById(R.id.tvTimeMessageOther);
    			viewHolder.tvMessageUser = (TextView) rowView.findViewById(R.id.tvMessageUser);
    			viewHolder.tvTimeMessageUser = (TextView) rowView.findViewById(R.id.tvTimeMessageUser);
    			
    			viewHolder.tvDate = (TextView) rowView.findViewById(R.id.tvDate);
    			
    			rowView.setTag(viewHolder);
    		}
    		
    		ViewHolder holder = (ViewHolder) rowView.getTag();
    		
    		holder.ivImageOther.setImageBitmap(null);
    		holder.ivImageOther.setVisibility(View.GONE);
    		holder.ivImageUser.setImageBitmap(null);
    		holder.ivImageUser.setVisibility(View.GONE);
    		holder.ivReadImageUser.setBackgroundDrawable(null);
    		holder.ivReadMessageUser.setBackgroundDrawable(null);
    		
    		holder.tvTimeImageOther.setText(null);
    		holder.tvTimeImageUser.setText(null);
    		holder.tvMessageOther.setText(null);
    		holder.tvMessageOther.setVisibility(View.GONE);
    		holder.tvMessageOther.setMaxWidth((DeviceDimensions.getWidth(context)*7)/10);
    		holder.tvTimeMessageOther.setText(null);
    		holder.tvTimeMessageUser.setText(null);
    		holder.tvMessageUser.setText(null);
    		holder.tvMessageUser.setVisibility(View.GONE);
    		holder.tvMessageUser.setMaxWidth((DeviceDimensions.getWidth(context)*7)/10);
    		
    		holder.tvDate.setVisibility(View.GONE);
    		holder.tvDate.setText(null);
    		
    		//use TimeCounter to display the correct chat time format
    		if (position == 0){
    			holder.tvDate.setVisibility(View.VISIBLE);
    			holder.tvDate.setText(TimeCounter.getDateChat(list.get(position).realtime));
    			list.get(position).time = TimeCounter.getTimeChat(list.get(position).realtime);
    		} else {
    			if (!TimeCounter.getDateChat(list.get(position).realtime).contentEquals(TimeCounter.getDateChat(list.get(position-1).realtime))){
    				holder.tvDate.setVisibility(View.VISIBLE);
    				holder.tvDate.setText(TimeCounter.getDateChat(list.get(position).realtime));
    			}
    			
    			if (!TimeCounter.getExactTimeChat(list.get(position).realtime).contentEquals(TimeCounter.getExactTimeChat(list.get(position-1).realtime))){
    				list.get(position).time = TimeCounter.getTimeChat(list.get(position).realtime);
    			} 
    		}
    		
    		if (list.get(position).imageUrl != null){
    			//image save into the memory and cache
    			if (list.get(position).type == 0){
    				if (list.get(position).bitmap != null){
    					holder.ivImageUser.setVisibility(View.VISIBLE);
    					holder.ivImageUser.setImageBitmap(Bitmap.createScaledBitmap(list.get(position).bitmap, (DeviceDimensions.getWidth(context)*4)/10, (DeviceDimensions.getWidth(context)*4)/10, false));
    					holder.ivImageUser.setOnClickListener(new View.OnClickListener() {
    			            public void onClick(View view) {
    			            	Bundle extras = new Bundle();
    							extras.putString("image_url", list.get(position).imageUrl);
    							extras.putInt("picWidth", DeviceDimensions.getWidth(context));
    							Intent i = new Intent(context, CampusWallImage.class);
    							i.putExtras(extras);
    							context.startActivity(i);
    			            }
    			        }); 
    					
    					if (list.get(position).read == 0){
        					holder.ivReadImageUser.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bubble_outgoing_sending));
        				} else if (list.get(position).read == 1){
        					holder.ivReadImageUser.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bubble_outgoing_delivered));
        				} else if (list.get(position).read == 2){
        					holder.ivReadImageUser.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bubble_outgoing_read));
        				} 
        				
        				holder.tvTimeImageUser.setText(list.get(position).time);
    				}
    			} else {
    				if (list.get(position).bitmap != null){
    					holder.ivImageOther.setVisibility(View.VISIBLE);
    					holder.ivImageOther.setImageBitmap(Bitmap.createScaledBitmap(list.get(position).bitmap, (DeviceDimensions.getWidth(context)*4)/10, (DeviceDimensions.getWidth(context)*4)/10, false));
    					holder.ivImageOther.setOnClickListener(new View.OnClickListener() {
    			            public void onClick(View view) {
    			            	Bundle extras = new Bundle();
    							extras.putString("image_url", list.get(position).imageUrl);
    							extras.putInt("picWidth", DeviceDimensions.getWidth(context));
    							Intent i = new Intent(context, CampusWallImage.class);
    							i.putExtras(extras);
    							context.startActivity(i);
    			            }
    			        }); 
    					
    					holder.tvTimeImageOther.setText(list.get(position).time);
    				}
    			}
    		} else {
    			if (list.get(position).type == 0){
    				holder.tvMessageUser.setVisibility(View.VISIBLE);
    				holder.tvMessageUser.setText(list.get(position).msg);
    				
    				if (list.get(position).read == 0){
    					holder.ivReadMessageUser.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bubble_outgoing_sending));	
    				} else if (list.get(position).read == 1){
    					holder.ivReadMessageUser.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bubble_outgoing_delivered));	
    				} else if (list.get(position).read == 2){
    					holder.ivReadMessageUser.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bubble_outgoing_read));	
    				}
    				
    				holder.tvTimeMessageUser.setText(list.get(position).time);
    			} else {
    				holder.tvMessageOther.setVisibility(View.VISIBLE);
    				holder.tvMessageOther.setText(list.get(position).msg);
    				holder.tvTimeMessageOther.setText(list.get(position).time);
    			}
    		}
    		
    		
    		return rowView;
    	}	
    	
    }
    
}

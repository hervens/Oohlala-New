package network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetworkUtils {
	 
    public final static int NONE = 0;//������
    public final static int WIFI = 1;//Wi-Fi
    public final static int MOBILE = 2;//3G,GPRS
     
    /**
     * ��ȡ��ǰ����״̬
     * @param context
     * @returnO
     */	
    public static int getNetworkState(Context context){
    	ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         
        //�ֻ������ж�
        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if(state == State.CONNECTED||state == State.CONNECTING){
            return MOBILE;
        }
 
        //Wifi�����ж�
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(state == State.CONNECTED||state == State.CONNECTING){
            return WIFI;
        }
        return NONE;
    }
}
package datastorage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LoginState {
	static final String LOGIN_STATE_NAME = "login_state";
	private static File mDataFile;
	private static File dir;
	
	public LoginState(File dir){
		LoginState.dir = dir;
		mDataFile = new File(dir, LOGIN_STATE_NAME);
	}
	
	public static Boolean isLogIn(File dir){
		mDataFile = new File(dir, LOGIN_STATE_NAME);
		Boolean islogin = false;
		RandomAccessFile file;
		boolean exists = mDataFile.exists();
		try {
			file = new RandomAccessFile(mDataFile, "rw");
			if (exists){
				islogin = file.readBoolean();
			} else {
				writeDataFile(file, islogin);
			}
			
		} catch (Exception e){
			
		}
		
		return islogin;
	}
	
	public static void modifyLoginState(File dir, Boolean islogin){
		
		try {
			mDataFile = new File(dir, LOGIN_STATE_NAME);
			RandomAccessFile file = new RandomAccessFile(mDataFile, "rw");;
			writeDataFile(file, islogin);
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}
	
	
	private static void writeDataFile(RandomAccessFile file, Boolean islogin) throws IOException {
		file.setLength(0L);
		file.writeBoolean(islogin);
	}
	
	
	
	
}

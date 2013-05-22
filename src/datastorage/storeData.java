package datastorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class storeData {
	static final String USER_PROFILE = "user_profile";
	static final String STORE_INFO = "store_info";
	static final String LAST_REFRESH_TIME = "refreshtime";
	static final String JID = "login_j";
	static final String JPASS = "pass_j";
	private static File dir;

	public storeData(File dir) {
		storeData.dir = dir;
	}

	public static int getLastRefresh(File dir) {
		File file = new File(dir, LAST_REFRESH_TIME);
		try {
			RandomAccessFile dataFile = new RandomAccessFile(file, "rw");

			int restoreData;
			restoreData = dataFile.readInt();
			dataFile.close();
			return restoreData;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}
	
	

	public static int saveLastMessage(File dir, int lastMessageId) {
		File file = new File(dir, LAST_REFRESH_TIME);
		boolean exists = file.exists();
		RandomAccessFile dataFile;

		try {
			dataFile = new RandomAccessFile(file, "rw");
			dataFile.seek(0);
			dataFile.writeInt(lastMessageId);
			dataFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return 0;
	}
	
	public static int getLastMessageId(File dir) {
		File file = new File(dir, LAST_REFRESH_TIME);
		try {
			RandomAccessFile dataFile = new RandomAccessFile(file, "rw");

			int restoreData;
			restoreData = dataFile.readInt();
			dataFile.close();
			return restoreData;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public static int saveLastRefresh(File dir, int time) {
		File file = new File(dir, LAST_REFRESH_TIME);
		boolean exists = file.exists();
		RandomAccessFile dataFile;

		try {
			dataFile = new RandomAccessFile(file, "rw");
			dataFile.seek(0);
			dataFile.writeInt(time);
			dataFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return 0;
	}

	public static String readEmail(File dir) {
		File file = new File(dir, USER_PROFILE);
		try {
			RandomAccessFile dataFile = new RandomAccessFile(file, "rw");

			String restoreData;
			restoreData = dataFile.readUTF();
			dataFile.close();
			return restoreData;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static String readPassword(File dir) {
		File file = new File(dir, STORE_INFO);
		try {
			RandomAccessFile dataFile = new RandomAccessFile(file, "rw");
			boolean exists = file.exists();

			String restoreData;
			restoreData = dataFile.readUTF();
			dataFile.close();
			return restoreData;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static void writeEmail(File dir, String data) {
		File file = new File(dir, USER_PROFILE);
		boolean exists = file.exists();
		RandomAccessFile dataFile;

		try {
			dataFile = new RandomAccessFile(file, "rw");
			dataFile.seek(0);
			dataFile.writeUTF(data);
			dataFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void writePassword(File dir, String data) {
		File file = new File(dir, STORE_INFO);
		boolean exists = file.exists();
		RandomAccessFile dataFile;

		try {
			dataFile = new RandomAccessFile(file, "rw");
			dataFile.seek(0);
			dataFile.writeUTF(data);
			dataFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void writeJid(File dir, String data) {
		File file = new File(dir, JID);
		
		RandomAccessFile dataFile;

		try {
			dataFile = new RandomAccessFile(file, "rw");
			dataFile.seek(0);
			dataFile.writeUTF(data);
			dataFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void writeJpass(File dir, String data) {
		File file = new File(dir, JPASS);
		
		RandomAccessFile dataFile;

		try {
			dataFile = new RandomAccessFile(file, "rw");
			dataFile.seek(0);
			dataFile.writeUTF(data);
			dataFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String readJID(File dir) {
		File file = new File(dir, JID);
		try {
			RandomAccessFile dataFile = new RandomAccessFile(file, "rw");
			boolean exists = file.exists();

			String restoreData;
			restoreData = dataFile.readUTF();
			dataFile.close();
			return restoreData;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static String readJPASS(File dir) {
		File file = new File(dir, JPASS);
		try {
			RandomAccessFile dataFile = new RandomAccessFile(file, "rw");
			boolean exists = file.exists();

			String restoreData;
			restoreData = dataFile.readUTF();
			dataFile.close();
			return restoreData;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}


}

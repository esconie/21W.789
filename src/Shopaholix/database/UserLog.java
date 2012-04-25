package Shopaholix.database;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Environment;

public class UserLog {

	public static void sendToServer() throws UnknownHostException, IOException{
		Socket sock = new Socket("23.21.127.158", 4444);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));        
        String log = readLogAsString();
        out.println(log);
        out.flush();
	}
	
	private static String readLogAsString() throws java.io.IOException{
		File logFile = new File(Environment.getExternalStorageDirectory(), Backend.getBackend(null).ID + ".log");//TODO:fix this passing of null pointer
	    byte[] buffer = new byte[(int) logFile.length()];
	    BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(logFile));
	        f.read(buffer);
	    } finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    return new String(buffer);
	}
	
	public static void appendLog(String text) {
		File logFile = new File(Environment.getExternalStorageDirectory(), Backend.getBackend(null).ID + ".log");//TODO: fix this passing of null pointer
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			buf.append(text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

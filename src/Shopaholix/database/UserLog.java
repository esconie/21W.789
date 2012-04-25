package Shopaholix.database;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class UserLog extends AsyncTask<String,Void,Void> {
		
	public static void appendLog(String text) {
		new UserLog().execute("SEND_LOG "+Backend.ID +" "+text);
	}

	@Override
	protected Void doInBackground(String... arg0) {
		Socket sock;
		try {
			sock = new Socket(Backend.IP,Backend.PORT);
	        PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));        
	        out.println(arg0[0]);
	        out.flush();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

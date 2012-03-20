package Shopaholix.main;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

class DownloadFile extends AsyncTask<String, Integer, String> {
    Context context;
    

    public DownloadFile(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {
        Log.d("look","at me");
        try {
            URL url = new URL("http://web.mit.edu/21w.789/www/papers/griswold2004.pdf");
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory(),"downloadtest" + System.currentTimeMillis() + ".txt"))));
            long startTime = System.currentTimeMillis();
            long lastTime = startTime;
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            // mobile
            State mobile = conMan.getNetworkInfo(0).getState();

            // wifi
            State wifi = conMan.getNetworkInfo(1).getState();
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                out.println("started download: " + tm.getNetworkType());
            } else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                out.println("started download: wifi");
            }
            URLConnection ucon = url.openConnection();
            ucon.connect();
            int lengthOfFile = ucon.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream());

            byte data[] = new byte[1024];

            long total = 0;
            long lastTotal = total;

            while (input.read(data) != -1) {
                if (total == 0) {
                    out.println("latency:" + (System.currentTimeMillis() - startTime) + "ms");
                }
                total += 1;
                if (System.currentTimeMillis() - lastTime > 10000) {
                    out.println("throughput: " + total * 1000 / (System.currentTimeMillis() - lastTime) + " KB/s");
                    lastTime = System.currentTimeMillis();
                }
            }
            out.println("Average throughput: " + lengthOfFile / (System.currentTimeMillis() - startTime) + " KB/s");
            input.close();
            out.flush();
            out.close();
            publishProgress();
            Thread.sleep(100);
            Log.d("look","at me again");
        }

        catch (Exception e) {
        }
        
        return null;
        
    }
}
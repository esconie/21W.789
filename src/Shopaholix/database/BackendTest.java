package Shopaholix.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class BackendTest extends AsyncTask<String, String, String> {
	Context context;
	public BackendTest(Context context) {
		this.context=context;
		
	}
	@Override
	protected String doInBackground(String... params) {
		Backend backend=new Backend(context);
		Log.d("BackendTest","hi");
		backend.getItem("0000000000");
		return null;
		
	}


}

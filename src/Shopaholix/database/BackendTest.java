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
		String upc="037000188421";
		Backend backend=new Backend(context);
		Log.d("BackendTest","starting backend test");
		Item item=UPCDatabase.lookupByUPC(upc);
		Log.d("BackendTest",item.toString());
		Log.d("BackendTest","finished backend test");
		return null;
		
	}


}

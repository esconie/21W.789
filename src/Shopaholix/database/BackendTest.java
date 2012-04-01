package Shopaholix.database;

import java.util.ArrayList;

import Shopaholix.database.ItemRatings.Rating;
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
		String upc2="037000230113";
		User aaki=new User("aaki");
		Backend backend=new Backend(context);
		Log.d("BackendTest","starting backend test");
		Item item=backend.getItem(upc);
		Log.d("BackendTest",item.toString());
		backend.rateItem(upc, Rating.GOOD);
		Log.d("BackendTest",item.toString());
		backend.addFamilyMember(aaki);
		Log.d("BackendTest",backend.getFamilyMembers().toString());
		Log.d("BackendTest",backend.getItem(upc).ratings.get(aaki).toString());
		backend.rateFamilyItem(upc, aaki, Rating.BAD);
		Log.d("BackendTest",item.toString());
		Log.d("BackendTest",item.tags.toString());
		backend.getItem(upc2);
		Log.d("BackendTest",backend.getSuggestedItems(new ArrayList<Tag>()).toString());

		Log.d("BackendTest","finished backend test");
		return null;
		
	}


}

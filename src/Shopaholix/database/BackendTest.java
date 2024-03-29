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
		Log.d("BackendTest","started backend test");
		Backend backend=Backend.getBackend(this.context);
		ArrayList<Tag> requiredTags=new ArrayList<Tag>();
		Log.d("BackendTest",backend.getSuggestedItems("").toString());
		Log.d("BackendTest",backend.getSuggestedTags("").toString());
		Log.d("BackendTest",backend.getSuggestedItems("pot").toString());
		Log.d("BackendTest",backend.getSuggestedTags("sta").toString());
		
		Log.d("BackendTest",backend.getSuggestedItems("potato").toString());
		Log.d("BackendTest",backend.getSuggestedTags("potato").toString());

		Log.d("BackendTest",backend.getSuggestedTags("stack").toString());
		Log.d("BackendTest",backend.getSuggestedTags("stack ").toString());
		Log.d("BackendTest",backend.getSuggestedItems("stack").toString());
		Log.d("BackendTest",backend.getSuggestedItems("   stack    ").toString());
		Log.d("BackendTest",backend.getSuggestedItems("potato  prInGL").toString());
		
		
		
		
//		Log.d("BackendTest","starting backend test");
//		Log.d("BackendTest",item.toString());
//		backend.rateItem(upc, Rating.GOOD);
//		Log.d("BackendTest",item.toString());
//		backend.addFamilyMember(aaki);
//		Log.d("BackendTest",backend.getFamilyMembers().toString());
//		Log.d("BackendTest",backend.getItem(upc).ratings.get(aaki).toString());
//		backend.rateFamilyItem(upc, aaki, Rating.BAD);
//		Log.d("BackendTest",item.toString());
//		Log.d("BackendTest",item.tags.toString());
//		backend.getItem(upc2);
//		Log.d("BackendTest",backend.getSuggestedItems(new ArrayList<Tag>()).toString());
//		Log.d("BackendTest",backend.getSuggestedTags(new ArrayList<Tag>()).toString());
		Log.d("BackendTest","finished backend test");
		return null;
		
	}


}

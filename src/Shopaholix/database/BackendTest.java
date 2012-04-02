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
		Backend backend=new Backend();
		String[] upcs={"037000188421","037000230113","037000188438","037000185055","037000185208"};
		for(String upc:upcs){
			backend.getItem(upc);
		}
		User haoyi=new User("Haoyi");
		backend.addFamilyMember(haoyi);
		backend.rateItem(upcs[0], Rating.GOOD);
		backend.rateItem(upcs[1], Rating.GOOD);
		backend.rateItem(upcs[3], Rating.NEUTRAL);
		backend.rateItem(upcs[4], Rating.BAD);
		backend.rateFamilyItem(upcs[0], haoyi, Rating.GOOD);
		backend.rateFamilyItem(upcs[1],haoyi,Rating.BAD);
		Log.d("BackendTest",backend.getItem(upcs[0]).toString());
		Log.d("BackendTest",backend.getFamilyMembers().toString());
		ArrayList<Tag> requiredTags=new ArrayList<Tag>();
		Log.d("BackendTest",backend.getSuggestedItems(requiredTags,new Tag(""),1).toString());
		Log.d("BackendTest",backend.getSuggestedTags(requiredTags,new Tag("")).toString());
		Log.d("BackendTest",backend.getSuggestedItems(requiredTags,new Tag("pot")).toString());
		Log.d("BackendTest",backend.getSuggestedTags(requiredTags,new Tag("sta")).toString());
		
		requiredTags.add(new Tag("potato"));
		Log.d("BackendTest",backend.getSuggestedItems(requiredTags,new Tag("")).toString());
		Log.d("BackendTest",backend.getSuggestedTags(requiredTags,new Tag("")).toString());
		requiredTags.add(new Tag("original"));
		Log.d("BackendTest",backend.getSuggestedItems(requiredTags,new Tag("")).toString());
		Log.d("BackendTest",backend.getSuggestedTags(requiredTags,new Tag("")).toString());
		
		
		
		
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

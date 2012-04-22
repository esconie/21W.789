package Shopaholix.database;

import java.util.HashMap;

import android.util.Log;

import Shopaholix.database.ItemRatings.Rating;

public class ItemRatings extends HashMap<User,Rating> {
	public enum Rating {
		UNRATED, BAD, NEUTRAL, GOOD
	}
	public Rating get(User user){
		Log.d("ooo",user.toString());
		Rating r = null;
		if(containsKey(user)){
			r = super.get(user);
		}
		if (r!=null) {
			Log.d("ooo",r.toString());
			return r;
		}
		Log.d("ooo",Rating.UNRATED.toString());
		return Rating.UNRATED;
	}
}

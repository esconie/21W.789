package Shopaholix.database;

import java.io.Serializable;
import java.util.HashMap;

import android.util.Log;

import Shopaholix.database.ItemRatings.Rating;

public class TagRatings extends HashMap<User,Rating> implements Serializable {
	public Rating get(User user){
		Log.d("ooo",user.toString());
		Shopaholix.database.ItemRatings.Rating r = null;
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

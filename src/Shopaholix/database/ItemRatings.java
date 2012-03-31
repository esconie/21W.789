package Shopaholix.database;

import java.util.HashMap;

import Shopaholix.database.ItemRatings.Rating;

public class ItemRatings extends HashMap<User,Rating> {
	public enum Rating {
		UNRATED, BAD, NEUTRAL, GOOD
	}
}

package Shopaholix.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import Shopaholix.database.ItemRatings.Rating;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Use ServerConnect to get updates from the server and ServerUpdate to push updates to the server
 * When serializing execute:
 * new ServerUpdate().execute(updates);
 * When deserializing execute:
 * new ServerConnect.execute(lastTime);
 */

public class Backend {
	public static Backend backend = new Backend();
	HashMap<String, Item> items = new HashMap<String, Item>();
	private User me;
	private HashMap<Integer, User> users;
	private HashSet<Tag> allTags;
	private ArrayList<String> updates = new ArrayList<String>();
	
	private Integer ID;
	Long lastTime;

	public Backend() {
		lastTime = (long) 0;
		//execute this statement whenever the backend is deserialized
		new ServerConnect().execute(lastTime);
		
		me = new User("Personal");
		users = new HashMap<Integer,User>();
		allTags = new HashSet<Tag>();
		users.put(0,me);
		String[] upcs = { "037000188421", "037000230113", "037000188438",
				"037000185055", "037000185208" };
		for (String upc : upcs) {
			getItem(upc);
		}
		User haoyi = new User("Haoyi");
		addFamilyMember(haoyi,1);
		rateItem(upcs[0], Rating.GOOD);
		rateItem(upcs[1], Rating.GOOD);
		rateItem(upcs[3], Rating.NEUTRAL);
		rateItem(upcs[4], Rating.BAD);
		rateFamilyItem(upcs[0], haoyi, Rating.GOOD);
		rateFamilyItem(upcs[1], haoyi, Rating.BAD);
	}

	public ArrayList<Item> getSuggestedItems(String s) {
		return getSuggestedItems(s, 5);
	}

	private ArrayList<Item> getSuggestedItems(String s, int numberOfResults) {
		return getSuggestedItems(parseFullTags(s), parsePartialTag(s),
				numberOfResults);
	}

	public ArrayList<Item> getSuggestedItems(ArrayList<Tag> tags, Tag partial,
			int numberOfResults) {
		Collection<Item> items = this.items.values();
		PriorityQueue<Tuple<Item, Integer>> bestItems = new PriorityQueue<Tuple<Item, Integer>>();
		for (Item item : items) {
			if (item.satisfies(tags, partial)) {
				int score = scoreItem(item);
				bestItems.add(new Tuple<Item, Integer>(item, score));
			}
		}
		ArrayList<Item> suggestedItems = new ArrayList<Item>();
		while (numberOfResults > 0 && !bestItems.isEmpty()) {
			suggestedItems.add(bestItems.poll().a);
			numberOfResults--;
		}
		return suggestedItems;
	}

	private int scoreItem(Item item) {
		int score = 0;
		for (User user : users.values()) {
			int weight = 1;
			if (user.equals(me)) {
				weight = 5;
			}
			Rating rating = item.ratings.get(user);
			if (rating == Rating.BAD) {
				score -= weight;
			} else if (rating == Rating.GOOD) {
				score += weight;
			}
		}
		return -score;

	}

	public ArrayList<Tag> getSuggestedTags(String s) {
		return getSuggestedTags(s, 5);
	}

	private ArrayList<Tag> getSuggestedTags(String s, int numberOfResults) {

		return getSuggestedTags(parseFullTags(s), parsePartialTag(s),
				numberOfResults);
	}

	private Tag parsePartialTag(String s) {
		if (s.equals("") || s.charAt(s.length() - 1) == ' ') {
			return new Tag("");
		}
		return new Tag(s.substring(s.lastIndexOf(' ') + 1));
	}

	private ArrayList<Tag> parseFullTags(String s) {
		ArrayList<Tag> full = new ArrayList<Tag>();
		if (-1 == s.lastIndexOf(' '))
			return full;
		s = s.substring(0, s.lastIndexOf(' '));
		String[] tokens = s.split(" ");
		for (String name : tokens) {
			if (!name.equals(""))
				full.add(new Tag(name));
		}
		return full;
	}

	public ArrayList<Tag> getSuggestedTags(ArrayList<Tag> requiredTags,
			Tag partial, int numberOfResults) {
		PriorityQueue<Tuple<Tag, Integer>> bestTags = new PriorityQueue<Tuple<Tag, Integer>>();
		for (Tag tag : allTags) {
			int score = scoreTag(tag, requiredTags, partial);
			bestTags.add(new Tuple<Tag, Integer>(tag, score));
		}
		ArrayList<Tag> suggestedTags = new ArrayList<Tag>();
		while (numberOfResults > 0 && !bestTags.isEmpty()) {
			Tuple<Tag, Integer> temp = bestTags.poll();
			if (temp.b > 1000) {
				break;
			}
			suggestedTags.add(temp.a);
			numberOfResults--;
		}
		return suggestedTags;
	}

	private int scoreTag(Tag tag, ArrayList<Tag> tags, Tag partial) {
		HashMap<User, HashMap<Rating, Integer>> aggregate = new HashMap<User, HashMap<Rating, Integer>>();
		boolean satisfied = false;
		int score = 0;
		if (tags.contains(tag)) {
			score -= 10000;
		}
		if (!tag.satisfies(partial)) {
			score -= 10000;
		}
		for (User user : users.values()) {
			HashMap<Rating, Integer> temp = new HashMap<Rating, Integer>();
			for (Rating rating : Rating.values()) {
				temp.put(rating, 0);
			}
			aggregate.put(user, temp);
		}
		for (Item item : items.values()) {
			if (item.satisfies(tags, tag, partial)) {
				for (User user : users.values()) {
					HashMap<Rating, Integer> temp = aggregate.get(user);
					Rating rating = item.ratings.get(user);
					temp.put(rating, temp.get(rating) + 1);
				}
				score += 2;
				satisfied = true;
			}
		}

		TagRatings tagRating = new TagRatings();
		for (User user : users.values()) {
			int weight = 1;
			if (user.equals(me)) {
				weight = 5;
			}
			HashMap<Rating, Integer> temp = aggregate.get(user);
			int numberOfRatings = temp.get(Rating.GOOD)
					+ temp.get(Rating.NEUTRAL) + temp.get(Rating.BAD);

			if (temp.get(Rating.GOOD) > .5 * numberOfRatings) {
				tagRating.put(user, Rating.GOOD);
			} else if (temp.get(Rating.BAD) > numberOfRatings * .5) {
				tagRating.put(user, Rating.BAD);
			} else if (numberOfRatings == 0) {
				tagRating.put(user, Rating.UNRATED);
			} else {
				tagRating.put(user, Rating.NEUTRAL);
			}
			score += weight * temp.get(Rating.GOOD) - weight
					* temp.get(Rating.BAD);
		}

		if (!satisfied) {
			score -= 10000;
		}// so tags that don't have any items satisfied by them aren't displayed
		tag.ratings = tagRating;
		return -score;
	}

	public Item getItem(String upc) {
		if (items.containsKey(upc)) {
			return items.get(upc);
		} else {
			Item item = UPCDatabase.lookupByUPC(upc);

			items.put(upc, item);
			for (Tag tag : item.tags) {
				allTags.add(tag);
			}
			return item;
		}
	}

	public void rateItem(String UPC, Rating rating) {
		String updateString = "RATING_UPDATE "+UPC+" "+ID+" "+rating.toString()+" "+new Date().getTime();
		updates.add(updateString);
		items.get(UPC).ratings.put(me, rating);
	}

	public void rateFamilyItem(String UPC, User user, Rating rating) {
		items.get(UPC).ratings.put(user, rating);
	}

	public void addFamilyMember(User user, Integer id) {
		String updateString = "MEMBER_UPDATE "+0+" "+ID+" true "+new Date().getTime();
		updates.add(updateString);
		
		users.put(id,user);
	}

	public void removeFamilyMember(Integer userID) {
		String updateString = "MEMBER_UPDATE "+0+" "+ID+" false "+new Date().getTime();
		updates.add(updateString);
		users.remove(userID);
	}

	public HashSet<User> getFamilyMembers() {
		return new HashSet<User>(users.values());
	}

	public class Tuple<A, B extends Comparable<B>> implements
			Comparable<Tuple<A, B>> {
		A a;
		B b;

		public Tuple(A a, B b) {
			this.a = a;
			this.b = b;
		}

		public int compareTo(Tuple<A, B> arg0) {
			return b.compareTo(arg0.b);
		}

	}
	
	private class ServerConnect extends AsyncTask<Long,Void,ArrayList<String>>{

		@Override
		protected ArrayList<String> doInBackground(Long... arg0) {
			String input = "";
			if (arg0[0] == 0) {
				input = "NEWUSER";
			} else {
				input = "GET_UPDATE "+ID+ " "+arg0[0];
			}
			input +="\n";			
			try {
				Socket sock = new Socket("localhost", 789);
		        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		        PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));        
		        //SEND REQUEST AND PRINT RESPONSE
		        while(true){
			        out.println(input);
			        out.flush();
			        String thisLine = "";
			        ArrayList<String> response = new ArrayList<String>();
			        while(true){
			        	thisLine = in.readLine();
			        	if(thisLine == null || thisLine.trim().length() <= 0)
			        		break;
			        	response.add(thisLine);
			        }
			        System.out.println(response);
			        return response;
		        }
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			String id = "([1-9][0-9]{7}|[0-9])";
			String upc = "([0-9]+)";
			String getUpdate = "(GET_UPDATE " + id + " [0-9]+)";
			String memberUpdate = "(MEMBER_UPDATE " + id + " " + id + " (true|false)) [0-9]+";
			String ratingUpdate = "(RATING_UPDATE " + upc + " " + id + " (GOOD|BAD|NEUTRAL) [0-9]+)";

			for (String update: result) {
				if (update.matches(id)) {
					ID = Integer.parseInt(update);
				} else if (update.matches(memberUpdate)) {
					String[] args = update.split(" ");
					Integer newID = Integer.parseInt(args[2]);
					if (newID != ID) {
						Boolean add = Boolean.parseBoolean(args[3]);
						if (add) {
							users.put(newID, new User("server"));
						}
						else {
							users.remove(newID);
						}
						lastTime = Long.parseLong(args[4]);
					}					
				} else if (update.matches(ratingUpdate)) {
					String[] args = update.split(" ");
					Integer newID = Integer.parseInt(args[2]);
					if (newID == ID) {
						rateItem(args[1], Rating.valueOf(args[2]));
					} else {
						User user = users.get(newID);
						if (user != null) {
							rateFamilyItem(args[1], user, Rating.valueOf(args[2]));
						}
					}
					lastTime = Long.parseLong(args[4]);
					
				}
			}
		} 
	}

	private class ServerUpdate extends AsyncTask<ArrayList<String>,Void,Void>{

		@Override
		protected Void doInBackground(ArrayList<String>... arg0) {			
			try {
				Socket sock = new Socket("localhost", 789);
		        PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));        
		        //SEND REQUEST AND PRINT RESPONSE
		        String zeroID = "(RATING_UPDATE [0-9]+ 0 (GOOD|BAD|NEUTRAL) [0-9]+)";
		        for (String update: arg0[0]) {
		        	if (update.matches(zeroID)) {
		        		String[] args = update.split(" ");
		        		update = args[0]+" "+args[1]+" "+ID+" "+args[3] + " "+args[4];
		        	}
		        	out.println(update);
				}
		        out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
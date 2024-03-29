package Shopaholix.database;

import java.io.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import Shopaholix.database.ItemRatings.Rating;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * Use ServerConnect to get updates from the server and ServerUpdate to push updates to the server
 * When serializing execute:
 * new ServerUpdate().execute(updates);
 * When deserializing execute:
 * new ServerConnect.execute(lastTime);
 */

public class Backend implements Serializable {
	private static final long serialVersionUID = -1724259363406356978L;
	private static boolean backendLoaded = false;
	private static Backend backend = new Backend();
	public HashMap<String, Item> items = new HashMap<String, Item>();
	public User me;
	public HashMap<String, User> users;
	public HashSet<Tag> allTags;
	public static final String IP = "23.21.127.158";
	public static final int PORT = 4444;
	public static final ReentrantLock serverLock = new ReentrantLock();
	
	public static String ID;
	Long lastTime;

	public Backend() {
		lastTime = (long) 0;
		//execute this statement whenever the backend is deserialized
		new ServerConnect().execute(lastTime);
		
		me = new User("Personal");
		users = new HashMap<String,User>();
		users.put("Personal", me);
		allTags = new HashSet<Tag>();
		
		new Thread(new Runnable(){
			public void run(){
				String[] upcs = { "037000188421", "037000230113", "037000188438",
						"037000185055", "037000185208" };
				for (String upc : upcs) {
					getItem(upc);
				}
				rateItem(upcs[0], Rating.GOOD);
				rateItem(upcs[1], Rating.GOOD);
				rateItem(upcs[3], Rating.NEUTRAL);
				rateItem(upcs[4], Rating.BAD);				
			}
		}).start();
		
	}

	public ArrayList<Item> getSuggestedItems(String s) {
		return getSuggestedItems(s, 5);
	}
	
	
	public void setContext(Context c) {

		try{
			AccountManager accountManager = AccountManager.get(c);
	
		    Account[] accounts =
		    accountManager.getAccountsByType("com.google");
			users.put(accounts[0].name,me);		
			ID = accounts[0].name;

		}catch(Exception e){

			String id = "id@" + Math.abs(new Random().nextInt());
			users.put(id,me);		

			ID = id;
		}

	}
	
	public static Backend getBackend(Context c) {
		if (backendLoaded) {
			return backend;
		} else {
			backend = readBackend(c);
			backendLoaded = true;
			return backend;
		}
	}
	
	private static Backend readBackend(Context c) {
		try {
			File f;
			if ((f = new File(Environment.getExternalStorageDirectory(),"/Shopaholix/shop.bak")).isFile()) {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
				backend = (Backend) in.readObject();
				backend.setContext(c);
				backend.new ServerConnect().execute(backend.lastTime);
				return backend;
			}
			throw new Exception();
		} catch (Exception e) {
			backend = new Backend();
			backend.setContext(c);
			return backend;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void writeBackend(Context c) throws IOException {
        new File(Environment.getExternalStorageDirectory(),"/Shopaholix/").mkdir();
        FileOutputStream file = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "/Shopaholix/shop.bak"));
        ObjectOutputStream output = new ObjectOutputStream(file);
        output.writeObject(backend);
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
			if(item==null) return null;
			items.put(upc, item);
			for (Tag tag : item.tags) {
				allTags.add(tag);
			}
			return item;
		}
	}

	public void rateItem(String UPC, Rating rating) {
		String updateString = "RATING_UPDATE "+UPC+" "+ID+" "+rating.toString()+" "+new Date().getTime();
		items.get(UPC).ratings.put(me, rating);
		Log.d("update",updateString);
		new ServerUpdate().execute(updateString);
		new ServerConnect().execute(lastTime);
	}

	public void rateFamilyItem(String UPC, User user, Rating rating) {
		if (items.containsKey(UPC)) {
			items.get(UPC).ratings.put(user, rating);
		} else {
			items.put(UPC, UPCDatabase.lookupByUPC(UPC));
			items.get(UPC).ratings.put(user, rating);
		}
	}

	public void addFamilyMember(User user, String id) {
		String updateString = "MEMBER_UPDATE "+ID+" "+id+" true "+new Date().getTime();
		users.put(id,user);
		new ServerUpdate().execute(updateString);
		new ServerConnect().execute(lastTime);

	}

	public void removeFamilyMember(String userID) {
		String updateString = "MEMBER_UPDATE "+ID+" "+userID+" false "+new Date().getTime();
		users.remove(userID);
		new ServerUpdate().execute(updateString);
		new ServerConnect().execute(lastTime);
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
			Log.d("cows","fetching start");
			Log.d("cows",arg0[0].toString());
			String input = "";
			// arg0[0] is the time 
			if (arg0[0] == 0) {
				input = "NEWUSER "+ID;
			} else {
				input = "GET_UPDATE "+ID+ " "+arg0[0];
				if(ID != null)
					Log.d("cows",ID);
			}
			input +="\n";			
			try {
		        //SEND REQUEST AND PRINT RESPONSE
		        ArrayList<String> response = new ArrayList<String>();
		        synchronized(serverLock) {
					Socket sock = new Socket(IP, 4444);
			        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			        PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));        

		        	out.println(input);
			        out.flush();
			        String thisLine = "";
			        while(true){
			        	thisLine = in.readLine();
			        	if(thisLine == null || thisLine.trim().length() <= 0)
			        		break;
			        	response.add(thisLine);
			        }
		        }
				return response;
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			if (result==null) {
				return;
			}
			String id = ".+@.+";
			String upc = "([0-9]+)";
			String getUpdate = "(GET_UPDATE " + id + " [0-9]+)";
			String memberUpdate = "(MEMBER_UPDATE " + id + " " + id + " (true|false)) [0-9]+";
			String ratingUpdate = "(RATING_UPDATE " + upc + " " + id + " (GOOD|BAD|NEUTRAL) [0-9]+)";

			for (String update: result) {
				if (update.matches(memberUpdate)) {
					String[] args = update.split(" ");
					String newID = args[2];
					if (!newID.equals(ID)) {
						Boolean add = Boolean.parseBoolean(args[3]);
						if (add) {
							if (!users.containsKey(newID)) {
								users.put(newID, new User(newID));
								Log.d("adding user", newID);
							}
						}
						else {
							users.remove(newID);
						}
						lastTime = Long.parseLong(args[4])+1;
					}					
				} else if (update.matches(ratingUpdate)) {
					String[] args = update.split(" ");
					String newID = args[2];
					if (newID.equals(ID)) {
						rateItem(args[1], Rating.valueOf(args[3]));
					} else {
						User user = users.get(newID);
						if (user != null) {
							rateFamilyItem(args[1], user, Rating.valueOf(args[3]));
						}
					}
					lastTime = Long.parseLong(args[4]) +1;
				} else if (update.matches(id)) {
					ID = update;
				}
			}
			if (lastTime == 0) {
				lastTime = (long) 1;
			}
		} 
	}

	private class ServerUpdate extends AsyncTask<String,Void,Void>{

		@Override
		protected Void doInBackground(String... arg0) {
			try {
		        //SEND REQUEST
		        synchronized (serverLock) {
		        	Log.d("cow",arg0[0]);
					Socket sock = new Socket(IP, 4444);
			        PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));        

		        	out.println(arg0[0]);
		        	out.flush();
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}

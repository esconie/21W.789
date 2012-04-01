package Shopaholix.database;

public class User {

	public String name;
	
	public User(String name){
		this.name=name;
	}
	public String toString(){
		return name;
	}
	public boolean equals(Object o){
		return ((User)o).name.equals(name);
		
	}
	public int hashCode(){
		return name.hashCode();
	}

}

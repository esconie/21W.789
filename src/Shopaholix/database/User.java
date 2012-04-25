package Shopaholix.database;

import java.io.Serializable;

public class User implements Serializable{

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

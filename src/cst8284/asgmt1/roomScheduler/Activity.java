package cst8284.asgmt1.roomScheduler;
/*  Course Name: CST8284
    Author: Prof. Dave Houtman
    Class name: Activity.java
    Date: February 11, 2020
*/ 

public class Activity {
	private String category, description;
	
	public Activity(String category, String description) {
		setCategory(category); setDescription(description);	
	}
	
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	
	public String getCategory() {return category;}
	public void setCategory(String category) {this.category = category;}
	
	@Override
	public String toString() {
		return  "Event: " + getCategory() + "\n" + 
			((getDescription()!="")?"Description: " + getDescription():"") + "\n";
	}
}

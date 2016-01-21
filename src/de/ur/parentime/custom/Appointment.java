package de.ur.parentime.custom;

/*
 * This class is a custom Object to handle the Appointments
 */

public class Appointment {
	
	 	private String time;
	    private String teacher;
	    private String tFirstName;
	    private String tLastName;
		private String pLastName;
		private String pFirstName;
	    private String parent;
	    private String id;
	    private int slot;
	    private String teacherUsername;
	
//	Returns the time
	public String getTime(){
		return time;
	}
	
//	Sets the time
	public void setTime(String time){
		this.time = time;
	}
	
//	Sets the slot
	public void setSlot(int slot){
		this.slot = slot;
	}
//	Returns the Slot
	public int getSlot(){
		return slot;
	}
	
//	Returns the ID
	public String getId(){
		return id;
	}
	
//	Sets the ID
	public void setId(String id){
		this.id = id;
	}
	
//	Returns the Teachers First Name
	public String getTeacherFirstName(){
		return tFirstName;
	}
	
//	Sets the Teachers First Name
	public void setTeacherFirstName(String tFirstName){
		this.tFirstName = tFirstName;
	}
	
//	Returns the Teachers Last Name
	public String getTeacherLastName(){
		return tLastName;
	}
	
//	Sets the Teachers Last Name
	public void setTeacherLastName(String tLastName){
		this.tLastName = tLastName;
	}
	
//	Returns the Parents First Name
	public String getParentFirstName(){
		return pFirstName;
	}
	
//	Sets the Parents First Name
	public void setParentFirstName(String pFirstName){
		this.pFirstName = pFirstName;
	}
	
//	Returns the Parents Last Name
	public String getParentLastName(){
		return pLastName;
	}
	
//	Sets the Parents Last Name
	public void setParentLastName(String pLastName){
		this.pLastName = pLastName;
	}

//	Returns the teacher
	public String getTeacher(){
		return teacher;
	}

//	Sets the teacher
	public void setTeacher(String teacher){
		this.teacher = teacher;
	}
	
//	Returns the Teachers Username
	public String getTeacherUsername(){
		return teacherUsername;
	}
	
//	Sets the Teachers Username
	public void setTeacherUsername(String teacherUsername){
		this.teacherUsername = teacherUsername;
	}
	
//	Returns the Parent
	public String getParent(){
		return parent;
	}
	
//	Sets the Parent
	public void setParent(String parent){
		this.parent = parent;
	}

}

package de.ur.parentime.custom;

/*
 * Custom Object handling the Times
 */

public class Time {
	
	 	private String time;
	    private String teacher;
	    private String parent;
	    private String id;
	    private int slot;
	    private String teacherUsername;
	
//	    Returns the time
		public String getTime(){
			return time;
		}
		
//		Sets the time
		public void setTime(String time){
			this.time = time;
		}
		
//		sets the Slot
		public void setSlot(int i){
			this.slot = i;
		}
		
//		Gets the Slot
		public int getSlot(){
			return slot;
		}
		
//		Returns the ID
		public String getId(){
			return id;
		}
		
//		Sets the ID
		public void setId(String id){
			this.id = id;
		}
		
//		Returns the Teacher
		public String getTeacher(){
			return teacher;
		}

//		Sets the Teacher
		public void setTeacher(String teacher){
			this.teacher = teacher;
		}
		
//		Returns the Teacher Username
		public String getTeacherUsername(){
			return teacherUsername;
		}
	
//		Sets the teacher Username
		public void setTeacherUsername(String teacherUsername){
			this.teacherUsername = teacherUsername;
		}
		
//		Returns the Parent Object
		public String getParent() {
			return parent;
		}
		
//		Sets the parent
		public void setParent(String parent){
			this.parent = parent;
		}
}

package cst8284.asgmt1.roomScheduler;
/*  Course Name: CST8284
    Author: Prof. Dave Houtman
    Class name: RoomScheduler.java
    Date: February 11, 2020
*/ 

import java.util.Scanner;
import java.util.Calendar;

public class RoomScheduler {
	
	private static Scanner scan = new Scanner(System.in);
	private RoomBooking[] roomBookings = new RoomBooking[80];
	private int lastBookingIndex = 0;  
	
	private static final int ENTER_ROOM_BOOKING = 1, DISPLAY_BOOKING = 2,
							 DISPLAY_DAY_BOOKINGS = 3, EXIT = 0;
	
	public RoomScheduler() {}
	
	public void launch() {
		int choice = 0;
		do {
		   choice = displayMenu();
		   executeMenuItem(choice);
		} while (choice != EXIT);		
	}
	
	private int displayMenu() {
		System.out.println("Enter a selection from the following menu:");
		System.out.println(
			ENTER_ROOM_BOOKING + ". Enter a room booking\n" +
			DISPLAY_BOOKING  + ". Display a booking\n" +
			DISPLAY_DAY_BOOKINGS + ". Display room bookings for the whole day\n" +
			EXIT + ". Exit program");
		int ch = scan.nextInt();
		scan.nextLine();  // 'eat' the next line in the buffer
		System.out.println(); // add a space before next menu output
		return ch;
	}
	
	private void executeMenuItem(int choice) {
		switch (choice) {
			case ENTER_ROOM_BOOKING: 
				saveRoomBooking(makeBookingFromUserInput()); 
				break;
			case DISPLAY_BOOKING: 
				displayBooking(makeCalendarFromUserInput(null, true));
				break;
			case DISPLAY_DAY_BOOKINGS: 
				displayDayBookings(makeCalendarFromUserInput(null, false)); 
				break;
			case EXIT: 
				System.out.println("Exiting Room Booking Application\n\n"); 
				break;
			default: System.out.println("Invalid choice: try again. (Select " + EXIT + " to exit.)\n");
		}
		System.out.println();  // add blank line after each output
	}
	
    private static String getResponseTo(String s) {
    	System.out.print(s);
		return(scan.nextLine());
    }
	
    private static RoomBooking makeBookingFromUserInput() {
    	String[] fullName = getResponseTo("Enter Client Name (as FirstName LastName): ").split(" ");
 		String phoneNumber = getResponseTo("Phone Number (e.g. 613-555-1212): ");
		String organization = getResponseTo("Organization (optional): ");
		String category = getResponseTo("Enter event category: ");
		String description = getResponseTo("Enter detailed description of event: ");
		Calendar startCal = makeCalendarFromUserInput(null, true);
		Calendar endCal = makeCalendarFromUserInput(startCal, true);
		
		ContactInfo contactInfo = new ContactInfo(fullName[0], fullName[1], phoneNumber, organization);
		Activity activity = new Activity(category, description);
		TimeBlock timeBlock = new TimeBlock(startCal, endCal);
		return (new RoomBooking(contactInfo, activity, timeBlock));
    }
    
    private static Calendar makeCalendarFromUserInput(Calendar initCal, boolean requestHour) {
    	Calendar cal = Calendar.getInstance(); cal.clear();
    	String date = "";
    	int hour = 0;	
    	boolean needCal = (initCal==null);
    	
   		if (needCal) date = getResponseTo("Event Date (entered as DDMMYYYY): ");
   		int day = needCal ? Integer.parseInt(date.substring(0,2)) : initCal.get(Calendar.DAY_OF_MONTH);
   		int month = needCal ? Integer.parseInt(date.substring(2,4))-1 : initCal.get(Calendar.MONTH);
   		int year = needCal ? Integer.parseInt(date.substring(4,8)) : initCal.get(Calendar.YEAR);
     		
		if (requestHour) {				
		   String time = getResponseTo((needCal?"Start":"End") +" Time: ");
		   hour = processTimeString(time);
		}

		cal.set(year, month, day, hour, 0);
		return (cal);
    }
    
	private static int processTimeString(String t) {
		int hour = 0;
		t = t.trim();
		if (t.contains ("pm") || (t.contains("p.m."))) hour = Integer.parseInt(t.split(" ")[0]) + 12;
		if (t.contains("am") || t.contains("a.m.")) hour = Integer.parseInt(t.split(" ")[0]);
		if (t.contains(":")) hour = Integer.parseInt(t.split(":")[0]);
		return hour;
	}
	
    private RoomBooking findBooking(Calendar cal) {
    	Calendar oneHourLater = Calendar.getInstance();
    	oneHourLater.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)+1);
    	TimeBlock findTB = new TimeBlock(cal, oneHourLater);
    	for (int idx = 0; idx < getBookingIndex(); idx++) 
    		if (getRoomBookings()[idx].getTimeBlock().overlaps(findTB)) 
    			return getRoomBookings()[idx];		
    	return null;
    }
    	
	private boolean saveRoomBooking(RoomBooking roomBooking) {	
		TimeBlock tb = roomBooking.getTimeBlock();  // Check this TimeBlock to see if already booked
		Calendar cal = (Calendar)tb.getStartTime().clone(); // use its Calendar
		int hour = cal.get(Calendar.HOUR_OF_DAY);//Get first hour of block
		for (; hour < tb.getEndTime().get(Calendar.HOUR_OF_DAY); hour++){ //Loop through each hour in TimeBlock
			cal.set(Calendar.HOUR_OF_DAY, hour); // set next hour
		    if (findBooking(cal)!=null) {  // TimeBlock already booked at that hour, can't add appointment
		    	System.out.println("Cannot save booking; that time is already booked");
				return false;
		    }	
		}  // else time slot still available; continue loop to next hour
		getRoomBookings()[getBookingIndex()] = roomBooking;  
	    setBookingIndex(getBookingIndex()+1);
		System.out.println("Booking time and date saved.");  
		return true;
	}
	
	private RoomBooking displayBooking(Calendar cal) {  
		RoomBooking booking = findBooking(cal);
		int hr = cal.get(Calendar.HOUR_OF_DAY);
		System.out.print((booking!=null) ?
		   "---------------\n"+ booking.toString()+"---------------\n": 
  	       "No booking scheduled between "+ hr + ":00 and " + (hr + 1) + ":00\n"
		);
		return booking;
	}
	
	private void displayDayBookings(Calendar cal) {
		for (int hrCtr = 8; hrCtr < 24; hrCtr++) {
			cal.set(Calendar.HOUR_OF_DAY, hrCtr);
			RoomBooking rb = displayBooking(cal);	
			if (rb !=null) hrCtr = rb.getTimeBlock().getEndTime().get(Calendar.HOUR_OF_DAY) - 1;
		}
	}
	
	private RoomBooking[] getRoomBookings() {return roomBookings;}
	private int getBookingIndex() {return lastBookingIndex;}
	private void setBookingIndex(int bookingIndex) { this.lastBookingIndex = bookingIndex;}
	     
}

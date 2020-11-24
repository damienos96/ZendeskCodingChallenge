package challenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.ZendeskResponseException;
import org.zendesk.client.v2.model.Ticket;

/**
 * Main class for the Zendesk Coding Challenge
 * 
 * @author Damian Debny
 *
 */
public class ZendeskChallenge {
	
	public static final int PAGE_SIZE = 25;
	public static final int DOWNLOAD_SIZE = 100;
	public static final int LOWER_LIMIT = 1;
	public static final String ID_TITLE = "ID";
	public static final String SUBJECT_TITLE = "Subject";
	public static final String REQUESTER_TITLE = "Requested by";
	
	public static void main(String[] args) {
		boolean programFinished = false;
		int pageWindowStart = LOWER_LIMIT;
		int pageWindowEnd = PAGE_SIZE;
		long downloadWindowStart = LOWER_LIMIT;
		try{
			//Enter your domain, email address and password respectively.
			Zendesk zd = new Zendesk.Builder("https://damiandebny.zendesk.com"/*Domain here*/)
			        .setUsername("damiandebny@hotmail.com"/*Email here*/)
			        .setPassword("sep1996"/*Password here*/)
			        .build();
			
			System.out.println("Welcome to the Zendesk Ticket Viewer.");
			List<Ticket> tickets = downloadTickets(zd, downloadWindowStart);
			displayNewPage(1, PAGE_SIZE, tickets);
			while(!programFinished){
				Scanner input = new Scanner(System.in);
				System.out.print("Please choose an option below:\n"
						+ " 1. Exit.\n"
						+ " 2. View specific ticket.\n"
						+ " 3. View next 25 tickets.\n"
						+ " 4. View previous 25 tickets.\n");
				
				if(input.hasNextInt()){
					int option = input.nextInt();
					//If the user wants to quit the program.
					if(option == 1){
						System.out.println("Goodbye.");
						input.close();
						programFinished = true;
					}
					
					//If the user wants to access more detail about a specific ticket.
					else if(option == 2){
						boolean idSearchFinished = false;
						while(!idSearchFinished){
							System.out.print("Please enter the ID number of the ticket you would like to see:\n");
							if(input.hasNextInt()){
								int id = input.nextInt();
								idSearchFinished = searchSpecificTicket(zd, id);							
							}
							else{
								System.out.println("You have entered an invalid ID number. Try again.");
							}
						}
					}
					
					//If the user wants to see the next 25 tickets.
					else if(option == 3){
						if(pageWindowEnd >= DOWNLOAD_SIZE){
							downloadWindowStart += DOWNLOAD_SIZE;
							pageWindowStart = LOWER_LIMIT;
							pageWindowEnd = PAGE_SIZE;
							tickets = downloadTickets(zd, downloadWindowStart);
							if(tickets.size() == 0){
								System.out.println("There are no more tickets to be shown.");
								downloadWindowStart -= DOWNLOAD_SIZE;
								pageWindowStart = DOWNLOAD_SIZE - PAGE_SIZE + 1;
								pageWindowEnd = DOWNLOAD_SIZE;
							}
							else{
								displayNewPage(pageWindowStart, pageWindowEnd, tickets);
							}
						}
						else if(pageWindowEnd >= tickets.size() ){
							System.out.println("There are no more tickets to be shown.");
						}
						else{
							pageWindowStart += PAGE_SIZE;
							pageWindowEnd += PAGE_SIZE;
							displayNewPage(pageWindowStart, pageWindowEnd, tickets);
						}
					}
					
					//If the user wants to see the previous 25 tickets
					else if(option == 4){
						if(pageWindowStart == LOWER_LIMIT && downloadWindowStart == LOWER_LIMIT){
							System.out.println("There are no previous tickets.");
						}
						else if(pageWindowStart == LOWER_LIMIT){
							downloadWindowStart -= DOWNLOAD_SIZE;
							pageWindowStart = DOWNLOAD_SIZE - PAGE_SIZE + 1;
							pageWindowEnd = DOWNLOAD_SIZE;
							tickets = downloadTickets(zd, downloadWindowStart);
							displayNewPage(pageWindowStart, pageWindowEnd, tickets);
						}
						else{
							pageWindowStart -= PAGE_SIZE;
							pageWindowEnd -= PAGE_SIZE;
							displayNewPage(pageWindowStart, pageWindowEnd, tickets);
						}
					}
					else{
						System.out.println("You have not chosen one of the available options.");
					}
				}
				else{
					System.out.println("You have not chosen one of the available options.");
				}
			}
		}
		catch(ZendeskResponseException e){
			exceptionHandler(e);
		}
		System.exit(0);
	}
	
	/**
	 * Displays up to 25 tickets in the console.
	 * 
	 * @param pageWindowStart	The index of the first ticket that is accessed.
	 * @param pageWindowEnd		The index of the last ticket that is accessed.
	 * @param tickets			A list of tickets that is accessed.
	 */
	public static void displayNewPage(int pageWindowStart, int pageWindowEnd, List<Ticket> tickets){
		int idSize = 0, subjectSize = 0;
		for(int i = pageWindowStart - 1; i < pageWindowEnd && i < tickets.size(); i++){
			int tmpIdSize = String.valueOf(tickets.get(i).getId()).length();
			if(tmpIdSize > idSize){
				idSize = tmpIdSize;
			}
			int tmpSubjectSize = tickets.get(i).getSubject().length();
			if(tmpSubjectSize > subjectSize){
				subjectSize = tmpSubjectSize;
			}
		}
		
		String idTitle = ID_TITLE;
		if(ID_TITLE.length() >= idSize){
			idTitle += " |";
		}
		else if(ID_TITLE.length() < idSize){
			
			for(int i = 0; i < (idSize - ID_TITLE.length()); i++){
				idTitle += " ";
			}
			idTitle += " |";
		}
		
		String subjectTitle = SUBJECT_TITLE;
		if(SUBJECT_TITLE.length() >= subjectSize){
			subjectTitle += " |";
		}
		else if(SUBJECT_TITLE.length() < subjectSize){
			
			for(int i = 0; i < (subjectSize - SUBJECT_TITLE.length()); i++){
				subjectTitle += " ";
			}
			subjectTitle += " |";
		}
		
		System.out.println(idTitle + subjectTitle + REQUESTER_TITLE);
		for(int i = pageWindowStart - 1; i < pageWindowEnd && i < tickets.size(); i++){
			String output = "" + tickets.get(i).getId();
			int tmpIdSize = idSize - String.valueOf(tickets.get(i).getId()).length();
			if(tmpIdSize > 0){
				for(int j = 0; j < tmpIdSize; j++){
					output += " ";
				}
			}
			output += " |" + tickets.get(i).getSubject();
			int tmpSubjectSize = subjectSize - tickets.get(i).getSubject().length();
			if(tmpSubjectSize > 0){
				for(int j = 0; j < tmpSubjectSize; j++){
					output += " ";
				}
			}
			output += " |" + tickets.get(i).getRequesterId();
			System.out.println(output);
		}
	}
	
	/**
	 * Finds and displays information about one specific ticket.
	 * 
	 * @param client	The Zendesk API client.
	 * @param id		The id of the ticket to be found.
	 * @return 			True if the ticket is found, false if the ticket wasn't found.
	 */
	public static boolean searchSpecificTicket(Zendesk client, int id){
		boolean foundTicket = false;
		if(id < 1){
			System.out.println("A ticket with ID number you have given doesn't exist. Try again.");
		}
		else{
			Ticket tmp = client.getTicket(id);
			if(tmp == null){
				System.out.println("A ticket with ID number you have given doesn't exist. Try again.");
			}
			else{
				System.out.println("Ticket ID: " + tmp.getId() 
									+ "\tRequested by: " + tmp.getRequesterId() 
									+ "\tDate created: " + tmp.getCreatedAt() 
									+ "\nSubject: " + tmp.getSubject()
									+ "\nDescription: \n" + tmp.getDescription());
				foundTicket = true;
			}
		}
		return foundTicket;
	}
	
	/**
	 * Download a set of tickets.
	 * 
	 * @param client				The Zendesk API client.
	 * @param downloadWindowStart	The index of the first ticket to be downloaded.
	 * @return						A set of 100 tickets.
	 */
	public static List<Ticket> downloadTickets(Zendesk client, long downloadWindowStart){
		System.out.println("Loading tickets...");
		List<Ticket> tickets = new ArrayList<Ticket>();
		long[] downloadSize = new long[DOWNLOAD_SIZE-1];
		for(int i = 0; i < DOWNLOAD_SIZE-1; i++){
			downloadSize[i] = downloadWindowStart + i + 1;
		}
		tickets = client.getTickets(downloadWindowStart, downloadSize);
		return tickets;
	}
	
	/**
	 * Deals with an exception thrown by the Zendesk API.
	 * 
	 * @param e	The exception thrown by the Zendesk API.
	 */
	public static void exceptionHandler(ZendeskResponseException e){
		//If the API is unavailable.
		if(e.getStatusCode() == 404){
			System.out.println("Unfortunately, the Ticket Viewer can't connect to Zendesk right now. Please try again later.");
		}
		else if(e.getStatusCode() == 401){
			System.out.println("One or more of the login parameters entered are incorrect.");
		}
		else{
			System.out.println("Unfortunately there has been an error. Please try again later.");
		}
	}
}
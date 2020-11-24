package challenge;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.Ticket;

public class ZendeskChallengeTest {
	
	//Enter your domain, email address and password respectively.
	Zendesk zd = new Zendesk.Builder("https://damiandebny.zendesk.com"/*Domain here*/)
	        .setUsername("damiandebny@hotmail.com"/*Email here*/)
	        .setPassword("sep1996"/*Password here*/)
	        .build();
	
	@Test
	public void ticketSearchTest(){
		boolean firstTest = ZendeskChallenge.searchSpecificTicket(zd, 0);
		assertEquals("Checking availability of the given ticket", false, firstTest);
		boolean secondTest = ZendeskChallenge.searchSpecificTicket(zd, 25);
		assertEquals("Checking availability of the given ticket", true, secondTest);
		boolean thirdTest = ZendeskChallenge.searchSpecificTicket(zd, 100);
		assertEquals("Checking availability of the given ticket", true, thirdTest);
		boolean fourthTest = ZendeskChallenge.searchSpecificTicket(zd, 300);
		assertEquals("Checking availability of the given ticket", false, fourthTest);
	}
	
	@Test
	public void ticketDownloadTest(){
		List<Ticket> firstTickets = ZendeskChallenge.downloadTickets(zd, ZendeskChallenge.LOWER_LIMIT);
		assertEquals("Checking number of tickets downloaded", 100, firstTickets.size());
		List<Ticket> secondTickets = ZendeskChallenge.downloadTickets(zd, ZendeskChallenge.LOWER_LIMIT + 100);
		assertEquals("Checking number of tickets downloaded", 100, secondTickets.size());
		List<Ticket> thirdTickets = ZendeskChallenge.downloadTickets(zd, ZendeskChallenge.LOWER_LIMIT + 200);
		assertEquals("Checking number of tickets downloaded", 2, thirdTickets.size());
		List<Ticket> fourthTickets = ZendeskChallenge.downloadTickets(zd, ZendeskChallenge.LOWER_LIMIT + 300);
		assertEquals("Checking number of tickets downloaded", 0, fourthTickets.size());
	}
}

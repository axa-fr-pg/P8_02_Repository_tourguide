package tourguide.service;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import tourguide.helper.InternalTestHelper;
import tourguide.user.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

	@Test
	public void getAllUsers() {
		InternalTestHelper.setInternalUserNumber(0);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

//		tourGuideService.addUser(user);
//		tourGuideService.addUser(user2);
		
//		List<User> allUsers = tourGuideService.getAllUsers();

//		tourGuideService.tracker.stopTracking();
	assertTrue(false); // TODO	
//		assertTrue(allUsers.contains(user));
//		assertTrue(allUsers.contains(user2));
	}
	
}

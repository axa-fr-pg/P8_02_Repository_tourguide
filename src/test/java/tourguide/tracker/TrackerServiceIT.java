package tourguide.tracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import tourguide.model.User;
import tourguide.reward.RewardService;
import tourguide.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrackerServiceIT {

	@Autowired private RewardService rewardService;
	@Autowired private TrackerService trackerService;
	@Autowired private UserService userService;
	
	@Test
	public void givenUsers_whenTrackAllUsers_thenAllUsersAreUpdated() {
		// GIVEN
		userService.initializeInternalUsers(3, false);
		List<User> users = userService.getAllUsers();
		long totalRewardPoints = 0;
		// WHEN
		try {
			trackerService.trackAllUsers();
		} catch (InterruptedException | ExecutionException e) {
			assertTrue(false);
		}
		// THEN
		for (User user : users) {
			assertNotNull(user.getVisitedLocations());
			assertEquals(1, user.getVisitedLocations().size());
			totalRewardPoints += rewardService.sumOfAllRewardPoints(user);
		}
		assertThat(totalRewardPoints > 0);
	}
}

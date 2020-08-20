package tourguide.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import tourguide.model.User;
import tourguide.reward.RewardService;
import tourguide.tracker.TrackerService;
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
		long totalRewardPoints = 0;
		// WHEN
		trackerService.trackAllUsers();
		List<User> users = userService.getAllUsers();
		// THEN
		for (User user : users) {
			assertNotNull(user.getVisitedLocations());
			assertEquals(1, user.getVisitedLocations().size());
			totalRewardPoints += rewardService.sumOfAllRewardPoints(user);
		}
		assertThat(totalRewardPoints > 0);
	}
}

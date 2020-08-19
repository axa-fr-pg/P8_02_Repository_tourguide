package tourguide.integration;

import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import tourguide.gps.GpsService;
import tourguide.model.AttractionData;
import tourguide.model.User;
import tourguide.model.VisitedLocationData;
import tourguide.reward.RewardService;
import tourguide.tracker.TrackerService;
import tourguide.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TourGuidePerformanceIT {
	
	Logger logger = LoggerFactory.getLogger(TourGuidePerformanceIT.class);

	@Autowired private TrackerService trackerService;
	@Autowired private UserService userService;
	@Autowired private RewardService rewardService;
	@Autowired private GpsService gpsService;
	
//	@Ignore
	@Test // Performance before optimization
	public void given100Users_whenTrackAllUsers_thenTimeElapsedBelow7Seconds() {
		givenUsers_whenTrackAllUsers_thenTimeElapsedBelowLimit(100, 7);
	}
	
	@Ignore
	@Test // Performance after optimization
	public void given100000Users_whenTrackAllUsers_thenTimeElapsedBelow15Minutes() {
		givenUsers_whenTrackAllUsers_thenTimeElapsedBelowLimit(100 * 1000, 15 * 60);
	}
	
//	@Ignore
	@Test // Performance before optimization
	public void given100Users_whenAddAllNewRewardsAllUsers_thenTimeElapsedBelow58Seconds() {
		givenUsers_whenAddAllNewRewardsAllUsers_thenTimeElapsedBelowLimit(100, 58);
	}
	
	@Ignore
	@Test // Performance after optimization
	public void given100000Users_whenAddAllNewRewardsAllUsers_thenTimeElapsedBelow20Minutes() {
		givenUsers_whenAddAllNewRewardsAllUsers_thenTimeElapsedBelowLimit(100 * 1000, 20 * 60);
	}
	
	private void givenUsers_whenTrackAllUsers_thenTimeElapsedBelowLimit(
			int numberOfUsers, long maximalExpectedDuration) {
		// GIVEN
		userService.initializeInternalUsers(numberOfUsers, true);
	    // WHEN
		long duration  = trackerService.trackAllUsers();
		// THEN
		assertTrue(duration <= maximalExpectedDuration);
	}

	private void givenUsers_whenAddAllNewRewardsAllUsers_thenTimeElapsedBelowLimit(
			int numberOfUsers, long maximalExpectedDuration) {
		// GIVEN
		userService.initializeInternalUsers(numberOfUsers, false);
		List<User> allUsers = userService.getAllUsers();
		List<AttractionData> allAttractions = gpsService.getAllAttractions();	 
		AttractionData anyExistingAttraction = allAttractions.get(0);
		for(User user : allUsers) {
			user.addToVisitedLocations(new VisitedLocationData(user.getUserId(), anyExistingAttraction, new Date()));
		}
	    // WHEN
		long duration = rewardService.addAllNewRewardsAllUsers(allUsers, allAttractions);
		// THEN
		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		assertTrue(duration <= maximalExpectedDuration);
	}

}

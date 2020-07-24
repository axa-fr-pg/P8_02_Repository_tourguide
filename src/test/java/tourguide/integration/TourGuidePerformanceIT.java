package tourguide.integration;

import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourguide.gps.GpsService;
import tourguide.model.User;
import tourguide.reward.RewardService;
import tourguide.tracker.TrackerService;
import tourguide.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
public class TourGuidePerformanceIT {
	
	Logger logger = LoggerFactory.getLogger(TourGuidePerformanceIT.class);

	@Autowired private TrackerService trackerService;
	@Autowired private UserService userService;
	@Autowired private RewardService rewardService;
	@Autowired private GpsService gpsService;
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */
	
	@Test // Performance before optimization
	public void given100Users_whenTrackAllUsers_thenTimeElapsedBelow7Seconds() {
		// GIVEN
		userService.initializeInternalUsers(100, true);
		long maximalExpectedDuration = 7;
	    // WHEN
		long duration =  maximalExpectedDuration + 1;
		try {
			duration = trackerService.trackAllUsers();
		} catch (InterruptedException | ExecutionException e) {
			assertTrue(false);
		}
		// THEN
		assertTrue(duration <= maximalExpectedDuration);
	}
	
	@Test // Performance before optimization
	public void given100Users_whenAddAllNewRewardsAllUsers_thenTimeElapsedBelow58Seconds() {
		// GIVEN
		userService.initializeInternalUsers(100, false);
		List<User> allUsers = userService.getAllUsers();
		List<Attraction> allAttractions = gpsService.getAllAttractions();	 
		Attraction anyExistingAttraction = allAttractions.get(0);
		long maximalExpectedDuration = 58;
		for(User user : allUsers) {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), anyExistingAttraction, new Date()));
		}
	    // WHEN
		long duration = maximalExpectedDuration + 1;
		try {
			duration = rewardService.addAllNewRewardsAllUsers(allUsers, allAttractions);
		} catch (InterruptedException | ExecutionException e) {
			assertTrue(false);
		}
		// THEN
		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		assertTrue(duration <= maximalExpectedDuration);
	}
	
	@Test // Performance after optimization
	public void given100000Users_whenTrackAllUsers_thenTimeElapsedBelow15Minutes() {
		// GIVEN
		userService.initializeInternalUsers(100000, true);
		long maximalExpectedDuration = 15 * 60;
	    // WHEN
		long duration = maximalExpectedDuration + 1;
		try {
			duration = trackerService.trackAllUsers();
		} catch (InterruptedException | ExecutionException e) {
			assertTrue(false);
		}
		// THEN
		assertTrue(duration <= maximalExpectedDuration);
	}
	
	@Test // Performance after optimization
	public void given100000Users_whenAddAllNewRewardsAllUsers_thenTimeElapsedBelow20Minutes() {
		// GIVEN
		userService.initializeInternalUsers(1000, false);
		List<User> allUsers = userService.getAllUsers();
		List<Attraction> allAttractions = gpsService.getAllAttractions();	 
		Attraction anyExistingAttraction = allAttractions.get(0);
		long maximalExpectedDuration = 20 * 60;

		for(User user : allUsers) {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), anyExistingAttraction, new Date()));
		}
	    // WHEN
		long duration = maximalExpectedDuration + 1;
		try {
			duration = rewardService.addAllNewRewardsAllUsers(allUsers, allAttractions);
		} catch (InterruptedException | ExecutionException e) {
			assertTrue(false);
		}
		// THEN
		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		assertTrue(duration <= maximalExpectedDuration);
	}
}
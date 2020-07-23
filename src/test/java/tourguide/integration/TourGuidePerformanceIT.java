package tourguide.integration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourguide.gps.GpsService;
import tourguide.model.User;
import tourguide.reward.RewardService;
import tourguide.service.TourGuideService;
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
	
	@Test
	public void given100Users_whenTrackAllUsers_thenTimeElapsedBelow7Seconds() {
		// GIVEN
		userService.initializeInternalUsers(100, true);
	    // WHEN
		long duration = trackerService.trackAllUsers();
		// THEN
		assertTrue(duration <= 7);
	}
	
	@Test
	public void given100Users_whenAddAllNewRewardsAllUsers_thenTimeElapsedBelow58Seconds() {
		// GIVEN
		StopWatch stopWatch = new StopWatch();
		userService.initializeInternalUsers(100, false);
		List<User> allUsers = userService.getAllUsers();
		List<Attraction> allAttractions = gpsService.getAllAttractions();	 
		Attraction anyExistingAttraction = allAttractions.get(0);	 
		for(User user : allUsers) {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), anyExistingAttraction, new Date()));
		}
	    // WHEN
		long duration = rewardService.addAllNewRewardsAllUsers(allUsers, allAttractions);
		// THEN
		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		assertTrue(duration <= 58);
	}
}

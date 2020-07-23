package tourguide;

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
import tourguide.model.User;
import tourguide.reward.RewardService;
import tourguide.service.TourGuideService;
import tourguide.tracker.TrackerService;
import tourguide.user.UserInternalNumber;
import tourguide.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPerformance {
	
	Logger logger = LoggerFactory.getLogger(TestPerformance.class);

	@Autowired GpsUtil gpsUtil; 
	@Autowired TourGuideService tourGuideService;
	@MockBean TrackerService tracker;
	
	@Before
	public void setup() {
		doNothing().when(tracker).run(); // In order to have a reproductible test
	}

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
	public void given100Users_whenTrackUserLocation_thenTimeElapsedBelow7Seconds() {
		// GIVEN
		UserInternalNumber.set(100);
		UserService userService = new UserService();
		List<User> allUsers = userService.getAllUsers();		
	    StopWatch stopWatch = new StopWatch();
	    // WHEN
		stopWatch.start();
		for(User user : allUsers) {
			tourGuideService.trackUserLocationAndCalculateRewards(user);
		}
		stopWatch.stop();
		// THEN
		logger.info("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.SECONDS.toSeconds(7) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
	@Test
	public void given100Users_whenCalculateRewards_thenTimeElapsedBelow58Seconds() {
		// GIVEN
		UserInternalNumber.set(100);
		UserService userService = new UserService();
	    StopWatch stopWatch = new StopWatch();
	    // WHEN
		Attraction attraction = gpsUtil.getAttractions().get(0);	 
		List<User> allUsers = userService.getAllUsers();
		stopWatch.start();
		for(User user : allUsers) {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
			tourGuideService.addUserRewards(user);
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		// THEN
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.SECONDS.toSeconds(58) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}

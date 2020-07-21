package tourguide.rewardservice;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourguide.rewardservice.RewardService;
import tourguide.service.TestHelperService;
import tourguide.service.TourGuideService;
import tourguide.tracker.Tracker;
import tourguide.user.User;
import tourguide.user.UserReward;
import tourguide.userservice.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RewardsServiceTest {
	
	@Test
	public void givenTwoLocations_whenGetDistance_thenReturnsCorrectDistance() {
		// GIVEN
		Location parisLocation = new Location(48.8534, 2.3488);
		Location londonLocation = new Location(51.5084, -0.1255);
		// WHEN
		double distance = RewardService.getDistance(parisLocation, londonLocation);
		// THEN
		assertEquals(213, distance, 1);
	}
}

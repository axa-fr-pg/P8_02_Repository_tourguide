package tourguide;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourguide.helper.InternalTestHelper;
import tourguide.service.RewardsService;
import tourguide.service.TourGuideService;
import tourguide.tracker.Tracker;
import tourguide.user.User;
import tourguide.user.UserReward;

public class TestRewardsService {
	
	@Autowired TourGuideService tourGuideService;
	@Autowired Tracker tracker;
	@Autowired GpsUtil gpsUtil;
	@Autowired RewardsService rewardsService;

	@Test
	public void userGetRewards() {
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}
	
	@Test
	public void isWithinAttractionProximity() {
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	@Test // Needs fixed - can throw ConcurrentModificationException
	public void nearAllAttractions() {
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
		tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}
	
	@Test
	public void givenTwoLocations_whenGetDistance_thenReturnsCorrectDistance() {
		// GIVEN
		Location parisLocation = new Location(48.8534, 2.3488);
		Location londonLocation = new Location(51.5084, -0.1255);
		// WHEN
		double distance = RewardsService.getDistance(parisLocation, londonLocation);
		// THEN
		assertEquals(213, distance, 1);
	}
}

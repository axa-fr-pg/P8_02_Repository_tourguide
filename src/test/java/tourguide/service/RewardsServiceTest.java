package tourguide.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
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
import tourguide.helper.InternalTestHelper;
import tourguide.service.RewardsService;
import tourguide.service.TourGuideService;
import tourguide.tracker.Tracker;
import tourguide.user.User;
import tourguide.user.UserReward;
import tripPricer.TripPricer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RewardsServiceTest {
	
	@MockBean GpsUtil gpsUtil;
	@MockBean Tracker tracker; // TODO replace with interface
	@MockBean UserService userService;  // TODO replace with interface
	@MockBean TourGuideService tourGuideService;  // TODO replace with interface
	@MockBean RewardCentral rewardCentral;
	@Autowired RewardsService rewardsService;
	@Autowired TestHelperService testHelperService;

	@Test
	public void givenPrerequisitesToAdd1RewardOk_whenCalculateRewards_thenAddsCorrectReward() {
		// MOCK gpsUtil.getAttractions
		List<Attraction> givenAttractions = testHelperService.mockGpsUtilGetAttractions();
		// GIVEN
		int expectedRewardPoints = 123;
		User user = new User(UUID.randomUUID(), "name", "phone", "email");
		Attraction attraction = givenAttractions.get(0);
		Location location = new Location(attraction.latitude, attraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		// MOCK rewardCentral.getAttractionRewardPoints
		when(rewardCentral.getAttractionRewardPoints(any(UUID.class), eq(user.getUserId()))).thenReturn(expectedRewardPoints);
		// WHEN
		rewardsService.calculateRewards(user);
		List<UserReward> userRewards = user.getUserRewards();
		// THEN
		assertNotNull(userRewards);
		assertEquals(1, userRewards.size());
		assertNotNull(userRewards.get(0));
		assertEquals(expectedRewardPoints, userRewards.get(0).getRewardPoints());
	}

	@Test
	public void givenTooFarToAddReward_whenCalculateRewards_thenAddsNoReward() {
		// MOCK gpsUtil.getAttractions
		List<Attraction> givenAttractions = testHelperService.mockGpsUtilGetAttractions();
		// GIVEN
		double latitudeDifferenceMakingItTooFar = 0.15; // for defaultProximityBuffer = 10
		User user = new User(UUID.randomUUID(), "name", "phone", "email");
		Attraction attraction = givenAttractions.get(0);
		Location location = new Location(attraction.latitude + latitudeDifferenceMakingItTooFar, attraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		// MOCK rewardCentral.getAttractionRewardPoints
		when(rewardCentral.getAttractionRewardPoints(any(UUID.class), eq(user.getUserId()))).thenReturn(999);
		// WHEN
		rewardsService.calculateRewards(user);
		List<UserReward> userRewards = user.getUserRewards();
		// THEN
		assertNotNull(userRewards);
		assertEquals(0, userRewards.size());
	}

	@Test
	public void givenAlreadyRewardedVisit_whenCalculateRewards_thenAddsNoReward() {
		// MOCK gpsUtil.getAttractions
		List<Attraction> givenAttractions = testHelperService.mockGpsUtilGetAttractions();
		// GIVEN
		int expectedRewardPoints = 123;
		User user = new User(UUID.randomUUID(), "name", "phone", "email");
		Attraction attraction = givenAttractions.get(0);
		Location location = new Location(attraction.latitude, attraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		UserReward userReward = new UserReward(visitedLocation, attraction, expectedRewardPoints);
		user.addUserReward(userReward);
		// MOCK rewardCentral.getAttractionRewardPoints
		when(rewardCentral.getAttractionRewardPoints(any(UUID.class), eq(user.getUserId()))).thenReturn(999);
		// WHEN
		rewardsService.calculateRewards(user);
		List<UserReward> userRewards = user.getUserRewards();
		// THEN
		assertNotNull(userRewards);
		assertEquals(1, userRewards.size());
		assertNotNull(userRewards.get(0));
		assertEquals(expectedRewardPoints, userRewards.get(0).getRewardPoints());
	}

	/* Method not used --> not need to test
	@Test
	public void isWithinAttractionProximity() {
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	} */
	
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

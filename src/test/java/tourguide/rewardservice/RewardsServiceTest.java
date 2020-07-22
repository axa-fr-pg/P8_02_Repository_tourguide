package tourguide.rewardservice;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourguide.rewardservice.RewardService;
import tourguide.service.TestHelperService;
import tourguide.user.User;
import tourguide.user.UserReward;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RewardsServiceTest {
	
	@Autowired RewardService rewardService;
	@Autowired TestHelperService testHelperService;
	@MockBean RewardCentral rewardCentral;

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
	
	@Test
	public void givenPrerequisitesToAdd1RewardOk_whenAddAllNewRewards_thenAddsCorrectReward() {
		// MOCK rewardCentral
		int expectedRewardPoints = 123;
		Attraction expectedAttraction = new Attraction("attraction_name", "attraction_city", "attraction_state", 0.31, -0.32);
		Attraction tooFarAttraction = new Attraction("far_name", "far_city", "far_state", 99, -99);
		User user = new User(new UUID(11,12), "user_name", "user_phone", "user_email");
		when(rewardCentral.getAttractionRewardPoints(eq(expectedAttraction.attractionId), eq(user.getUserId())))
			.thenReturn(expectedRewardPoints);
		List<Attraction> attractions = Arrays.asList(expectedAttraction, tooFarAttraction);
		assertEquals(2, attractions.size());
		// GIVEN user was close enough to the attraction
		rewardService.setProximityBuffer(10); // statute miles
		double latitudeDifferenceMakingItCloseEnough = 0.14; // degrees
		Location location = new Location(expectedAttraction.latitude + latitudeDifferenceMakingItCloseEnough, expectedAttraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		// WHEN
		rewardService.addAllNewRewards(user, user.getVisitedLocations(), attractions);
		List<UserReward> userRewards = user.getUserRewards();
		// THEN
		assertNotNull(userRewards);
		assertEquals(1, userRewards.size());
		assertNotNull(userRewards.get(0));
		assertEquals(expectedRewardPoints, userRewards.get(0).getRewardPoints());
	}

	@Test
	public void givenTooFarToAddReward_whenAddAllNewRewards_thenAddsNoReward() {
		// MOCK rewardCentral
		int expectedRewardPoints = 123;
		Attraction attraction = new Attraction("attraction_name", "attraction_city", "attraction_state", 0.31, -0.32);
		User user = new User(new UUID(11,12), "user_name", "user_phone", "user_email");
		when(rewardCentral.getAttractionRewardPoints(eq(attraction.attractionId), eq(user.getUserId())))
			.thenReturn(expectedRewardPoints);
		List<Attraction> attractions = Arrays.asList(attraction);
		// GIVEN user was close enough to the attraction
		rewardService.setProximityBuffer(10); // statute miles
		double latitudeDifferenceMakingItTooFar = 0.15; // degrees
		Location location = new Location(attraction.latitude + latitudeDifferenceMakingItTooFar, attraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		// WHEN
		rewardService.addAllNewRewards(user, user.getVisitedLocations(), attractions);
		List<UserReward> userRewards = user.getUserRewards();
		// THEN
		assertNotNull(userRewards);
		assertEquals(0, userRewards.size());
	}

	@Test
	public void givenAlreadyRewardedVisit_whenAddAllNewRewards_thenAddsNoReward() {
		// MOCK rewardCentral
		int expectedRewardPoints = 123;
		Attraction expectedAttraction = new Attraction("attraction_name", "attraction_city", "attraction_state", 0.31, -0.32);
		Attraction tooFarAttraction = new Attraction("far_name", "far_city", "far_state", 99, -99);
		User user = new User(new UUID(11,12), "user_name", "user_phone", "user_email");
		when(rewardCentral.getAttractionRewardPoints(eq(expectedAttraction.attractionId), eq(user.getUserId())))
			.thenReturn(999 + expectedRewardPoints);
		List<Attraction> attractions = Arrays.asList(expectedAttraction, tooFarAttraction);
		assertEquals(2, attractions.size());
		// GIVEN user has already been rewarded for this attraction
		rewardService.setProximityBuffer(10); // statute miles
		double latitudeDifferenceMakingItCloseEnough = 0.14; // degrees
		Location location = new Location(expectedAttraction.latitude + latitudeDifferenceMakingItCloseEnough, expectedAttraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		UserReward userReward = new UserReward(visitedLocation, expectedAttraction, expectedRewardPoints);
		user.addUserReward(userReward);
		// WHEN
		rewardService.addAllNewRewards(user, user.getVisitedLocations(), attractions);
		List<UserReward> userRewards = user.getUserRewards();
		// THEN
		assertNotNull(userRewards);
		assertEquals(1, userRewards.size());
		assertNotNull(userRewards.get(0));
		assertEquals(expectedRewardPoints, userRewards.get(0).getRewardPoints());
	}

	@Test
	public void givenMaximalProximityBuffer_whenAddAllNewRewards_thenAddsRewardsForAllAttractions() {
		// MOCK rewardCentral
		int expectedRewardPoints = 123;
		Attraction attraction1 = new Attraction("attraction_name1", "attraction_city1", "attraction_state1", 0.311, -0.321);
		Attraction attraction2 = new Attraction("attraction_name2", "attraction_city2", "attraction_state2", 0.312, -0.322);
		User user = new User(new UUID(11,12), "user_name", "user_phone", "user_email");
		when(rewardCentral.getAttractionRewardPoints(any(UUID.class), eq(user.getUserId())))
			.thenReturn(expectedRewardPoints);
		List<Attraction> attractions = Arrays.asList(attraction1, attraction2);
		assertEquals(2, attractions.size());
		// GIVEN user is close enough to all attractions
		rewardService.setProximityBuffer((Integer.MAX_VALUE/2 ) -1);
		Location location = new Location(0, 0);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		// WHEN
		rewardService.addAllNewRewards(user, user.getVisitedLocations(), attractions);
		List<UserReward> userRewards = user.getUserRewards();
		// THEN
		assertNotNull(userRewards);
		assertEquals(2, userRewards.size());
		assertNotNull(userRewards.get(0));
		assertEquals(expectedRewardPoints, userRewards.get(0).getRewardPoints());
	}
}

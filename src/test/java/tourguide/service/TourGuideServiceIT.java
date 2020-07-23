package tourguide.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourguide.gps.GpsService;
import tourguide.model.AttractionNearby;
import tourguide.model.User;
import tourguide.model.UserPreferences;
import tourguide.model.UserReward;
import tourguide.reward.RewardService;
import tourguide.service.TourGuideService;
import tourguide.tracker.TrackerService;
import tourguide.user.UserService;
import tripPricer.Provider;
import tripPricer.TripPricer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TourGuideServiceIT {

	@MockBean GpsUtil gpsUtil;
	@MockBean TripPricer tripPricer;
	@MockBean TrackerService tracker;
	@MockBean UserService userService;
	@MockBean RewardCentral rewardCentral;

	@Autowired RewardService rewardService;
	@Autowired TourGuideService tourGuideService;
	@Autowired TestHelperService testHelperService;
	@Autowired GpsService gpsService;

	@Before
	public void deactivateUnexpectedServices() {
		doNothing().when(tracker).run();
		doNothing().when(userService).initializeInternalUsers();
	}
	
	/* NOT USED ANY MORE
	@Test
	public void givenUser_whenTrackUserLocation_thenLocationAddedToUserHistory() {
		// GIVEN mock GpsUtil
		User user = testHelperService.mockUserServiceGetUserAndGpsUtilGetUserLocation(1, null);
		// WHEN
		VisitedLocation visitedLocation = tourGuideService.trackUserLocationAndCalculateRewards(user);
		// THEN
		assertEquals(2, user.getVisitedLocations().size());
		assertNotNull(visitedLocation);
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
		assertEquals(visitedLocation.location.latitude, user.getLastVisitedLocation().location.latitude, 0.0000000001);
		assertEquals(visitedLocation.location.longitude, user.getLastVisitedLocation().location.longitude, 0.0000000001);
	}*/
	
	@Test
	public void givenAttractions_whenGetNearByAttractions_thenCorrectListReturned() {
		// GIVEN mock UserService & GpsUtil
		User user = testHelperService.mockUserServiceGetUserAndGpsUtilGetUserLocation(1, null);
		// MOCK getAttractions
		testHelperService.mockGpsUtilGetAttractions();
		// WHEN
		List<AttractionNearby> resultAttractions = tourGuideService.getNearByAttractions(user.getUserName());
		// THEN
		assertNotNull(resultAttractions);
		assertEquals(TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS, resultAttractions.size());
		double resultCheckSum = 0;
		for (AttractionNearby a : resultAttractions) {
			resultCheckSum += a.attractionLocation.longitude;
		}
		double expectedCheckSum = ( TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS + 1)
				* TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS / 2 
				* TestHelperService.LONGITUDE_ATTRACTION_ONE ;
		assertEquals(expectedCheckSum, resultCheckSum, 0.00000000001);
	}
	
	@Test
	public void givenDuration4Price_whenGetTripDealsForDuration8_thenReturnsDoublePrice() {
		// GIVEN
		int adults = 2;
		int children = 3;
		int duration = 4;
		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setNumberOfAdults(adults);
		userPreferences.setNumberOfChildren(children);
		userPreferences.setTripDuration(duration);
		// MOCK getUser
		User user = testHelperService.mockUserServiceGetUserAndGpsUtilGetUserLocation(1, userPreferences);
		// MOCK getAttractions
		testHelperService.mockGpsUtilGetAttractions();
		// MOCK getPrice
		double priceForDuration4 = 1000;
		List<Provider> givenProvidersSimple = new ArrayList<Provider>();
		givenProvidersSimple.add(new Provider(null, "providerSimple", priceForDuration4));
		List<Provider> givenProvidersDouble = new ArrayList<Provider>();
		givenProvidersDouble.add(new Provider(null, "providerDouble", 2*priceForDuration4));
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), eq(duration), anyInt()))
			.thenReturn(givenProvidersSimple);
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), eq(2*duration), anyInt()))
			.thenReturn(givenProvidersDouble);
		// WHEN
		List<Provider> duration4Providers = tourGuideService.getTripDeals(user);
		userPreferences.setTripDuration(2*duration);
		List<Provider> duration8Providers = tourGuideService.getTripDeals(user);
		// THEN
		assertNotNull(duration4Providers);
		assertNotNull(duration8Providers);
		assertNotNull(duration4Providers.size());
		assertNotNull(duration8Providers.size());
		assertNotNull(duration4Providers.get(0));
		assertNotNull(duration8Providers.get(0));
		assertEquals(duration4Providers.get(0).price *2, duration8Providers.get(0).price, 0.0000001);
	}
	
	@Test
	public void given1ChildPrice_whenGetTripDealsWith2Children_thenReturnsDoublePriceForChildren() {
		// GIVEN
		int adults = 0;
		int children = 1;
		int duration = 3;
		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setNumberOfAdults(adults);
		userPreferences.setNumberOfChildren(children);
		userPreferences.setTripDuration(duration);
		// MOCK getUser
		User user = testHelperService.mockUserServiceGetUserAndGpsUtilGetUserLocation(1, userPreferences);
		// MOCK getAttractions
		testHelperService.mockGpsUtilGetAttractions();
		// MOCK getPrice
		double priceForOneChild = 100;
		List<Provider> givenProvidersSimple = new ArrayList<Provider>();
		givenProvidersSimple.add(new Provider(null, "providerSimple", priceForOneChild));
		List<Provider> givenProvidersDouble = new ArrayList<Provider>();
		givenProvidersDouble.add(new Provider(null, "providerDouble", 2*priceForOneChild));
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), eq(children), anyInt(), anyInt()))
			.thenReturn(givenProvidersSimple);
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), eq(2*children), anyInt(), anyInt()))
			.thenReturn(givenProvidersDouble);
		// WHEN
		List<Provider> providers1Child = tourGuideService.getTripDeals(user);
		userPreferences.setNumberOfChildren(2*children);
		List<Provider> providers2Children = tourGuideService.getTripDeals(user);
		// THEN
		assertNotNull(providers1Child);
		assertNotNull(providers2Children);
		assertNotNull(providers1Child.size());
		assertNotNull(providers2Children.size());
		assertNotNull(providers1Child.get(0));
		assertNotNull(providers2Children.get(0));
		assertEquals(providers1Child.get(0).price *2, providers2Children.get(0).price, 0.0000001);
	}
	
	@Test
	public void givenUserList_whenGetLastLocationAllUsers_thenReturnsCorrectList() {
		// MOCK getAllUsers
		List<User> givenUsers = testHelperService.mockGetAllUsersAndLocations(5);
		// WHEN
		Map<String,Location> allUserLocations = tourGuideService.getLastLocationAllUsers();
		// THEN
		assertNotNull(allUserLocations);
		assertEquals(givenUsers.size(), allUserLocations.size()); // CHECK LIST SIZE
		User givenUser = givenUsers.get(0);
		assertNotNull(givenUser);
		assertNotNull(givenUser.getUserId());
		Location resultLocation = allUserLocations.get(givenUser.getUserId().toString());
		assertNotNull(resultLocation);
		VisitedLocation givenVisitedLocation = givenUser.getLastVisitedLocation();
		assertNotNull(givenVisitedLocation);
		Location givenLocation = givenVisitedLocation.location;
		assertNotNull(givenLocation);
		assertEquals(givenLocation.latitude, resultLocation.latitude, 0.0000000001); // CHECK LOCATION FOR FIRST GIVEN USER
		assertEquals(givenLocation.longitude, resultLocation.longitude, 0.0000000001); // CHECK LOCATION FOR FIRST GIVEN USER
	}
	
	@Test
	public void givenPrerequisitesToAdd1RewardOk_whenAddUserRewards_thenAddsCorrectReward() {
		// MOCK gpsUtil.getAttractions
		List<Attraction> givenAttractions = testHelperService.mockGpsUtilGetAttractions();
		// MOCK rewardCentral
		int expectedRewardPoints = 123;
		User user = new User(new UUID(11,12), "user_name", "user_phone", "user_email");
		Attraction expectedAttraction = givenAttractions.get(0);
		when(rewardCentral.getAttractionRewardPoints(eq(expectedAttraction.attractionId), eq(user.getUserId())))
			.thenReturn(expectedRewardPoints);
		// GIVEN user was close enough to the attraction
		rewardService.setProximityMaximalDistance(10); // statute miles
		double latitudeDifferenceMakingItCloseEnough = 0.14; // degrees
		Location location = new Location(expectedAttraction.latitude - latitudeDifferenceMakingItCloseEnough, expectedAttraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		// WHEN
		tourGuideService.addUserRewards(user);
		List<UserReward> userRewards = tourGuideService.getUserRewards(user);
		// THEN
		assertNotNull(userRewards);
		assertEquals(1, userRewards.size());
		assertNotNull(userRewards.get(0));
		assertEquals(expectedRewardPoints, userRewards.get(0).getRewardPoints());
	}

	@Test
	public void givenTooFarToAddReward_whenAddUserRewards_thenAddsNoReward() {
		// MOCK gpsUtil.getAttractions
		List<Attraction> givenAttractions = testHelperService.mockGpsUtilGetAttractions();
		// MOCK rewardCentral
		User user = new User(new UUID(11,12), "user_name", "user_phone", "user_email");
		Attraction closestAttraction = givenAttractions.get(0);
		// GIVEN user was close enough to the attraction
		rewardService.setProximityMaximalDistance(10); // statute miles
		double latitudeDifferenceMakingItTooFar = 0.15; // degrees
		Location location = new Location(closestAttraction.latitude - latitudeDifferenceMakingItTooFar, closestAttraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		// WHEN
		tourGuideService.addUserRewards(user);
		List<UserReward> userRewards = tourGuideService.getUserRewards(user);
		// THEN
		assertNotNull(userRewards);
		assertEquals(0, userRewards.size());
	}

	@Test
	public void givenAlreadyRewardedVisit_whenAddUserRewards_thenAddsNoReward() {
		// MOCK gpsUtil.getAttractions
		List<Attraction> givenAttractions = testHelperService.mockGpsUtilGetAttractions();
		// MOCK rewardCentral
		int expectedRewardPoints = 123;
		User user = new User(new UUID(11,12), "user_name", "user_phone", "user_email");
		Attraction expectedAttraction = givenAttractions.get(0);
		when(rewardCentral.getAttractionRewardPoints(eq(expectedAttraction.attractionId), eq(user.getUserId())))
			.thenReturn(expectedRewardPoints);
		// GIVEN user has already been rewarded for this attraction
		rewardService.setProximityMaximalDistance(10); // statute miles
		double latitudeDifferenceMakingItCloseEnough = 0.14; // degrees
		Location location = new Location(expectedAttraction.latitude - latitudeDifferenceMakingItCloseEnough, expectedAttraction.longitude);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		UserReward userReward = new UserReward(visitedLocation, expectedAttraction, expectedRewardPoints);
		user.addUserReward(userReward);
		// WHEN
		tourGuideService.addUserRewards(user);
		List<UserReward> userRewards = tourGuideService.getUserRewards(user);
		// THEN
		assertNotNull(userRewards);
		assertEquals(1, userRewards.size());
		assertNotNull(userRewards.get(0));
		assertEquals(expectedRewardPoints, userRewards.get(0).getRewardPoints());
	}

	@Test
	public void givenMaximalProximityBuffer_whenAddUserRewards_thenAddsRewardsForAllAttractions() {
		// MOCK gpsUtil.getAttractions
		List<Attraction> givenAttractions = testHelperService.mockGpsUtilGetAttractions();
		// MOCK rewardCentral
		int expectedRewardPoints = 123;
		User user = new User(new UUID(11,12), "user_name", "user_phone", "user_email");
		Attraction expectedAttraction = givenAttractions.get(0);
		when(rewardCentral.getAttractionRewardPoints(eq(expectedAttraction.attractionId), eq(user.getUserId())))
			.thenReturn(expectedRewardPoints);
		// GIVEN user is close enough to all attractions
		rewardService.setProximityMaximalDistance((Integer.MAX_VALUE/2 ) -1);
		Location location = new Location(0, 0);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(0));
		user.addToVisitedLocations(visitedLocation);
		// WHEN
		tourGuideService.addUserRewards(user);
		List<UserReward> userRewards = tourGuideService.getUserRewards(user);
		// THEN
		assertNotNull(userRewards);
		assertEquals(TestHelperService.NUMBER_OF_TEST_ATTRACTIONS, userRewards.size());
		assertNotNull(userRewards.get(0));
		assertEquals(expectedRewardPoints, userRewards.get(0).getRewardPoints());
	}

}

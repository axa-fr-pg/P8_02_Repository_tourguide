package tourguide.integration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import rewardCentral.RewardCentral;
import tourguide.api.TourGuideService;
import tourguide.gps.GpsService;
import tourguide.model.AttractionNearby;
import tourguide.model.LocationData;
import tourguide.model.ProviderData;
import tourguide.model.User;
import tourguide.model.UserPreferences;
import tourguide.model.VisitedLocationData;
import tourguide.reward.RewardService;
import tourguide.service.TestHelperService;
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
		doNothing().when(userService).initializeInternalUsers(any(Integer.class), any(Boolean.class));
	}
	
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
		List<ProviderData> duration4Providers = tourGuideService.getTripDeals(user);
		userPreferences.setTripDuration(2*duration);
		List<ProviderData> duration8Providers = tourGuideService.getTripDeals(user);
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
		List<ProviderData> providers1Child = tourGuideService.getTripDeals(user);
		userPreferences.setNumberOfChildren(2*children);
		List<ProviderData> providers2Children = tourGuideService.getTripDeals(user);
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
		Map<String,LocationData> allUserLocations = tourGuideService.getLastLocationAllUsers();
		// THEN
		assertNotNull(allUserLocations);
		assertEquals(givenUsers.size(), allUserLocations.size()); // CHECK LIST SIZE
		givenUsers.forEach( user -> {
			LocationData resultLocation = allUserLocations.get(user.getUserId().toString());
			assertNotNull(resultLocation);
			VisitedLocationData givenVisitedLocation = user.getLastVisitedLocation();
			assertNotNull(givenVisitedLocation);
			LocationData givenLocation = givenVisitedLocation.location;
			assertNotNull(givenLocation);
			assertEquals(givenLocation.latitude, resultLocation.latitude, 0.0000000001);
			assertEquals(givenLocation.longitude, resultLocation.longitude, 0.0000000001);
		});
	}
	
	@Test
	public void givenUserWithVisitedLocation_whenGetLastUserLocation_thenReturnsLastVisitedLocation() {
		// GIVEN mock GpsUtil
		User user = testHelperService.mockUserWithVisitedLocation(1, null);
		// WHEN
		VisitedLocationData resultLocation = tourGuideService.getLastUserLocation(user);
		// THEN
		assertNotNull(resultLocation);
		assertTrue(resultLocation.userId.equals(user.getUserId()));
		assertEquals(TestHelperService.LATITUDE_USER_ONE, resultLocation.location.latitude, 0.0000000001);
		assertEquals(TestHelperService.LONGITUDE_USER_ONE, resultLocation.location.longitude, 0.0000000001);
	}

	@Test
	public void givenUserWithoutVisitedLocation_whenGetLastUserLocation_thenReturnsCurrentLocation() {
		// GIVEN mock GpsUtil
		User user = testHelperService.mockUserWithoutVisitedLocation(1, null);
		// WHEN
		VisitedLocationData resultLocation = tourGuideService.getLastUserLocation(user);
		// THEN
		assertNotNull(resultLocation);
		assertTrue(resultLocation.userId.equals(user.getUserId()));
		assertEquals(TestHelperService.CURRENT_LATITUDE, resultLocation.location.latitude, 0.0000000001);
		assertEquals(TestHelperService.CURRENT_LONGITUDE, resultLocation.location.longitude, 0.0000000001);
	}
}

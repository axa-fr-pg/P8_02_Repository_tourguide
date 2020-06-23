package tourguide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourguide.helper.InternalTestHelper;
import tourguide.service.RewardsService;
import tourguide.service.TourGuideService;
import tourguide.user.User;
import tourguide.user.UserPreferences;
import tripPricer.Provider;
import tripPricer.TripPricer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestTourGuideService {

	@MockBean GpsUtil gpsUtil;
	@MockBean TripPricer tripPricer;
	@Autowired TourGuideService tourGuideService;  // TODO replace with interface

	@Test
	public void getUserLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}
	
	private Attraction newTestAttraction(int index) {
		return new Attraction("name"+index, "city"+index, "state"+index, -1 * index, index);
	}	
	
	@Test
	public void givenAttractions_whenGetNearByAttractions_thenCorrectListReturned() {
		// GIVEN
		VisitedLocation myVisitedLocation = new VisitedLocation(null, new Location(0, 0), null);
		List<Attraction> givenAttractions = new ArrayList<Attraction>();	
		int numberTestCases = TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS*2;
		for (int i=0; i<numberTestCases; i++) {
			givenAttractions.add(newTestAttraction(numberTestCases-i));
		}
		when(gpsUtil.getAttractions()).thenReturn(givenAttractions);
		// WHEN
		List<Attraction> resultAttractions = tourGuideService.getNearByAttractions(myVisitedLocation);
		// THEN
		assertNotNull(resultAttractions);
		assertEquals(TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS, resultAttractions.size());
		double resultCheckSum = 0;
		for (Attraction a : resultAttractions) {
			resultCheckSum += a.longitude;
		}
		int expectedCheckSum = ( TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS + 1)
				* TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS / 2;
		assertEquals(expectedCheckSum, resultCheckSum, 0.0000000001);
	}
	
	@Test
	public void givenDuration4Price_whenGetTripDealsForDuration8_thenReturnsDoublePrice() {
		// MOCK getAttractions
		List<Attraction> givenAttractions = new ArrayList<Attraction>();	
		int numberTestCases = TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS*2;
		for (int i=0; i<numberTestCases; i++) {
			givenAttractions.add(newTestAttraction(numberTestCases-i));
		}
		when(gpsUtil.getAttractions()).thenReturn(givenAttractions);
		// MOCK getPrice
		double priceForDuration4 = 1000;
		List<Provider> givenProvidersSimple = new ArrayList<Provider>();
		givenProvidersSimple.add(new Provider(null, "providerSimple", priceForDuration4));
		List<Provider> givenProvidersDouble = new ArrayList<Provider>();
		givenProvidersDouble.add(new Provider(null, "providerDouble", 2*priceForDuration4));
		int adults = 2;
		int children = 3;
		int duration = 4;
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), eq(duration), anyInt()))
			.thenReturn(givenProvidersSimple);
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), eq(2*duration), anyInt()))
			.thenReturn(givenProvidersDouble);
		// GIVEN
		User user = new User(new UUID(11,12), "test", "000", "test@tourguide.com");
		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setNumberOfAdults(adults);
		userPreferences.setNumberOfChildren(children);
		userPreferences.setTripDuration(duration);
		user.setUserPreferences(userPreferences);
		Location location = new Location(21,22);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date());
		user.addToVisitedLocations(visitedLocation);
		List<Provider> duration4Providers = tourGuideService.getTripDeals(user);
		// WHEN
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
		// MOCK getAttractions
		List<Attraction> givenAttractions = new ArrayList<Attraction>();	
		int numberTestCases = TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS*2;
		for (int i=0; i<numberTestCases; i++) {
			givenAttractions.add(newTestAttraction(numberTestCases-i));
		}
		when(gpsUtil.getAttractions()).thenReturn(givenAttractions);
		// MOCK getPrice
		double priceForOneChild = 100;
		List<Provider> givenProvidersSimple = new ArrayList<Provider>();
		givenProvidersSimple.add(new Provider(null, "providerSimple", priceForOneChild));
		List<Provider> givenProvidersDouble = new ArrayList<Provider>();
		givenProvidersDouble.add(new Provider(null, "providerDouble", 2*priceForOneChild));
		int adults = 0;
		int children = 1;
		int duration = 3;
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), eq(children), anyInt(), anyInt()))
			.thenReturn(givenProvidersSimple);
		when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), eq(2*children), anyInt(), anyInt()))
			.thenReturn(givenProvidersDouble);
		// GIVEN
		User user = new User(new UUID(11,12), "test", "000", "test@tourguide.com");
		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setNumberOfAdults(adults);
		userPreferences.setNumberOfChildren(children);
		userPreferences.setTripDuration(duration);
		user.setUserPreferences(userPreferences);
		Location location = new Location(21,22);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date());
		user.addToVisitedLocations(visitedLocation);
		List<Provider> providers1Child = tourGuideService.getTripDeals(user);
		// WHEN
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
}

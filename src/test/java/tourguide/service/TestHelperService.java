package tourguide.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourguide.api.TourGuideService;
import tourguide.model.AttractionData;
import tourguide.model.LocationData;
import tourguide.model.User;
import tourguide.model.UserPreferences;
import tourguide.model.VisitedLocationData;
import tourguide.user.UserService;

@Service
public class TestHelperService {

	public final static int NUMBER_OF_TEST_ATTRACTIONS = TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS*2;
	public final static double LATITUDE_USER_ONE = 0.21;
	public final static double LONGITUDE_USER_ONE = -0.00022;
	public final static double LATITUDE_ATTRACTION_ONE = 0.31;
	public final static double LONGITUDE_ATTRACTION_ONE = -0.00032;
	public final static double CURRENT_LATITUDE = 0.111;
	public final static double CURRENT_LONGITUDE = -0.222;

	@Autowired GpsUtil gpsUtil;
	@Autowired UserService userService;

	public  List<User> mockGetAllUsersAndLocations(int numberOfUsers) {
		List<User> givenUsers = new ArrayList<User>();
		for (int i=0; i<numberOfUsers; i++) {
			givenUsers.add(mockUserServiceGetUserAndGpsUtilGetUserLocation(i+1, null));
		}
		when(userService.getAllUsers()).thenReturn(givenUsers);
		return givenUsers;
	}

	public User mockGpsUtilGetUserLocation(int index) {
		User user = new User(new UUID(11*index,12*index), "name"+index, "phone"+index, "email"+index);
		Location location = new Location(LATITUDE_USER_ONE*index,LONGITUDE_USER_ONE*index);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(index));
		when(gpsUtil.getUserLocation(user.getUserId())).thenReturn(visitedLocation);
		return user;
	}
	
	public User mockUserServiceGetUserAndGpsUtilGetUserLocation(int index, UserPreferences userPreferences) {
		User user = mockUserWithVisitedLocation(index, userPreferences);
		VisitedLocationData userLastVisitedLocationData = user.getLastVisitedLocation();
		Location expectedLocation = new Location( userLastVisitedLocationData.location.latitude,
				userLastVisitedLocationData.location.longitude);
		VisitedLocation expectedVisitedLocation = new VisitedLocation( userLastVisitedLocationData.userId,
				expectedLocation, userLastVisitedLocationData.timeVisited);
		when(userService.getUser(user.getUserName())).thenReturn(user);
		when(gpsUtil.getUserLocation(user.getUserId())).thenReturn(expectedVisitedLocation);
		return user;
	}
	
	public User mockUserWithVisitedLocation(int index, UserPreferences userPreferences) {
		User user = mockUserWithoutVisitedLocation(index, userPreferences);
		LocationData location = new LocationData(LATITUDE_USER_ONE*index,LONGITUDE_USER_ONE*index);
		VisitedLocationData visitedLocation = new VisitedLocationData(user.getUserId(), location, new Date(index));
		user.addToVisitedLocations(visitedLocation);
		return user;
	}
	
	public User mockUserWithoutVisitedLocation(int index, UserPreferences userPreferences) {
		User user = new User(new UUID(11*index,12*index), "name"+index, "phone"+index, "email"+index);
		user.setUserPreferences(userPreferences);
		Location currentLocation = new Location(CURRENT_LATITUDE, CURRENT_LONGITUDE);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), currentLocation, new Date());
		when(gpsUtil.getUserLocation(user.getUserId())).thenReturn(visitedLocation);
		return user;
	}
	
	public List<AttractionData> mockGpsUtilGetAttractions() {
		return mockGpsUtilGetAttractions(NUMBER_OF_TEST_ATTRACTIONS);
	}

	public List<AttractionData> mockGpsUtilGetAttractions(int numberOfTestAttractions) {
		List<Attraction> givenAttractions = new ArrayList<Attraction>();	
		List<AttractionData> responseAttractions = new ArrayList<AttractionData>();	
		for (int i=0; i<numberOfTestAttractions; i++) {
			int index = numberOfTestAttractions - i;
			Attraction attraction = new Attraction("name"+index, "city"+index, "state"+index, 
					LATITUDE_ATTRACTION_ONE*index, LONGITUDE_ATTRACTION_ONE*index);	
			givenAttractions.add(attraction);
			responseAttractions.add(newAttractionData(attraction));
		}
		when(gpsUtil.getAttractions()).thenReturn(givenAttractions);
		return responseAttractions;
	}
	
	private AttractionData newAttractionData(Attraction attraction) {
		return new AttractionData(attraction.attractionName, attraction.city, attraction.state, attraction.latitude, attraction.longitude);
	}
}

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
import tourguide.model.User;
import tourguide.model.UserPreferences;
import tourguide.user.UserService;

@Service
public class TestHelperService {

	public static int NUMBER_OF_TEST_ATTRACTIONS = TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS*2;
	public static double latitudeUserOne = 0.21;
	public static double longitudeUserOne = -0.22;
	public static double latitudeAttractionOne = 0.31;
	public static double longitudeAttractionOne = -0.32;

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

	public User mockUserServiceGetUserAndGpsUtilGetUserLocation(int index, UserPreferences userPreferences) {
		User user = new User(new UUID(11*index,12*index), "name"+index, "phone"+index, "email"+index);
		Location location = new Location(latitudeUserOne*index,longitudeUserOne*index);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, new Date(index));
		user.addToVisitedLocations(visitedLocation);
		user.setUserPreferences(userPreferences);
		when(userService.getUser(user.getUserName())).thenReturn(user);
		when(gpsUtil.getUserLocation(user.getUserId())).thenReturn(visitedLocation);
		return user;
	}
	
	public List<Attraction> mockGpsUtilGetAttractions() {
		List<Attraction> givenAttractions = new ArrayList<Attraction>();	
		for (int i=0; i<NUMBER_OF_TEST_ATTRACTIONS; i++) {
			int index = NUMBER_OF_TEST_ATTRACTIONS - i;
			Attraction attraction = new Attraction("name"+index, "city"+index, "state"+index, 
					latitudeAttractionOne*index, longitudeAttractionOne*index);
			givenAttractions.add(attraction);
		}
		when(gpsUtil.getAttractions()).thenReturn(givenAttractions);
		return givenAttractions;
	}
	
}

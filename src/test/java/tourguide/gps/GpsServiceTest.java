package tourguide.gps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourguide.gps.GpsService;
import tourguide.model.User;
import tourguide.service.TestHelperService;
import tourguide.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GpsServiceTest {

	@MockBean GpsUtil gpsUtil;
	@MockBean UserService userService;
	@Autowired TestHelperService testHelperService;
	@Autowired GpsService gpsService;

	@Test
	public void givenUser_whenTrackUserLocation_thenLocationAddedToUserHistory() {
		// GIVEN mock GpsUtil
		User user = testHelperService.mockUserServiceGetUserAndGpsUtilGetUserLocation(1, null);
		// WHEN
		VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
		// THEN
		assertEquals(2, user.getVisitedLocations().size()); // Current location has been added to the list
		assertNotNull(visitedLocation);
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
		assertEquals(visitedLocation.location.latitude, user.getLastVisitedLocation().location.latitude, 0.0000000001);
		assertEquals(visitedLocation.location.longitude, user.getLastVisitedLocation().location.longitude, 0.0000000001);
	}

	@Test
	public void givenUserWithVisitedLocation_whenGetUserLocation_thenReturnsCorrectLocation() {
		// GIVEN mock GpsUtil
		User user = testHelperService.mockUserServiceGetUserAndGpsUtilGetUserLocation(1, null);
		// WHEN
		VisitedLocation visitedLocation = gpsService.getUserLocation(user);
		// THEN
		assertEquals(1, user.getVisitedLocations().size()); // Current location has not been added to the list
		assertNotNull(visitedLocation);
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
		assertEquals(visitedLocation.location.latitude, user.getLastVisitedLocation().location.latitude, 0.0000000001);
		assertEquals(visitedLocation.location.longitude, user.getLastVisitedLocation().location.longitude, 0.0000000001);
	}

	@Test
	public void givenUserWithoutVisitedLocation_whenGetUserLocation_thenReturnsCorrectLocation() {
		// GIVEN mock GpsUtil
		User user = testHelperService.mockUserServiceGetUserAndGpsUtilGetUserLocation(1, null);
		user.clearVisitedLocations();
		// WHEN
		VisitedLocation visitedLocation = gpsService.getUserLocation(user);
		// THEN
		assertEquals(1, user.getVisitedLocations().size()); // Current location has been added to the list
		assertNotNull(visitedLocation);
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void givenUserList_whenGetAllUserLocations_thenReturnsCorrectList() {
		// GIVEN mock getAllUsers
		List<User> givenUsers = new ArrayList<User>();
		int numberOfUsers = 5;
		for (int i=0; i<numberOfUsers; i++) {
			givenUsers.add(testHelperService.mockUserServiceGetUserAndGpsUtilGetUserLocation(i+1, null));
		}
		when(userService.getAllUsers()).thenReturn(givenUsers);
		// WHEN
		Map<UUID,Location> allUserLocations = gpsService.getAllUserLocations(givenUsers);
		// THEN
		assertNotNull(allUserLocations);
		assertEquals(givenUsers.size(), allUserLocations.size()); // CHECK LIST SIZE
		User givenUser = givenUsers.get(0);
		assertNotNull(givenUser);
		assertNotNull(givenUser.getUserId());
		Location resultLocation = allUserLocations.get(givenUser.getUserId());
		assertNotNull(resultLocation);
		VisitedLocation givenVisitedLocation = givenUser.getLastVisitedLocation();
		assertNotNull(givenVisitedLocation);
		Location givenLocation = givenVisitedLocation.location;
		assertNotNull(givenLocation);
		assertEquals(givenLocation.latitude, resultLocation.latitude, 0.0000000001); // CHECK LOCATION FOR FIRST GIVEN USER
		assertEquals(givenLocation.longitude, resultLocation.longitude, 0.0000000001); // CHECK LOCATION FOR FIRST GIVEN USER
	}	
}
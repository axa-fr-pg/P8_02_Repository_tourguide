package tourguide.gps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import gpsUtil.location.VisitedLocation;
import tourguide.gps.GpsService;
import tourguide.model.LocationData;
import tourguide.model.User;
import tourguide.model.VisitedLocationData;
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
	public void givenUser_whenGetCurrentUserLocation_thenReturnsCurrentLocation() {
		// GIVEN mock GpsUtil
		User user = testHelperService.mockUserWithoutVisitedLocation(1, null);
		// WHEN
		VisitedLocationData resultLocation = gpsService.getCurrentUserLocation(user.getUserId().toString());
		// THEN
		assertNotNull(resultLocation);
		assertTrue(resultLocation.userId.equals(user.getUserId()));
		assertEquals(TestHelperService.CURRENT_LATITUDE, resultLocation.location.latitude, 0.0000000001);
		assertEquals(TestHelperService.CURRENT_LONGITUDE, resultLocation.location.longitude, 0.0000000001);
	}

	@Test
	public void givenUserList_whenTrackAllUserLocations_thenAddsVisitedLocationToAllUsers() {
		// GIVEN mock getAllUsers
		List<User> givenUsers = testHelperService.mockGetAllUsersAndLocations(5);
		for (User user : givenUsers) {
			assertNotNull(user);
			assertNotNull(user.getVisitedLocations());
			assertEquals(1, user.getVisitedLocations().size());
		}
		// WHEN
		gpsService.trackAllUserLocations(givenUsers);
		// THEN
		for (User user : givenUsers) {
			assertNotNull(user);
			assertNotNull(user.getVisitedLocations());
			assertEquals(2, user.getVisitedLocations().size());
		}
	}
}
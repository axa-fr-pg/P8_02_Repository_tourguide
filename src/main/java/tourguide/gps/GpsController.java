package tourguide.gps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tourguide.model.AttractionData;
import tourguide.model.LocationData;
import tourguide.model.User;
import tourguide.model.VisitedLocationData;

@RestController
public class GpsController {

	private Logger logger = LoggerFactory.getLogger(GpsController.class);
	@Autowired private GpsService gpsService;

	@PatchMapping("/trackAllUserLocations")
	public List<User> trackAllUserLocations(@RequestBody List<User> userList) {
		logger.debug("trackAllUserLocations with list of size = " + userList.size());
		return gpsService.trackAllUserLocations(userList);
	}
	
	public List<AttractionData> getAllAttractions() {
		logger.debug("getAllAttractions");
		return gpsService.getAllAttractions();
	}

	public VisitedLocationData getLastUserLocation(User user) {
		logger.debug("getLastUserLocation for User " + user.getUserName());
		return gpsService.getLastUserLocation(user);
	}

	public Map<UUID, LocationData> getLastUsersLocations(List<User> allUsers) {
		logger.debug("getLastUsersLocations for List of size " + allUsers.size());
		return gpsService.getLastUsersLocations(allUsers);
	}
}

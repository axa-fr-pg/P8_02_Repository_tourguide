package tourguide.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.gps.GpsController;
import tourguide.model.AttractionData;
import tourguide.model.LocationData;
import tourguide.model.User;
import tourguide.model.VisitedLocationData;

@Service
public class GpsRequest {

	private Logger logger = LoggerFactory.getLogger(GpsRequest.class);
	@Autowired private GpsController gpsController;

	public void trackAllUserLocations(List<User> userList) {
		logger.debug("trackAllUserLocations with list of size = " + userList.size());
		gpsController.trackAllUserLocations(userList);
	}

	public List<AttractionData> getAllAttractions() {
		logger.debug("getAllAttractions");
		return gpsController.getAllAttractions();
	}

	public VisitedLocationData getLastUserLocation(User user) {
		logger.debug("getLastUserLocation");
		return gpsController.getLastUserLocation(user);
	}

	public Map<UUID, LocationData> getLastUsersLocations(List<User> allUsers) {
		logger.debug("getLastUsersLocations");
		return gpsController.getLastUsersLocations(allUsers);
	}
}

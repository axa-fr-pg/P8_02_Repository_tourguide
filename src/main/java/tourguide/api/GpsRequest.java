package tourguide.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.gps.GpsController;
import tourguide.model.AttractionData;
import tourguide.model.User;

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

}

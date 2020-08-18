package tourguide.gps;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.model.AttractionData;
import tourguide.model.User;

@Service
public class GpsController {

	private Logger logger = LoggerFactory.getLogger(GpsController.class);
	@Autowired private GpsService gpsService;

	public void trackAllUserLocations(List<User> userList) {
		logger.debug("trackAllUserLocations with list of size = " + userList.size());
		gpsService.trackAllUserLocations(userList);
	}
	
	public List<AttractionData> getAllAttractions() {
		logger.debug("getAllAttractions");
		return gpsService.getAllAttractions();
	}
}

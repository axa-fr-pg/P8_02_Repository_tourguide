package tourguide.gps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourguide.model.AttractionData;
import tourguide.model.User;

@Service
public class GpsService {

	private Logger logger = LoggerFactory.getLogger(GpsService.class);
	@Autowired private GpsUtil gpsUtil;

	public void trackAllUserLocations(List<User> userList) {
		logger.debug("trackAllUserLocations with list of size = " + userList.size());
		userList.stream().parallel().forEach(user -> {
			VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
			user.addToVisitedLocations(visitedLocation);
		});
	}

	public VisitedLocation getUserLocation(User user) {
		logger.debug("getUserLocation with userName = " + user.getUserName());
		return gpsUtil.getUserLocation(user.getUserId());
	}
	
	public VisitedLocation getLastUserLocation(User user) {
		logger.debug("getLastUserLocation with userName = " + user.getUserName());
		if (user.getVisitedLocations().size() > 0) {
			return user.getLastVisitedLocation();
		}
		return getUserLocation(user);
	}
	
	public Map<UUID,Location> getLastUsersLocations(List<User> userList) {
		logger.debug("getLastUsersLocations with list of size = " + userList.size());
		Map<UUID,Location> userLocations = new HashMap<UUID,Location>();
		userList.stream().parallel().forEach(user -> {
			userLocations.put(user.getUserId(), getLastUserLocation(user).location);
		});
		return userLocations;
	}
	
	public List<AttractionData> getAllAttractions() {
		logger.debug("getAllAttractions");
		List<AttractionData> dataList = new ArrayList<AttractionData>();
		gpsUtil.getAttractions().stream().forEach(attraction -> {
			AttractionData data = new AttractionData();
			data.name = attraction.attractionName;
			data.city = attraction.city;
			data.state = attraction.state;
			data.latitude = attraction.latitude;
			data.longitude = attraction.longitude;
			dataList.add(data);
		});
		return dataList;
	}
}

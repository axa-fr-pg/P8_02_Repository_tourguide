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
import tourguide.model.LocationData;
import tourguide.model.User;
import tourguide.model.VisitedLocationData;

@Service
public class GpsService {

	private Logger logger = LoggerFactory.getLogger(GpsService.class);
	@Autowired private GpsUtil gpsUtil;

	public List<User> trackAllUserLocations(List<User> userList) {
		logger.debug("trackAllUserLocations with list of size = " + userList.size());
		userList.stream().parallel().forEach(user -> {
			VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
			user.addToVisitedLocations(VisitedLocationData.newVisitedLocationData(visitedLocation));
		});
		return userList;
	}

	public VisitedLocationData getCurrentUserLocation(String userIdString) {
		logger.debug("getUserLocation with userId = " + userIdString);
		UUID userId = UUID.fromString(userIdString);
		return VisitedLocationData.newVisitedLocationData(gpsUtil.getUserLocation(userId));
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

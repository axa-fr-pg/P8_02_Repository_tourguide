package tourguide.api;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tourguide.model.AttractionData;
import tourguide.model.User;
import tourguide.model.VisitedLocationData;
import tourguide.user.UserService;

@Service
public class GpsRequest {
	
	private final GpsClient gpsClient;
	private Logger logger = LoggerFactory.getLogger(GpsRequest.class);
	@Autowired private ObjectMapper objectMapper;
	@Autowired private UserService userService;
	
	public GpsRequest(GpsClient gpsClient) {
		this.gpsClient = gpsClient;
	}

	public List<User> trackAllUserLocations(List<User> userList) {
		logListContent("trackAllUserLocations  before external call", userList);
		List<User> updatedUserList = gpsClient.trackAllUserLocations(userList);
		logListContent("trackAllUserLocations after external call", updatedUserList);
		userService.setAllUsers(updatedUserList);
		return updatedUserList;
	}

	public List<AttractionData> getAllAttractions() {
		logger.debug("getAllAttractions before external call");
		List<AttractionData> attractions = gpsClient.getAllAttractions();
		logListContent("getAllAttractions after external call", attractions);
		return attractions;
	}

	public VisitedLocationData getCurrentUserLocation(User user) {
		logger.debug("getCurrentUserLocation before external call for User " + user.getUserName());
		VisitedLocationData visitedLocation = gpsClient.getCurrentUserLocation(user.getUserId().toString());
		logListContent("getCurrentUserLocation after external call", Collections.singletonList(visitedLocation));
		return visitedLocation;
	}

	private void logListContent(String methodName, List<?> list) {
		logger.debug(methodName + " number of elements " + list.size() + " : " + list.toString());
		try {
			logger.debug(methodName + " content details : " + objectMapper.writeValueAsString(list));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("logListContent catched a JsonProcessingException");
		}
	}
}

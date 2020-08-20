package tourguide.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tourguide.gps.GpsController;
import tourguide.model.AttractionData;
import tourguide.model.LocationData;
import tourguide.model.User;
import tourguide.model.VisitedLocationData;
import tourguide.user.UserService;

@Service
public class GpsRequest {
	
	private final GpsCaller gpsCaller;
	private Logger logger = LoggerFactory.getLogger(GpsRequest.class);
	@Autowired private GpsController gpsController;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private UserService userService;
	
	public GpsRequest(GpsCaller gpsCaller) {
		this.gpsCaller = gpsCaller;
	}

	public List<User> trackAllUserLocations(List<User> userList) {
		logger.debug("trackAllUserLocations before external call");
		logListContent(userList);
		List<User> updatedUserList = gpsCaller.trackAllUserLocations(userList);
		logger.debug("trackAllUserLocations after external call");
		logListContent(updatedUserList);
		userService.setAllUsers(updatedUserList);
		return updatedUserList;
	}

	public List<AttractionData> getAllAttractions() {
		logger.debug("getAllAttractions");
		return gpsController.getAllAttractions();
	}

	public VisitedLocationData getLastUserLocation(User user) {
		logger.debug("getLastUserLocation for User " + user.getUserName());
		return gpsController.getLastUserLocation(user);
	}

	public Map<UUID, LocationData> getLastUsersLocations(List<User> allUsers) {
		logger.debug("getLastUsersLocations for List of size " + allUsers.size());
		return gpsController.getLastUsersLocations(allUsers);
	}
	
	private void logListContent(List<?> list) {
		logger.debug("logListContent size " + list.size() + " : " + list.toString());
		try {
			logger.debug("logListContent details : " + objectMapper.writeValueAsString(list));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("logListContent catched a JsonProcessingException");
		}
	}
}

package tripmaster.tourguide.api;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.user.User;
import tripmaster.tourguide.user.UserService;

/**
 * Class to prepare a gps API request. Implements GpsRequestService interface.
 * @see tripmaster.tourguide.api.GpsClient
 * @see tripmaster.tourguide.api.GpsRequestService
 */
@Service
public class GpsRequestServiceImpl implements GpsRequestService {
	
	private final GpsClient gpsClient;
	private Logger logger = LoggerFactory.getLogger(GpsRequestServiceImpl.class);
	@Autowired private ObjectMapper objectMapper;
	@Autowired private UserService userService;
	
	public GpsRequestServiceImpl(GpsClient gpsClient) {
		this.gpsClient = gpsClient;
	}

	/**
	 * Gets the current location and stores it into the visited location history for each user of a given list.
	 * @param userList for which the tracking shall be processed.
	 * @return List of users updated with added visited locations.
	 */
	@Override
	public List<User> trackAllUserLocations(List<User> userList) {
		logListContent("trackAllUserLocations  before external call", userList);
		List<User> updatedUserList = gpsClient.trackAllUserLocations(userList);
		logListContent("trackAllUserLocations after external call", updatedUserList);
		userService.setAllUsers(updatedUserList);
		return updatedUserList;
	}

	/**
	 * Gets the list of all known attractions in the ecosystem.
	 * @return List of AttractionData containing one entry for each existing attraction.
	 */
	@Override
	public List<AttractionData> getAllAttractions() {
		logger.debug("getAllAttractions before external call");
		List<AttractionData> attractions = gpsClient.getAllAttractions();
		logListContent("getAllAttractions after external call", attractions);
		return attractions;
	}

	/**
	 * Gets current user location.
	 * @param user for whom the location shall be determined.
	 * @return VisitedLocationData containing the current location of the user.
	 */
	@Override
	public VisitedLocationData getCurrentUserLocation(User user) {
		logger.debug("getCurrentUserLocation before external call for User " + user.userName);
		VisitedLocationData visitedLocation = gpsClient.getCurrentUserLocation(user.userId.toString());
		logListContent("getCurrentUserLocation after external call", Collections.singletonList(visitedLocation));
		return visitedLocation;
	}

	// logging helper tool
	private void logListContent(String methodName, List<?> list) {
		logger.debug(methodName + " number of elements " + list.size() + " : " + list.toString());
		try {
			logger.debug(methodName + " content details : " + objectMapper.writeValueAsString(list));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("logListContent catched a JsonProcessingException");
		}
	}
}

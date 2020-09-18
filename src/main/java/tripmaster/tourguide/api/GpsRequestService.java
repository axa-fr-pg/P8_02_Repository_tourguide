package tripmaster.tourguide.api;

import java.util.List;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.user.User;

/**
 * Interface to prepare a gps API request.
 * @see tripmaster.tourguide.api.GpsClient
 * @see tripmaster.tourguide.api.GpsRequestServiceImpl
 */
public interface GpsRequestService {

	List<User> trackAllUserLocations(List<User> userList);

	List<AttractionData> getAllAttractions();

	VisitedLocationData getCurrentUserLocation(User user);

}
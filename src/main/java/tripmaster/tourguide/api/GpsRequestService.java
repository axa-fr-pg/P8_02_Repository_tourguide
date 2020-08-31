package tripmaster.tourguide.api;

import java.util.List;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.user.User;


public interface GpsRequestService {

	List<User> trackAllUserLocations(List<User> userList);

	List<AttractionData> getAllAttractions();

	VisitedLocationData getCurrentUserLocation(User user);

}
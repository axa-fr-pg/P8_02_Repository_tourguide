package tourguide.api;

import java.util.List;

import tourguide.model.AttractionData;
import tourguide.model.User;
import tourguide.model.VisitedLocationData;

public interface GpsRequestService {

	List<User> trackAllUserLocations(List<User> userList);

	List<AttractionData> getAllAttractions();

	VisitedLocationData getCurrentUserLocation(User user);

}
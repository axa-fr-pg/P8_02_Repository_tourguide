package tourguide.gps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourguide.model.User;

@Service
public class GpsService {

	@Autowired private GpsUtil gpsUtil;

	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		return visitedLocation;
	}

	public void trackAllUserLocations(List<User> userList) {
		userList.stream().forEach(user -> {
			VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
			user.addToVisitedLocations(visitedLocation);
		});
	}

	public VisitedLocation getUserLocation(User user) {
		return gpsUtil.getUserLocation(user.getUserId());
	}
	
	public VisitedLocation getLastUserLocation(User user) {
		if (user.getVisitedLocations().size() > 0) {
			return user.getLastVisitedLocation();
		}
		return getUserLocation(user);
	}
	
	public Map<UUID,Location> getLastUsersLocations(List<User> userList) {
		Map<UUID,Location> userLocations = new HashMap<UUID,Location>();
		for (User u : userList) {
			userLocations.put(u.getUserId(), getLastUserLocation(u).location);
		}
		return userLocations;
	}
	
	public List<Attraction> getAllAttractions() {
		return gpsUtil.getAttractions();
	}
}

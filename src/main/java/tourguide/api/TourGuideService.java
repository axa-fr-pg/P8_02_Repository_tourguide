package tourguide.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.model.AttractionData;
import tourguide.model.AttractionDistance;
import tourguide.model.AttractionNearby;
import tourguide.model.LocationData;
import tourguide.model.ProviderData;
import tourguide.model.User;
import tourguide.model.UserReward;
import tourguide.model.VisitedLocationData;

import tourguide.trip.TripService;
import tourguide.user.UserService;

@Service
public class TourGuideService {
	
	public static final int NUMBER_OF_PROPOSED_ATTRACTIONS = 5;
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	@Autowired private GpsRequest gpsRequest;
	@Autowired private RewardRequest rewardRequest;
	@Autowired private TripService tripService;
	@Autowired private UserService userService;
	
	public TourGuideService() {
	}
	
	public List<UserReward> getUserRewards(User user) {
		logger.debug("getUserRewards userName = " + user.getUserName());
		return user.getUserRewards();
	}
	
	public Map<String,LocationData> getLastLocationAllUsers() {
		logger.debug("getLastLocationAllUsers");
		// Get all users within the application
		List<User> allUsers = userService.getAllUsers();
		// Get visited locations for all of them
		Map<UUID,LocationData> allUserLocationsWithUUID = gpsRequest.getLastUsersLocations(allUsers);
		// Change the key of the map to match the String format requirement
		Map<String,LocationData> allUserLocations = allUserLocationsWithUUID.entrySet().stream().collect(Collectors.toMap(
				entry -> entry.getKey().toString(),
				entry -> entry.getValue()
		));
		return allUserLocations;
	}
	
	public List<ProviderData> getTripDeals(User user) {
		logger.debug("getTripDeals userName = " + user.getUserName());
		// Calculate the sum of all reward points for given user
		int cumulativeRewardPoints = rewardRequest.sumOfAllRewardPoints(user);
		// List attractions in the neighborhood of the user
		List<AttractionNearby> attractions = getNearByAttractions(user.getUserName());		
		// Calculate trip proposals matching attractions list, user preferences and reward points 
		return tripService.calculateProposals( user, attractions, cumulativeRewardPoints);
	}
	
	public List<AttractionNearby> getNearByAttractions(String userName) {		
		logger.debug("getNearByAttractions userName = " + userName);
		// Prepare user location as reference to measure attraction distance
		User user = userService.getUser(userName);
    	VisitedLocationData visitedLocation = gpsRequest.getLastUserLocation(user);
		LocationData fromLocation = visitedLocation.location;
		// Prepare list of all attractions to be sorted
		List<AttractionDistance> fullList = new ArrayList<>();
		for(AttractionData toAttraction : gpsRequest.getAllAttractions()) {
			AttractionDistance ad = new AttractionDistance(fromLocation, toAttraction);
			fullList.add(ad);
		}
		// Sort list
		fullList.sort(null);		
		// Keep the selection
		List<AttractionNearby> nearbyAttractions = new ArrayList<>();
		for (int i=0; i<NUMBER_OF_PROPOSED_ATTRACTIONS && i<fullList.size(); i++) {
			AttractionData attraction = fullList.get(i);
			int rewardPoints = rewardRequest.getRewardPoints(attraction, user);
			AttractionNearby nearbyAttraction = new AttractionNearby(attraction, user, rewardPoints);
			nearbyAttractions.add(nearbyAttraction);
		}
		return nearbyAttractions;
	}	
}

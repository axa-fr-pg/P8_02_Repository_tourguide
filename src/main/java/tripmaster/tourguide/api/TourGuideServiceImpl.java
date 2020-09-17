package tripmaster.tourguide.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.attraction.AttractionDistance;
import tripmaster.common.attraction.AttractionNearby;
import tripmaster.common.location.LocationData;
import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.trip.ProviderData;
import tripmaster.common.user.User;
import tripmaster.common.user.UserReward;
import tripmaster.tourguide.user.UserService;

@Service
public class TourGuideServiceImpl implements TourGuideService {
	
	private Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);
	@Autowired private GpsRequestService gpsRequest;
	@Autowired private RewardRequestService rewardRequest;
	@Autowired private TripRequestService tripRequest;
	@Autowired private UserService userService;
	
	public TourGuideServiceImpl() {
	}
	
	@Override
	public List<UserReward> getUserRewards(User user) {
		logger.debug("getUserRewards userName = " + user.userName);
		return user.getUserRewards();
	}
	
	@Override
	public VisitedLocationData getLastUserLocation(User user) {
		logger.debug("getLastUserLocation with userName = " + user.userName);
		if (user.getVisitedLocations().size() > 0) {
			return user.getLastVisitedLocation();
		}
		return gpsRequest.getCurrentUserLocation(user);
	}
	
	@Override
	public Map<String,LocationData> getLastLocationAllUsers() {
		logger.debug("getLastLocationAllUsers");
		// Get all users within the application
		List<User> allUsers = userService.getAllUsers();
		Map<String,LocationData> allUserLocationsMap = new HashMap<String,LocationData>();
		// Get visited locations for all of them
		allUsers.forEach(user -> {
			allUserLocationsMap.put(user.userId.toString(), getLastUserLocation(user).location);
		});
		return allUserLocationsMap;
	}
	
	@Override
	public List<ProviderData> getTripDeals(User user) {
		logger.debug("getTripDeals userName = " + user.userName);
		// Calculate the sum of all reward points for given user
		int cumulativeRewardPoints = rewardRequest.sumOfAllRewardPoints(user);
		// List attractions in the neighborhood of the user
		List<AttractionNearby> attractions = getNearbyAttractions(user.userName);		
		// Calculate trip proposals matching attractions list, user preferences and reward points 
		return tripRequest.calculateProposals( user, attractions, cumulativeRewardPoints);
	}
	
	@Override
	public List<AttractionNearby> getNearbyAttractions(String userName) {		
		logger.debug("getNearbyAttractions userName = " + userName);
		// Prepare user location as reference to measure attraction distance
		User user = userService.getUser(userName);
    	VisitedLocationData visitedLocation = getLastUserLocation(user);
		LocationData fromLocation = visitedLocation.location;
		// Prepare list of all attractions to be sorted
		List<AttractionDistance> fullList = new ArrayList<>();
		for(AttractionData toAttraction : gpsRequest.getAllAttractions()) {
			AttractionDistance ad = new AttractionDistance(fromLocation,toAttraction);
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

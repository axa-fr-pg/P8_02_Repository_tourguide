package tourguide.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourguide.gps.GpsService;
import tourguide.model.AttractionDistance;
import tourguide.model.AttractionNearby;
import tourguide.model.User;
import tourguide.model.UserReward;
import tourguide.reward.RewardService;
import tourguide.tracker.TrackerService;
import tourguide.trip.TripService;
import tourguide.user.UserService;
import tripPricer.Provider;

@Service
public class TourGuideService {
	
	public static final int NUMBER_OF_PROPOSED_ATTRACTIONS = 5;
	Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	@Autowired private GpsUtil gpsUtil;
	@Autowired private GpsService gpsService;
	@Autowired private RewardService rewardService;
	@Autowired private TripService tripService;
	@Autowired private TrackerService tracker;
	@Autowired private UserService userService;
	
	public TourGuideService() {
		addShutDownHook(); // WHY ???
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocationAndCalculateRewards(user);
		return visitedLocation;
	}
	
	public Map<String,Location> getAllUserLastLocations() {
		// Get all users within the application
		List<User> allUsers = userService.getAllUsers();
		// Get visited locations for all of them
		Map<String,Location> allUserLocations = new HashMap<String,Location>();
		for (User u : allUsers) {
			allUserLocations.put(u.getUserId().toString(), getUserLocation(u).location);
		}
		return allUserLocations;
	}
	
	public List<Provider> getTripDeals(User user) {
		// Calculate the sum of all reward points for given user
		int cumulativeRewardPoints = rewardService.sumOfAllRewardPoints(user);
		// List attractions in the neighborhood of the user
		List<AttractionNearby> attractions = getNearByAttractions(user.getUserName());		
		// Calculate trip proposals matching attractions list, user preferences and reward points 
		return tripService.calculateProposals( user, attractions, cumulativeRewardPoints);
	}
	
	public VisitedLocation trackUserLocationAndCalculateRewards(User user) {
		// Get current user location and register it for given user
		VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
		// Update rewards for given user
		calculateRewards(user);
		return visitedLocation;
	}

	public List<AttractionNearby> getNearByAttractions(String userName) {		
		// Prepare user location as reference to measure attraction distance
		User user = userService.getUser(userName);
    	VisitedLocation visitedLocation = getUserLocation(user);
		Location fromLocation = visitedLocation.location;
		// Prepare list of all attractions to be sorted
		List<AttractionDistance> fullList = new ArrayList<>();
		for(Attraction toAttraction : gpsUtil.getAttractions()) {
			AttractionDistance ad = new AttractionDistance(fromLocation, toAttraction);
			fullList.add(ad);
		}
		// Sort list
		fullList.sort(null);		
		// Keep the selection
		List<AttractionNearby> nearbyAttractions = new ArrayList<>();
		for (int i=0; i<NUMBER_OF_PROPOSED_ATTRACTIONS && i<fullList.size(); i++) {
			Attraction attraction = fullList.get(i);
			int rewardPoints = rewardService.getRewardPoints(attraction, user);
			AttractionNearby nearbyAttraction = new AttractionNearby(attraction, user, rewardPoints);
			nearbyAttractions.add(nearbyAttraction);
		}
		return nearbyAttractions;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}

	public void calculateRewards(User user) {
		// Get all existing attractions within the application
		List<Attraction> attractions = gpsUtil.getAttractions();
		// Add all new rewards for given combination of user, visited locations and existing attractions
		rewardService.addAllNewRewards(user, attractions);
	}

}

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
import tourguide.model.AttractionDistance;
import tourguide.model.AttractionNearby;
import tourguide.tracker.Tracker;
import tourguide.user.User;
import tourguide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	
	public static final String tripPricerApiKey = "test-server-api-key";
	public static final int NUMBER_OF_PROPOSED_ATTRACTIONS = 5;
	Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	@Autowired private GpsUtil gpsUtil; // TODO interface
	@Autowired private RewardsService rewardsService; // TODO interface
	@Autowired private TripPricer tripPricer; // TODO interface
	@Autowired private Tracker tracker; // TODO interface
	@Autowired private UserService userService; // TODO interface
	
	public TourGuideService() {
		// addShutDownHook(); WHY ???
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	public Map<String,Location> getAllUserLocations() {
		Map<String,Location> allUserLocations = new HashMap<String,Location>();
		List<User> allUsers = userService.getAllUsers();
		for (User u : allUsers) {
			VisitedLocation vl = u.getLastVisitedLocation();
//			if (vl != null) { // Null may happen through Tracker thread for instance
				allUserLocations.put(u.getUserId().toString(), vl.location);
//			}
		}
		return allUserLocations;
	}
	

	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = new ArrayList<Provider>();
		List<AttractionNearby> attractions = getNearByAttractions(user.getUserName());
		for (AttractionNearby a : attractions) {
			providers.addAll(tripPricer.getPrice(
					tripPricerApiKey, 
					a.id, 
					user.getUserPreferences().getNumberOfAdults(), 
					user.getUserPreferences().getNumberOfChildren(), 
					user.getUserPreferences().getTripDuration(), 
					cumulativeRewardPoints));			
		}
		user.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
/*		if (visitedLocation == null) { // May happen through Tracker thread for instance
			return null;
		}*/
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<AttractionNearby> getNearByAttractions(String userName) {		
		// Prepare list of all attractions for sorting
		User user = userService.getUser(userName);
    	VisitedLocation visitedLocation = getUserLocation(user);
		Location fromLocation = visitedLocation.location;
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
			int rewardPoints = rewardsService.getRewardPoints(attraction, user);
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

}

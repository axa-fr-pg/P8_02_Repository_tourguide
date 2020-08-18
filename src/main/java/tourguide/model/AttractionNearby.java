package tourguide.model;

import java.util.UUID;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourguide.gps.LocationWithEmptyConstructor;
import tourguide.reward.RewardService;

/**
 * AttractionNearby is the data format exchanged in JSON with the client for the getNearbyAttractions controller
 * 
 * Members are public as tolerated for basic data structure
 * 
 */
public class AttractionNearby {
	public final UUID id; // Basically not requested but required for further reuse of object instances
	public final String name;
	public LocationWithEmptyConstructor attractionLocation;
	public LocationWithEmptyConstructor userLocation;
	public double distance;
	public int rewardPoints;
	
	public AttractionNearby(Attraction attraction, User user, int rewardPoints) {
		id = attraction.attractionId;
		name = attraction.attractionName;
		attractionLocation = new LocationWithEmptyConstructor(attraction.latitude, attraction.longitude);
		VisitedLocation visitedLocation = user.getLastVisitedLocation();
		userLocation = new LocationWithEmptyConstructor(visitedLocation.location.latitude, visitedLocation.location.longitude);
		distance = RewardService.getDistance(attractionLocation, userLocation);
		this.rewardPoints = rewardPoints;
	}
	
	public AttractionNearby() {
		id = new UUID(0,0);
		name = new String();
	}
}

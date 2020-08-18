package tourguide.model;

import java.util.Date;
import java.util.UUID;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public class UserReward {

	// Replaced VisitedLocation class with the 3 contained objects for use with the Jackson object mapper
	public LocationWithEmptyConstructor visitLocation;
	public UUID visitUserId;
	public Date visitTime;
	
	public AttractionWithEmptyConstructor attraction;
	private int rewardPoints;
	
	public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
		visitLocation = new LocationWithEmptyConstructor(
				visitedLocation.location.latitude, visitedLocation.location.longitude);
		visitUserId = visitedLocation.userId;
		visitTime = visitedLocation.timeVisited;
		this.attraction = new AttractionWithEmptyConstructor(attraction.attractionName,
				attraction.city, attraction.state, attraction.latitude, attraction.longitude);
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward() {
	}
	
	/* NOT USED 
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	} */

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
	public int getRewardPoints() {
		return rewardPoints;
	}	
}

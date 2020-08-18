package tourguide.model;

import java.util.Date;
import java.util.UUID;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourguide.gps.AttractionWithEmptyConstructor;
import tourguide.gps.LocationWithEmptyConstructor;

public class UserReward {

	/*
	 *  The older version of this class contained a isitedLocation member.
	 *  This was not compatible with the Jackson object mapper.
	 *  This member has been split into its 3 own members : visitLocation, visitUserId & visitTime.
	 */
	public LocationWithEmptyConstructor visitLocation;
	public UUID visitUserId;
	public Date visitTime;
	public AttractionWithEmptyConstructor attraction;
	public int rewardPoints;
	
	public UserReward(VisitedLocation visitedLocation, AttractionData attraction, int rewardPoints) {
		visitLocation = new LocationWithEmptyConstructor(
				visitedLocation.location.latitude, visitedLocation.location.longitude);
		visitUserId = visitedLocation.userId;
		visitTime = visitedLocation.timeVisited;
		this.attraction = new AttractionWithEmptyConstructor(attraction.name,
				attraction.city, attraction.state, attraction.latitude, attraction.longitude);
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward() {
	}
}

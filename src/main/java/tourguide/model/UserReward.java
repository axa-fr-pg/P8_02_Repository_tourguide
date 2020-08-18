package tourguide.model;

public class UserReward {

	public VisitedLocationData visitedLocation;
	public AttractionData attraction;
	public int rewardPoints;
	
	public UserReward(VisitedLocationData visitedLocation, AttractionData attraction, int rewardPoints) {
		visitedLocation = new VisitedLocationData(
			visitedLocation.userId,
			new LocationData(visitedLocation.location.latitude, visitedLocation.location.longitude),
			visitedLocation.timeVisited);
		this.attraction = new AttractionData(attraction.name,
				attraction.city, attraction.state, attraction.latitude, attraction.longitude);
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward() {
	}
}

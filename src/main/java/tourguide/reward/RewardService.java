package tourguide.reward;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourguide.model.User;
import tourguide.model.UserReward;

@Service
public class RewardService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private static final double EARTH_RADIUS_IN_NAUTICAL_MILES = 3440.0647948;

    private static final int DEFAULT_PROXIMITY_MAXIMAL_DISTANCE = 10;
	private int proximityMaximalDistance = DEFAULT_PROXIMITY_MAXIMAL_DISTANCE;

	Logger logger = LoggerFactory.getLogger(RewardService.class);
	
	@Autowired private RewardCentral rewardCentral;
	
	public void setProximityMaximalDistance(int proximityBuffer) {
		logger.debug("setProximityMaximalDistance to " + proximityBuffer);
		this.proximityMaximalDistance = proximityBuffer;
	}
	
	/* NOT USED
	public void resetProximityMaximalDistanceToDefault() {
		proximityMaximalDistance = DEFAULT_PROXIMITY_MAXIMAL_DISTANCE;
	}*/
	
	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		logger.debug("nearAttraction " + attraction.attractionName);
		return getDistance(attraction, visitedLocation.location) > proximityMaximalDistance ? false : true;
	}
	
	public int getRewardPoints(Attraction attraction, User user) {
		logger.debug("nearAttraction userName = " + user.getUserName());
		return rewardCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	/**
	 * Calculates distance in statute miles between locations
	 * Uses Spherical Law of Cosines
	 * @param loc1
	 * @param loc2
	 * @return calculated distance
	 */	
	public static double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angleDistance = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMilesDistance = EARTH_RADIUS_IN_NAUTICAL_MILES * angleDistance;
        double statuteMilesDistance = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMilesDistance;
        return statuteMilesDistance;
	}

	public void addAllNewRewards(User user, List<Attraction> attractions)	{
		logger.debug("addAllNewRewards userName = " + user.getUserName() 
			+ " and attractionList of size " + attractions.size());
		for(VisitedLocation visitedLocation : user.getVisitedLocations()) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}
	}
	
	public void addAllNewRewardsAllUsers(List<User> userList, List<Attraction> attractions)	{
		logger.debug("addAllNewRewardsAllUsers userListName of size = " + userList.size() 
			+ " and attractionList of size " + attractions.size());
		userList.stream().forEach(user -> {
			addAllNewRewards(user, attractions);
		});
	}
	
	public int sumOfAllRewardPoints(User user) {
		logger.debug("sumOfAllRewardPoints userName = " + user.getUserName()) ;
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		return cumulativeRewardPoints;
	}
}

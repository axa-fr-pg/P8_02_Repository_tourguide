package tourguide.reward;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourguide.model.AttractionData;
import tourguide.model.User;
import tourguide.model.UserReward;

@Service
public class RewardService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private static final double EARTH_RADIUS_IN_NAUTICAL_MILES = 3440.0647948;

    private static final int NUMBER_OF_EXPECTED_USER_PARTITIONS = 25;
    private static final int THREAD_POOL_SIZE = NUMBER_OF_EXPECTED_USER_PARTITIONS * 2;
    
    private static final int DEFAULT_PROXIMITY_MAXIMAL_DISTANCE = 10;
	private int proximityMaximalDistance = DEFAULT_PROXIMITY_MAXIMAL_DISTANCE;

	private Logger logger = LoggerFactory.getLogger(RewardService.class);
	
	@Autowired private RewardCentral rewardCentral;
	
	public void setProximityMaximalDistance(int proximityBuffer) {
		logger.debug("setProximityMaximalDistance to " + proximityBuffer);
		this.proximityMaximalDistance = proximityBuffer;
	}
	
	public int getProximityMaximalDistance() {
		logger.debug("getProximityMaximalDistance returns " + proximityMaximalDistance);
		return this.proximityMaximalDistance;
	}
	
	public boolean nearAttraction(VisitedLocation visitedLocation, AttractionData attractionData) {
		logger.debug("nearAttraction " + attractionData.name);
		Location attractionLocation = new Location(attractionData.latitude, attractionData.longitude);
		return getDistance(attractionLocation, visitedLocation.location) > proximityMaximalDistance ? false : true;
	}
	
	public int getRewardPoints(AttractionData attractionData, User user) {
		logger.debug("getRewardPoints userName = " + user.getUserName() + " for attraction " + attractionData.name );
		int points = rewardCentral.getAttractionRewardPoints(attractionData.id, user.getUserId());
		return points;
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

	public void addAllNewRewards(User user, List<AttractionData> attractions)	{
		logger.debug("addAllNewRewards userName = " + user.getUserName() 
			+ " and attractionList of size " + attractions.size());
		for(VisitedLocation visitedLocation : user.getVisitedLocations()) {
			for(AttractionData attractionData : attractions) {
				long numberOfRewardsOfTheUserForThisAttraction = 
						user.getUserRewards().stream().filter(reward -> 
						reward.attraction.attractionName.equals(attractionData.name)).count();
				if( numberOfRewardsOfTheUserForThisAttraction == 0) {
					if(nearAttraction(visitedLocation, attractionData)) {
						logger.debug("addAllNewRewards new Reward for userName = " + user.getUserName() + " for attraction " + attractionData.name );
						user.addUserReward(new UserReward(visitedLocation, attractionData, getRewardPoints(attractionData, user)));
					}
				}
			}
		}
	}
	
	private List<List<User>> divideUserList(List<User> userList) {
		List<List<User>> partitionList = new LinkedList<List<User>>();
		int expectedSize = userList.size() / NUMBER_OF_EXPECTED_USER_PARTITIONS;
		if (expectedSize == 0) {
			partitionList.add(userList);
			return partitionList;
		}
		for (int i = 0; i < userList.size(); i += expectedSize) {
			partitionList.add(userList.subList(i, Math.min(i + expectedSize, userList.size())));
		}
		return partitionList;
	}
	
	public long addAllNewRewardsAllUsers(List<User> userList, List<AttractionData> attractions)	{
		logger.debug("addAllNewRewardsAllUsers userListName of size = " + userList.size() 
			+ " and attractionList of size " + attractions.size());
		ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_POOL_SIZE);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		// Divide user list into several parts and submit work separately for these parts
		divideUserList(userList).stream().parallel().forEach( partition -> {
			try {
				logger.debug("addAllNewRewardsAllUsers submits calculation for user partition of size" +  partition.size());
				forkJoinPool.submit( () -> partition.stream().parallel().forEach(user -> {
					addAllNewRewards(user, attractions);
				})).get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error("addAllNewRewardsAllUsers got an exception");
				e.printStackTrace();
				throw new RuntimeException("addAllNewRewardsAllUsers got an exception");
			}
		});
		stopWatch.stop();
		forkJoinPool.shutdown();
		long duration = TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime());
		logger.info("addAllNewRewardsAllUsers required " + duration + " seconds for " + userList.size() + " users");
		return duration;
	}
	
	public int sumOfAllRewardPoints(User user) {
		logger.debug("sumOfAllRewardPoints userName = " + user.getUserName()) ;
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.rewardPoints).sum();
		return cumulativeRewardPoints;
	}
}

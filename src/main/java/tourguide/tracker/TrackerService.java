package tourguide.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import tourguide.gps.GpsService;
import tourguide.model.User;
import tourguide.reward.RewardService;
import tourguide.user.UserService;

@Service
public class TrackerService extends Thread {

	private Logger logger = LoggerFactory.getLogger(TrackerService.class);
	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();	
	private boolean stop = false;
	
	@Autowired private UserService userService;
	@Autowired private GpsService gpsService;
	@Autowired private RewardService rewardService;

	public TrackerService() {
		executorService.submit(this);
	}
	
	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}
	
	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while(true) {
			if(Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}
			
			List<User> users = userService.getAllUsers();
			logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
			users.forEach(u -> {System.out.println(u.getUserName() + " visitedLocations before:" + u.getVisitedLocations().size());} );
			stopWatch.start();
			trackAllUsers();
			stopWatch.stop();
			users.forEach(u -> {System.out.println(u.getUserName() + " visitedLocations after:" + u.getVisitedLocations().size());} );
			logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
			stopWatch.reset();
			try {
				logger.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);
			} catch (InterruptedException e) {
				break;
			}
		}
		
	}
	
	protected void trackAllUsers() {
		// Get All users
		List<User> allUsers = userService.getAllUsers();
		// Get and register current location for all users
		gpsService.trackAllUserLocations(allUsers);
		// Get all attractions
		List<Attraction> allAttractions = gpsService.getAllAttractions();
		// Update rewards for all users
		rewardService.addAllNewRewardsAllUsers(allUsers, allAttractions);
	}
}

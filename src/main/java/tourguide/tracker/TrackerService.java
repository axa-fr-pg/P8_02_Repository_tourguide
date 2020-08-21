package tourguide.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.api.GpsRequest;
import tourguide.api.RewardRequest;
import tourguide.model.AttractionData;
import tourguide.model.User;
import tourguide.user.UserService;

@Service
public class TrackerService extends Thread {

	private Logger logger = LoggerFactory.getLogger(TrackerService.class);
	private static final long TRACKER_POLLING_FREQUENCE = TimeUnit.MINUTES.toSeconds(5);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();	
	private boolean stop = false;
	
	@Autowired private UserService userService;
	@Autowired private GpsRequest gpsRequest;
	@Autowired private RewardRequest rewardRequest;

	public TrackerService() {
		logger.debug("new instance");
		executorService.submit(this);
	}
	
	@Override
	public void run() {
		logger.debug("run() starting");
		while(true) {
			if(Thread.currentThread().isInterrupted() || stop) {
				logger.debug("run() has been told to stop");
				break;
			}			
			trackAllUsers();
			try {
				logger.debug("run() waiting for next iteration");
				TimeUnit.SECONDS.sleep(TRACKER_POLLING_FREQUENCE);
			} catch (InterruptedException e) {
				logger.error("run() catched InterruptedException");
				break;
			}
		}
		logger.debug("run() reached the end");
	}
	
	public void trackAllUsers() {
		logger.debug("trackAllUsers starts iteration over all users");
		// Get All users
		List<User> allUsersStart = userService.getAllUsers();
		// Get and register current location for all users
		List<User> allUsersUpdated = gpsRequest.trackAllUserLocations(allUsersStart);
		// Get all attractions
		List<AttractionData> allAttractions = gpsRequest.getAllAttractions();
		// Update rewards for all users
		rewardRequest.addAllNewRewardsAllUsers(allUsersUpdated, allAttractions);
	}
}

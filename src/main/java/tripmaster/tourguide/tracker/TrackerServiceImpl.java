package tripmaster.tourguide.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.user.User;
import tripmaster.tourguide.api.GpsRequestService;
import tripmaster.tourguide.api.RewardRequestService;
import tripmaster.tourguide.user.UserService;

/**
 * Class for tracker services. Implements TrackerService interface.
 * @see tripmaster.tourguide.tracker.TrackerService
 */
@Service
public class TrackerServiceImpl extends Thread implements TrackerService {

	private Logger logger = LoggerFactory.getLogger(TrackerServiceImpl.class);
	private static final long TRACKER_POLLING_FREQUENCE = TimeUnit.MINUTES.toSeconds(5);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();	
	private boolean stop = false;
	
	@Autowired private UserService userService;
	@Autowired private GpsRequestService gpsRequest;
	@Autowired private RewardRequestService rewardRequest;

	public TrackerServiceImpl() {
		logger.debug("new instance");
		executorService.submit(this);
	}
	
	/*
	 * Launch a tracker daemon, which will track all users every 5 minutes.
	 * @see tripmaster.tourguide.tracker.TrackerServiceImpl.trackAllUsers
	 */
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
	
	/**
	 * Computes a full update of the user information for each user in the ecosystem :
	 * get the current location, store it into the user visited location history, add user rewards for newly visited attractions.
	 */
	@Override
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

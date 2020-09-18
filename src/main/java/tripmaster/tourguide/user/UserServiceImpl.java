package tripmaster.tourguide.user;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tripmaster.common.location.LocationData;
import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.user.User;

/**
 *
 */
/**
 * Class for user services. Implements UserService interface.
 * Persistence in memory for beta production with internal users.
 * A database will be set up with external users in a future release.
 * @see tripmaster.tourguide.user.UserService
 */
@Service
public class UserServiceImpl implements UserService {
	
	private static final int DEFAULT_INTERNAL_USER_NUMBER = 100;
	private static final boolean DEFAULT_LOCATION_HISTORY_ACTIVATED = true;
	private Random random;
	
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	// TODO : replace map with genuine data access and persistence layers.
	private Map<String, User> internalUserMap;
	
	public UserServiceImpl() {
		this(true);
		logger.debug("new instance with empty constructor");
	}
	
	// For tests it is very convenient to create an empty instance of UserService (without any user)
	public UserServiceImpl(boolean fillInternalUserMapWithRandomUsers) {
		logger.debug("new instance of UserService with fillInternalUserMapWithRandomUsers = " + 
				fillInternalUserMapWithRandomUsers);
		internalUserMap = new HashMap<>();		
		random = new Random();
		if (fillInternalUserMapWithRandomUsers) {
			logger.debug("Initializing users");
			initializeInternalUsers(DEFAULT_INTERNAL_USER_NUMBER, DEFAULT_LOCATION_HISTORY_ACTIVATED);
			logger.debug("Finished initializing users");
		}
	}

	/**
	 * Gets an user based on his name.
	 * @param userName to be searched for.
	 * @return User found with the given name.
	 */
	@Override
	public User getUser(String userName) {
		logger.debug("getUser with userName = " + userName);
		return internalUserMap.get(userName);
	}
	
	/**
	 * Get a list with all users in the ecosystem.
	 * @return List of users.
	 */
	@Override
	public List<User> getAllUsers() {
		logger.debug("getAllUsers returns list of size = " + internalUserMap.size());
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	/**
	 * Replace the user list in the ecosystem with the given user list.
	 * Makes almost sense in a testing context.
	 * @param userList to use for future user requests.
	 * @see tripmaster.tourguide.user.UserServiceImpl.getAllUsers
	 * @see tripmaster.tourguide.user.UserServiceImpl.getUser
	 */
	@Override
	public void setAllUsers(List<User> userList) {
		logger.debug("setAllUsers with list of size = " + userList.size());
		internalUserMap = new HashMap<>();	
		userList.stream().forEach(user -> { internalUserMap.put(user.userName, user); });
	}
	
	/**
	 * Add the given user to the user list in the ecosystem.
	 * @param user to be added.
	 */
	@Override
	public void addUser(User user) {
		logger.debug("addUser with userName = " + user.userName);
		if(!internalUserMap.containsKey(user.userName)) {
			internalUserMap.put(user.userName, user);
		}
	}
	
	/**
	 * Generate a user list to populate the ecosystem.
	 * @param expectedNumberOfUsers size of the list to be created.
	 * @param withLocationHistory boolean to specify whether users shall be generated with or without a history of 3 visited locations.
	 */
	@Override
	public void initializeInternalUsers(int expectedNumberOfUsers, boolean withLocationHistory) {
		logger.debug("initializeInternalUsers with InternalUserNumber = " + expectedNumberOfUsers);
		internalUserMap = new HashMap<>();
		IntStream.range(0, expectedNumberOfUsers).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000" + i;
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			if (withLocationHistory) {
				generateUserLocationHistory(user);			
			}
			internalUserMap.put(userName, user);
		});
		logger.debug("initializeInternalUsers terminated");
	}

	// helper method to populate the ecosystem
	private void generateUserLocationHistory(User user) {
		logger.debug("generateUserLocationHistory with userName = " + user.userName);
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocationData(user.userId, 
					new LocationData(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	// helper method to populate the ecosystem
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
	}
	
	// helper method to populate the ecosystem
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
	}
	
	// helper method to populate the ecosystem
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}	
}

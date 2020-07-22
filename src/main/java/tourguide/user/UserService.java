package tourguide.user;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourguide.model.User;
import tourguide.service.TourGuideService;

/**
 * Provide services for users based on a persistence layer.
 * Use memory for beta production with internal users
 * Database connection will be used with external users in a future release
 *
 */
@Service
public class UserService {
	
	Random random;

	Logger logger = LoggerFactory.getLogger(UserService.class);
	private Map<String, User> internalUserMap;
	
	public UserService() {
		this(true);
		logger.debug("new instance of UserService with empty constructor");
	}
	
	public UserService(boolean fillInternalUserMapWithRandomUsers) {
		logger.debug("new instance of UserService with fillInternalUserMapWithRandomUsers = " + 
				fillInternalUserMapWithRandomUsers);
		internalUserMap = new HashMap<>();		
		random = new Random();
		if (fillInternalUserMapWithRandomUsers) {
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
	}

	@Autowired TourGuideService tourGuideService;
	
	public User getUser(String userName) {
		logger.debug("getUser with userName = " + userName);
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		logger.debug("getAllUsers returns list of size = " + internalUserMap.size());
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		logger.debug("addUser with userName = " + user.getUserName());
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public void initializeInternalUsers() {
		logger.debug("initializeInternalUsers with InternalUserNumber = " 
				+ UserInternalNumber.get());
		IntStream.range(0, UserInternalNumber.get()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000" + i;
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + UserInternalNumber.get() + " internal beta users.");
	}

	private void generateUserLocationHistory(User user) {
		logger.debug("generateUserLocationHistory with userName = " + user.getUserName());
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), 
					new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}	
}

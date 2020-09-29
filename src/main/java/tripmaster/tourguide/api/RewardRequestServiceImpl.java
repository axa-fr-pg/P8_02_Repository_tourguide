package tripmaster.tourguide.api;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.user.User;
import tripmaster.common.user.UserAttraction;
import tripmaster.common.user.UserAttractionLists;
import tripmaster.tourguide.user.UserService;

/**
 * Class to prepare a reward API request. Implements RewardRequestService interface.
 * @see tripmaster.tourguide.api.RewardClient
 * @see tripmaster.tourguide.api.RewardRequestService
 */
@Service
public class RewardRequestServiceImpl implements RewardRequestService {
	
	private Logger logger = LoggerFactory.getLogger(RewardRequestServiceImpl.class);
	private final RewardClient rewardClient;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private UserService userService;
	
	public RewardRequestServiceImpl(RewardClient rewardClient) {
		this.rewardClient = rewardClient;
	}
	
	/**
	 * Adds new rewards to all users reward lists for each given attraction (if not already rewarded for a given user). 
	 * @param userList for which the rewards shall be added.
	 * @param attractions list of AttractionData for which a reward shall be added (if not already done for a given user).
	 * @return List of users updated with added rewards.
	 */
	@Override
	public List<User> addAllNewRewardsAllUsers(List<User> userList, List<AttractionData> attractions) {
		logListContent("addAllNewRewardsAllUsers before external call", userList);
		logListContent("addAllNewRewardsAllUsers before external call", attractions);
		UserAttractionLists userAttractionLists = new UserAttractionLists(attractions, userList);
		List<User> updatedUserList = rewardClient.addAllNewRewardsAllUsers(userAttractionLists);
		userService.setAllUsers(updatedUserList);
		logListContent("addAllNewRewardsAllUsers before external call", userList);
		return updatedUserList;
	}

	/**
	 * Calculates the number of reward points for a given user.
	 * @param user for which the calculation shall be done.
	 * @return int number of points.
	 */
	@Override
	public int sumOfAllRewardPoints(User user) {
		logListContent("sumOfAllRewardPoints before external call", Collections.singletonList(user));
		int result = rewardClient.sumOfAllRewardPoints(user);
		logListContent("sumOfAllRewardPoints after external call", Collections.singletonList(result));
		return result;		
	}

	/**
	 * Gets the number of a reward points for a given attraction & user pair. 
	 * @param user for which the points shall be calculated
	 * @param attraction for which the points shall be calculated
	 * @return int number of points.
	 */
	@Override
	public int getRewardPoints(AttractionData attraction, User user) {
		logListContent("getRewardPoints before external call", Collections.singletonList(attraction));
		logListContent("getRewardPoints before external call", Collections.singletonList(user));
		UserAttraction userAttraction = new UserAttraction(user, attraction);
		int result =  rewardClient.getRewardPoints(userAttraction);
		logListContent("getRewardPoints after external call", Collections.singletonList(result));
		return result;
	}
	
	// logging helper tool
	private void logListContent(String methodName, List<?> list) {
		logger.debug(methodName + " number of elements " + list.size() + " : " + list.toString());
		try {
			logger.debug(methodName + " content details : " + objectMapper.writeValueAsString(list));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("logListContent catched a JsonProcessingException");
		}
	}
}

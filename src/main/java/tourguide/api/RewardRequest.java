package tourguide.api;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tourguide.model.AttractionData;
import tourguide.model.AttractionUserLists;
import tourguide.model.User;
import tourguide.model.UserAttraction;
import tourguide.user.UserService;

@Service
public class RewardRequest {
	
	private Logger logger = LoggerFactory.getLogger(RewardRequest.class);
	private final RewardClient rewardClient;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private UserService userService;
	
	public RewardRequest(RewardClient rewardClient) {
		this.rewardClient = rewardClient;
	}
	
	public List<User> addAllNewRewardsAllUsers(List<User> userList, List<AttractionData> attractions) {
		logListContent("addAllNewRewardsAllUsers before external call", userList);
		logListContent("addAllNewRewardsAllUsers before external call", attractions);
		AttractionUserLists attractionUserLists = new AttractionUserLists(attractions, userList);
		List<User> updatedUserList = rewardClient.addAllNewRewardsAllUsers(attractionUserLists);
		userService.setAllUsers(updatedUserList);
		logListContent("addAllNewRewardsAllUsers before external call", userList);
		return updatedUserList;
	}

	public int sumOfAllRewardPoints(User user) {
		logListContent("sumOfAllRewardPoints before external call", Collections.singletonList(user));
		int result = rewardClient.sumOfAllRewardPoints(user);
		logListContent("sumOfAllRewardPoints after external call", Collections.singletonList(result));
		return result;		
	}

	public int getRewardPoints(AttractionData attraction, User user) {
		logListContent("sumOfAllRewardPoints before external call", Collections.singletonList(attraction));
		logListContent("sumOfAllRewardPoints before external call", Collections.singletonList(user));
		UserAttraction userAttraction = new UserAttraction(user, attraction);
		int result =  rewardClient.getRewardPoints(userAttraction);
		logListContent("sumOfAllRewardPoints after external call", Collections.singletonList(result));
		return result;
	}
	
	private void logListContent(String methodName, List<?> list) {
		logger.debug(methodName + " number of elements " + list.size() + " : " + list.toString());
		try {
			logger.debug(methodName + " content details : " + objectMapper.writeValueAsString(list));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("logListContent catched a JsonProcessingException");
		}
	}
}

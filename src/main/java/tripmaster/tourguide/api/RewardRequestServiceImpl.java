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

@Service
public class RewardRequestServiceImpl implements RewardRequestService {
	
	private Logger logger = LoggerFactory.getLogger(RewardRequestServiceImpl.class);
	private final RewardClient rewardClient;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private UserService userService;
	
	public RewardRequestServiceImpl(RewardClient rewardClient) {
		this.rewardClient = rewardClient;
	}
	
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

	@Override
	public int sumOfAllRewardPoints(User user) {
		logListContent("sumOfAllRewardPoints before external call", Collections.singletonList(user));
		int result = rewardClient.sumOfAllRewardPoints(user);
		logListContent("sumOfAllRewardPoints after external call", Collections.singletonList(result));
		return result;		
	}

	@Override
	public int getRewardPoints(AttractionData attraction, User user) {
		logListContent("getRewardPoints before external call", Collections.singletonList(attraction));
		logListContent("getRewardPoints before external call", Collections.singletonList(user));
		UserAttraction userAttraction = new UserAttraction(user, attraction);
		int result =  rewardClient.getRewardPoints(userAttraction);
		logListContent("getRewardPoints after external call", Collections.singletonList(result));
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

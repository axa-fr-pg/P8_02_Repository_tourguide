package tourguide.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.model.AttractionData;
import tourguide.model.User;
import tourguide.reward.RewardController;

@Service
public class RewardRequest {
	
	private Logger logger = LoggerFactory.getLogger(RewardRequest.class);
	@Autowired private RewardController rewardController;
	
	public void addAllNewRewardsAllUsers(List<User> userList, List<AttractionData> attractions) {
		logger.debug("addAllNewRewardsAllUsers userListName of size = " + userList.size() 
			+ " and attractionList of size " + attractions.size());
		rewardController.addAllNewRewardsAllUsers(userList, attractions);		
	}

	
}

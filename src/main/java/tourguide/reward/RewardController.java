package tourguide.reward;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.model.AttractionData;
import tourguide.model.User;

@Service
public class RewardController {

	private Logger logger = LoggerFactory.getLogger(RewardController.class);
	@Autowired private RewardService rewardService;
	
	public void addAllNewRewardsAllUsers(List<User> userList, List<AttractionData> attractions) {
		logger.debug("addAllNewRewardsAllUsers userListName of size = " + userList.size() 
			+ " and attractionList of size " + attractions.size());
		rewardService.addAllNewRewardsAllUsers(userList, attractions);		
	}

}

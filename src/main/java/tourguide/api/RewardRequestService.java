package tourguide.api;

import java.util.List;

import tourguide.model.AttractionData;
import tourguide.model.User;

public interface RewardRequestService {

	List<User> addAllNewRewardsAllUsers(List<User> userList, List<AttractionData> attractions);

	int sumOfAllRewardPoints(User user);

	int getRewardPoints(AttractionData attraction, User user);

}
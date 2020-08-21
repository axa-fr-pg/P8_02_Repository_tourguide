package tourguide.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tourguide.model.UserAttractionLists;
import tourguide.model.User;
import tourguide.model.UserAttraction;

@FeignClient(name="reward", url="http://localhost:8080")
public interface RewardClient {

	@PatchMapping("/addAllNewRewardsAllUsers")
	List<User> addAllNewRewardsAllUsers(@RequestBody UserAttractionLists attractionUserLists);

	@GetMapping("/sumOfAllRewardPoints")
	int sumOfAllRewardPoints(@RequestBody User user);

	@GetMapping("/getRewardPoints")
	int getRewardPoints(@RequestBody UserAttraction userAttraction);
}

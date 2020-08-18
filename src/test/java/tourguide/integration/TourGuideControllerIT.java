package tourguide.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourguide.gps.GpsService;
import tourguide.model.AttractionNearby;
import tourguide.model.LocationWithEmptyConstructor;
import tourguide.model.ProviderWithEmptyConstructor;
import tourguide.model.User;
import tourguide.model.UserReward;
import tourguide.reward.RewardService;
import tourguide.service.TourGuideService;
import tourguide.user.UserService;
import tripPricer.Provider;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TourGuideControllerIT {

	@Autowired private UserService userService;
	@Autowired private GpsService gpsService;
	@Autowired private RewardService rewardService;
	@Autowired private WebApplicationContext wac; 
	@Autowired private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

	@Test
	public void givenUser1_whenGetLastLocation_thenReturnsCorrectLocation() throws Exception 
	{
		// GIVEN
		String userName = "internalUser1";
		User user = userService.getUser(userName);
		// WHEN
		String responseString = mockMvc
				.perform(get("/getLastLocation?userName=" + userName))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();		
		Location responseObject = objectMapper.readValue(responseString, LocationWithEmptyConstructor.class);
		// THEN
		assertNotNull(responseObject);
		assertEquals(user.getLastVisitedLocation().location.latitude, responseObject.latitude, 0.000001);
		assertEquals(user.getLastVisitedLocation().location.longitude, responseObject.longitude, 0.000001);
	}
	
	@Test
	public void givenUser1_whenGetNearbyAttractions_thenReturnsCorrectAttractions() throws Exception 
	{
		// GIVEN 
		String userName = "internalUser1";
		User user = userService.getUser(userName);
		Location userLocation = user.getLastVisitedLocation().location;
		// WHEN
		String responseString = mockMvc
				.perform(get("/getNearbyAttractions?userName=" + userName))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		JavaType expectedResultType = objectMapper.getTypeFactory().constructCollectionType(List.class, AttractionNearby.class);
		List<AttractionNearby> responseObject = objectMapper.readValue(responseString, expectedResultType);
		// THEN
		assertNotNull(responseObject);
		assertThat(responseObject.size() > 0);
		for (AttractionNearby a : responseObject) {
			assertNotNull(a);
			double distance = RewardService.getDistance(a.attractionLocation, userLocation);
			assertThat(distance < rewardService.getProximityMaximalDistance());
		}
	}
	
	@Test
	public void givenUser1_whenGetRewards_thenReturnsExpectedReward() throws Exception 
	{
		// GIVEN
		String userName = "internalUser1";
		int rewardPoints = 12345;
		User user = userService.getUser(userName);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), 
				user.getLastVisitedLocation().location, new Date());
		Attraction attraction = gpsService.getAllAttractions().get(0);
		user.addUserReward(new UserReward(visitedLocation, attraction, rewardPoints));
		// WHEN
		String responseString = mockMvc
				.perform(get("/getRewards?userName=" + userName))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();		
		JavaType expectedResultType = objectMapper.getTypeFactory().constructCollectionType(List.class, UserReward.class);
		List<UserReward> responseObject = objectMapper.readValue(responseString, expectedResultType);
		// THEN
		assertNotNull(responseObject);
		assertEquals(1, responseObject.size());
		boolean rewardFound = false;
		for (UserReward r : responseObject) {
			if (r.visitUserId.equals(user.getUserId()) && r.getRewardPoints() == 12345) {
				rewardFound = true;
			}
		}
		assertTrue(rewardFound);
	}

	@Test
	public void givenUserList_whenGetAllCurrentLocations_thenReturnsFullList() throws Exception 
	{
		// GIVEN
		List<User> userList = userService.getAllUsers();
		int numberOfUsers = userList.size();
		// WHEN
		String responseString = mockMvc
				.perform(get("/getAllCurrentLocations"))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();		
		JavaType expectedResultType = objectMapper.getTypeFactory().constructMapLikeType(
				Map.class, String.class, LocationWithEmptyConstructor.class);
		Map<String, Location> responseObject = objectMapper.readValue(responseString, expectedResultType);
		// THEN
		assertNotNull(responseObject);
		assertEquals(numberOfUsers, responseObject.size());
		for (int i=0; i< numberOfUsers; i++) {
			Location location = responseObject.get(userList.get(i).getUserId().toString());
			assertNotNull(location);
		}
	}
	
	@Test
	public void givenUser1_whenGetTripDeals_thenReturnsCorrectSomeDeals() throws Exception 
	{
		// GIVEN 
		String userName = "internalUser1";
		// WHEN
		String responseString = mockMvc
				.perform(get("/getTripDeals?userName=" + userName))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		JavaType expectedResultType = objectMapper.getTypeFactory().constructCollectionType(List.class, ProviderWithEmptyConstructor.class);
		List<Provider> responseObject = objectMapper.readValue(responseString, expectedResultType);
		// THEN
		assertNotNull(responseObject);
		assertThat(responseObject.size() >= TourGuideService.NUMBER_OF_PROPOSED_ATTRACTIONS);
	}
	
	@Test
	public void givenUser1_whenGetUser_thenReturnsCorrectUser() throws Exception 
	{
		// GIVEN
		String userName = "internalUser1";
		User user = userService.getUser(userName);
		// WHEN
		String responseString = mockMvc
				.perform(get("/getUser?userName=" + userName))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();		
		// THEN
		assertNotNull(responseString);
		assertTrue(responseString.contains(user.getUserName()));
		assertTrue(responseString.contains(user.getUserId().toString()));
	}	
}

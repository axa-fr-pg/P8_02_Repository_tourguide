package tourguide.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import tourguide.model.User;
import tourguide.model.UserReward;
import tourguide.reward.RewardService;
import tourguide.user.UserService;

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
				.perform(get("/getNearbyAttractions?userName=internalUser1"))
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
	public void givenUser1_whenGetRewards_thenReturnsNonEmptyRewardList() throws Exception 
	{
		// GIVEN
		String userName = "internalUser1";
		int rewardPoints = 12345;
		String rewardPointsAsString = "" + rewardPoints;
		User user = userService.getUser(userName);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), 
				user.getLastVisitedLocation().location, new Date());
		Attraction attraction = gpsService.getAllAttractions().get(0);
		user.addUserReward(new UserReward(visitedLocation, attraction, rewardPoints));
		List<String> expectedResponse = Arrays.asList("visitedLocation", "userId", "mostSigBits", "leastSigBits", 
				"location", "longitude", "latitude", "timeVisited", "date", "day", 
				"attraction", "attractionName", "city", attraction.attractionName, "state", 
				"attractionId", "rewardPoints", rewardPointsAsString);
		// WHEN
		String responseString = mockMvc
				.perform(get("/getRewards?userName=" + userName))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();		
		// THEN
		assertNotNull(responseString);
		for (String s : expectedResponse) {
			assertTrue(responseString.contains(s));
		}
	}

	@Test
	public void givenUserList_whenGetAllCurrentLocations_thenReturnsFullList() throws Exception 
	{
		// GIVEN user list initialized at program startup
		// WHEN
		String responseString = mockMvc
				.perform(get("/getAllCurrentLocations"))
				.andDo(print())
				.andReturn().getResponse().getContentAsString();		
		// THEN
		assertNotNull(responseString);
		assertTrue(responseString.contains("latitude"));
		assertTrue(responseString.contains("longitude"));
	}
	
}

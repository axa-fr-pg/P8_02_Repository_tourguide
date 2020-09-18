package tripmaster.tourguide.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.output.JsonStream;

import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.trip.ProviderData;
import tripmaster.tourguide.user.UserService;

/**
 * API class for tourguide methods
 */
@RestController
public class TourGuideController {

	@Autowired private TourGuideService tourGuideService;
	@Autowired private UserService userService;
	@Autowired private ObjectMapper objectMapper;
	
	@GetMapping("/")
    public String welcome() {
        return "Welcome at the TourGuide application !";
    }
    
	/**
	 * Gets the last know visited location for a given user. Based on user visited location history.
	 * @param userName the name of the user whose last location is requested.
	 * @return String JSON formatted user last location.
	 */
    @GetMapping("/getLastLocation") 
    public String getLastLocation(@RequestParam String userName) throws JsonProcessingException {
    	VisitedLocationData visitedLocation = tourGuideService.getLastUserLocation(userService.getUser(userName));
		return objectMapper.writeValueAsString(visitedLocation.location);
    }
    
	/**
	 * Gets the 5 attractions which are the closest to the user's location, regardless of their distance from him.
	 * @param userName the name of the user to be considered for the research.
	 * @return String JSON formatted attraction nearby list (id, name, attraction location, user location, distance, reward points).
	 */
    @GetMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) throws JsonProcessingException {
    	return objectMapper.writeValueAsString(tourGuideService.getNearbyAttractions(userName));
    }
    
	/**
	 * Gets the list of all rewards for a given user.
	 * @param userName the name of the user whose reward list is requested.
	 * @return String JSON formatted reward list.
	 */
    @GetMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) throws JsonProcessingException {
    	return objectMapper.writeValueAsString(tourGuideService.getUserRewards(userService.getUser(userName)));
    }
    
	/**
	 * Gets the last know visited location for all users. Based on user visited location history.
	 * @return String JSON formatted map with user name as key and last location as value.
	 */
    @GetMapping("/getAllLastLocations")
    public String getAllLastLocations() throws JsonProcessingException {
    	return objectMapper.writeValueAsString(tourGuideService.getLastLocationAllUsers());
    }
    
	/**
	 * Gets the proposed trips for a given user. Based on user preferences.
	 * @return String JSON formatted list of providers (name, price and id).
	 */
    @GetMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws JsonProcessingException {
    	List<ProviderData> providers = tourGuideService.getTripDeals(userService.getUser(userName));
    	return objectMapper.writeValueAsString(providers);
    }
    
    /*
     * Old version of the endpoints kept below for the sake of backwards compatibility (if really required).
     * New version above has been improved with Jackson (required for exhaustive testing).
     * Please note that old versions have not been fully tested (but they use common services).
     * The single difference between both versions is the format of UUID :
     *     - old : "id" : {"mostSigBits": xxx ,"leastSigBits": xxx,"leastSignificantBits": xxx,"mostSignificantBits": xxx }
     *     - new : "id" : "47ca1549-4d8b-4377-8f74-bb887e9e6570"
     */
    
    /**
     * Old version of the getLastLocation API (legacy format for ids).
     * @see tripmaster.tourguide.api.TourGuideController.getLastLocation
     */
    @GetMapping("/getLastLocationOld") 
    public String getLastLocationOld(@RequestParam String userName) {
    	VisitedLocationData visitedLocation = tourGuideService.getLastUserLocation(userService.getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    
    /**
     * Old version of the getNearbyAttractions API (legacy format for ids).
     * @see tripmaster.tourguide.api.TourGuideController.getNearbyAttractions
     */
    @GetMapping("/getNearbyAttractionsOld") 
    public String getNearbyAttractionsOld(@RequestParam String userName) throws JsonProcessingException {
    	return JsonStream.serialize(tourGuideService.getNearbyAttractions(userName));
    }
    
    /**
     * Old version of the getRewards API (legacy format for ids).
     * @see tripmaster.tourguide.api.TourGuideController.getRewards
     */
    @GetMapping("/getRewardsOld") 
    public String getRewardsOld(@RequestParam String userName) throws JsonProcessingException {
    	return JsonStream.serialize(tourGuideService.getUserRewards(userService.getUser(userName)));
    }
    
    /**
     * Old version of the getAllCurrentLocations API (legacy format for ids).
     * @see tripmaster.tourguide.api.TourGuideController.getAllCurrentLocations
     */
    @GetMapping("/getAllCurrentLocationsOld")
    public String getAllCurrentLocationsOld() throws JsonProcessingException {
    	return JsonStream.serialize(tourGuideService.getLastLocationAllUsers());
    }
    
    /**
     * Old version of the getTripDeals API (legacy format for ids).
     * @see tripmaster.tourguide.api.TourGuideController.getTripDeals
     */
    @GetMapping("/getTripDealsOld")
    public String getTripDealsOld(@RequestParam String userName) throws JsonProcessingException {
    	List<ProviderData> providers = tourGuideService.getTripDeals(userService.getUser(userName));
    	return JsonStream.serialize(providers);
    }
}
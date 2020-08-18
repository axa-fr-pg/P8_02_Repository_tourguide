package tourguide.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourguide.gps.GpsService;
import tourguide.service.TourGuideService;
import tourguide.user.UserService;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired private TourGuideService tourGuideService;
	@Autowired private GpsService gpsService;
	@Autowired private UserService userService;
	@Autowired private ObjectMapper objectMapper;
	
	@GetMapping("/")
    public String index() {
        return "Welcome at the TourGuide application !";
    }
    
    @GetMapping("/getLastLocation") 
    public String getLastLocation(@RequestParam String userName) throws JsonProcessingException {
    	VisitedLocation visitedLocation = gpsService.getLastUserLocation(userService.getUser(userName));
		return objectMapper.writeValueAsString(visitedLocation.location);
    }
    
    @GetMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) throws JsonProcessingException {
    	return objectMapper.writeValueAsString(tourGuideService.getNearByAttractions(userName));
    }
    
    @GetMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) throws JsonProcessingException {
    	return objectMapper.writeValueAsString(tourGuideService.getUserRewards(userService.getUser(userName)));
    }
    
    @GetMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() throws JsonProcessingException {
    	return objectMapper.writeValueAsString(tourGuideService.getLastLocationAllUsers());
    }
    
    @GetMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws JsonProcessingException {
    	List<Provider> providers = tourGuideService.getTripDeals(userService.getUser(userName));
    	return objectMapper.writeValueAsString(providers);
    }
    
    /*
     * Old version of the endpoints kept for the sake of backwards compatibility (if really required).
     * New version above has been improved with Jackson (required for exhaustive testing).
     * Please note that old versions have not been fully tested (but use the same services).
     * Main differences between both versions are in the format of UUID :
     *     - old : "id" : {"mostSigBits": xxx ,"leastSigBits": xxx,"leastSignificantBits": xxx,"mostSignificantBits": xxx }
     *     - new : "id" : "47ca1549-4d8b-4377-8f74-bb887e9e6570"
     */
    
    @GetMapping("/getLastLocationOld") 
    public String getLastLocationOld(@RequestParam String userName) {
    	VisitedLocation visitedLocation = gpsService.getLastUserLocation(userService.getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    
    @GetMapping("/getNearbyAttractionsOld") 
    public String getNearbyAttractionsOld(@RequestParam String userName) throws JsonProcessingException {
    	return JsonStream.serialize(tourGuideService.getNearByAttractions(userName));
    }
    
    @GetMapping("/getRewardsOld") 
    public String getRewardsOld(@RequestParam String userName) throws JsonProcessingException {
    	return JsonStream.serialize(tourGuideService.getUserRewards(userService.getUser(userName)));
    }
    
    @GetMapping("/getAllCurrentLocationsOld")
    public String getAllCurrentLocationsOld() throws JsonProcessingException {
    	return JsonStream.serialize(tourGuideService.getLastLocationAllUsers());
    }
    
    @GetMapping("/getTripDealsOld")
    public String getTripDealsOld(@RequestParam String userName) throws JsonProcessingException {
    	List<Provider> providers = tourGuideService.getTripDeals(userService.getUser(userName));
    	return JsonStream.serialize(providers);
    }
}
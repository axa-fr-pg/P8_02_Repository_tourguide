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
import tourguide.model.User;
import tourguide.service.TourGuideService;
import tourguide.user.UserService;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired TourGuideService tourGuideService;
	@Autowired GpsService gpsService;
	@Autowired UserService userService;
	@Autowired private ObjectMapper objectMapper;
	
	@GetMapping("/")
    public String index() {
        return "Welcome at the TourGuide application !";
    }
    
    @GetMapping("/getLastLocation") 
    public String getLastLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = gpsService.getLastUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    
    @GetMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) throws JsonProcessingException {
 //   	return JsonStream.serialize(tourGuideService.getNearByAttractions(userName));
    	return objectMapper.writeValueAsString(tourGuideService.getNearByAttractions(userName));
    }
    
    @GetMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) throws JsonProcessingException {
//    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    	return objectMapper.writeValueAsString(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @GetMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() throws JsonProcessingException {
//    	return JsonStream.serialize(tourGuideService.getLastLocationAllUsers());
    	return objectMapper.writeValueAsString(tourGuideService.getLastLocationAllUsers());
    }
    
    @GetMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws JsonProcessingException {
//    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return objectMapper.writeValueAsString(providers);
    }
    
    @GetMapping("/getUser")
    private User getUser(@RequestParam 	String userName) {
    	return userService.getUser(userName);
    }
}
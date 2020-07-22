package tourguide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

@Configuration
public class TourGuideModule {
	
	@Bean
	public TripPricer tripPricer() {
		return new TripPricer();
	}
}

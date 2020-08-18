package tourguide.trip;

import java.util.UUID;

import tripPricer.Provider;

/**
 * Extension of the Provider class provided in the read only tripPricer library
 * Required for use with the Jackson object mapper
 * 
 */
public class ProviderWithEmptyConstructor extends Provider {

	public ProviderWithEmptyConstructor(UUID tripId, String name, double price) {
		super(tripId, name, price);
	}

	public ProviderWithEmptyConstructor() {
		this(new UUID(0,0), new String(), 0);
	}
}

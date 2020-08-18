package tourguide.model;

import java.util.UUID;

import tripPricer.Provider;

public class ProviderWithEmptyConstructor extends Provider {

	public ProviderWithEmptyConstructor(UUID tripId, String name, double price) {
		super(tripId, name, price);
	}

	public ProviderWithEmptyConstructor() {
		this(new UUID(0,0), new String(), 0);
	}
}

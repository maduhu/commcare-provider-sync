package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProviderDetailsResponseTest {
    @Test
    public void shouldCheckIfThereAreProviders() {
        ProviderDetailsResponse providerDetailsResponse = new ProviderDetailsResponse();
        assertTrue(providerDetailsResponse.hasNoProviders());

        providerDetailsResponse.setProviders(new ArrayList<Provider>());
        assertTrue(providerDetailsResponse.hasNoProviders());

        providerDetailsResponse.setProviders(new ArrayList<Provider>() {{
            add(new Provider());
        }});
        assertFalse(providerDetailsResponse.hasNoProviders());
    }
}

package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;
import org.motechproject.commcare.provider.sync.TestUtils;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ProviderDetailsResponseTest {
    @Test
    public void shouldMapResponseStringToProviderDetails() {
        String responseString = "{\n" +
                "    \"meta\": {\n" +
                "        \"limit\": 2,\n" +
                "        \"next\": \"?limit=2&offset=2\",\n" +
                "        \"offset\": 0,\n" +
                "        \"previous\": null,\n" +
                "        \"total_count\": 601\n" +
                "    },\n" +
                "    \"objects\": [\n" +
                "        {\n" +
                "            \"default_phone_number\": \"8294168471\",\n" +
                "            \"email\": \"\",\n" +
                "            \"first_name\": \"Dr.Pramod\",\n" +
                "            \"groups\": [\n" +
                "                \"89fda0284e008d2e0c980fb13fb63886\",\n" +
                "                \"89fda0284e008d2e0c980fb13fb66a7b\",\n" +
                "                \"89fda0284e008d2e0c980fb13fb72931\",\n" +
                "                \"89fda0284e008d2e0c980fb13fb76c43\",\n" +
                "                \"89fda0284e008d2e0c980fb13fb7dcf2\",\n" +
                "                \"89fda0284e008d2e0c980fb13fb8f9f3\",\n" +
                "                \"89fda0284e008d2e0c980fb13fbc20ab\",\n" +
                "                \"89fda0284e008d2e0c980fb13fbda82a\",\n" +
                "                \"89fda0284e008d2e0c980fb13fc18199\"\n" +
                "            ],\n" +
                "            \"id\": \"b0645df855266f29849eb2515b5ed57c\",\n" +
                "            \"last_name\": \"Kumar Gautam\",\n" +
                "            \"phone_numbers\": [\n" +
                "                \"8294168471\"\n" +
                "            ],\n" +
                "            \"resource_uri\": \"\",\n" +
                "            \"user_data\": {\n" +
                "                \"asset-id\": \"MP818\",\n" +
                "                \"awc-code\": \"\",\n" +
                "                \"block\": \"Sonbarsa\",\n" +
                "                \"district\": \"\",\n" +
                "                \"imei-no\": \"351971057712199\",\n" +
                "                \"location-code\": \"\",\n" +
                "                \"panchayat\": \"\",\n" +
                "                \"role\": \"MOIC\",\n" +
                "                \"subcentre\": \"\",\n" +
                "                \"user_type\": \"\",\n" +
                "                \"village\": \"\"\n" +
                "            },\n" +
                "            \"username\": \"8294168471@care-bihar.commcarehq.org\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"default_phone_number\": \"8294168748\",\n" +
                "            \"email\": \"\",\n" +
                "            \"first_name\": \"Henarry\",\n" +
                "            \"groups\": [\n" +
                "                \"89fda0284e008d2e0c980fb13fb72b68\",\n" +
                "                \"89fda0284e008d2e0c980fb13fb7ce36\",\n" +
                "                \"89fda0284e008d2e0c980fb13fbb2923\",\n" +
                "                \"89fda0284e008d2e0c980fb13fbdd4f3\",\n" +
                "                \"89fda0284e008d2e0c980fb13fbeec02\",\n" +
                "                \"89fda0284e008d2e0c980fb13fc06332\",\n" +
                "                \"89fda0284e008d2e0c980fb13fc16a44\"\n" +
                "            ],\n" +
                "            \"id\": \"b0645df855266f29849eb2515b5ee454\",\n" +
                "            \"last_name\": \"Turner\",\n" +
                "            \"phone_numbers\": [\n" +
                "                \"8294168748\"\n" +
                "            ],\n" +
                "            \"resource_uri\": \"\",\n" +
                "            \"user_data\": {\n" +
                "                \"asset-id\": \"MP1069\",\n" +
                "                \"awc-code\": \"\",\n" +
                "                \"block\": \"Sattarkatyea\",\n" +
                "                \"district\": \"\",\n" +
                "                \"imei-no\": \"352445051471325\",\n" +
                "                \"location-code\": \"\",\n" +
                "                \"panchayat\": \"\",\n" +
                "                \"role\": [\"BHM\", \"ASHA\"],\n" +
                "                \"subcentre\": \"\",\n" +
                "                \"user_type\": \"\",\n" +
                "                \"village\": \"\"\n" +
                "            },\n" +
                "            \"username\": \"8294168748@care-bihar.commcarehq.org\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        ProviderDetailsResponse providerDetailsResponse = TestUtils.fromJson(responseString, ProviderDetailsResponse.class);

        List<Provider> providers = providerDetailsResponse.getRecords();
        assertEquals(2, providers.size());

        assertEquals("MOIC", providers.get(0).getUserData().get("role"));

        List<String> roles = (List<String>) providers.get(1).getUserData().get("role");
        assertEquals("BHM", roles.get(0));
        assertEquals("ASHA", roles.get(1));
    }
}

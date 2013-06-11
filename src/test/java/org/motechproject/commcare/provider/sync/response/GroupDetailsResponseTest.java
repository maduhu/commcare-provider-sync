package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;
import org.motechproject.commcare.provider.sync.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GroupDetailsResponseTest {

    @Test
    public void shouldMapResponseStringToGroupDetails() {
        String responseString = "{\n" +
                "    \"meta\": {\n" +
                "        \"limit\": 2,\n" +
                "        \"next\": \"?limit=2&offset=2\",\n" +
                "        \"offset\": 0,\n" +
                "        \"previous\": null,\n" +
                "        \"total_count\": 410\n" +
                "    },\n" +
                "    \"objects\": [\n" +
                "        {\n" +
                "            \"case_sharing\": true,\n" +
                "            \"domain\": \"care-bihar\",\n" +
                "            \"id\": \"3c5a80e4db53049dfc110c368a0d05d4\",\n" +
                "            \"metadata\": {\n" +
                "                \"awc-code\": \"myawc-code\"\n" +
                "            },\n" +
                "            \"name\": \"danny team 1\",\n" +
                "            \"path\": [],\n" +
                "            \"reporting\": true,\n" +
                "            \"resource_uri\": \"\",\n" +
                "            \"users\": []\n" +
                "        },\n" +
                "        {\n" +
                "            \"case_sharing\": true,\n" +
                "            \"domain\": \"care-bihar\",\n" +
                "            \"id\": \"3c5a80e4db53049dfc110c368a0d1570\",\n" +
                "            \"metadata\": {\n" +
                "                \"awc-code\": [\"myawc-code1\", \"myawc-code2\"]\n" +
                "            },\n" +
                "            \"name\": \"afrisis team 1\",\n" +
                "            \"path\": [],\n" +
                "            \"reporting\": true,\n" +
                "            \"resource_uri\": \"\",\n" +
                "            \"users\": [\n" +
                "                \"67bffa913b38e7901851d863eded0809\",\n" +
                "                \"67bffa913b38e7901851d863edecfb4a\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        GroupDetailsResponse groupDetailsResponse = TestUtils.fromJson(responseString, GroupDetailsResponse.class);

        List<Group> groups = groupDetailsResponse.getRecords();

        assertEquals(2, groups.size());
        assertEquals("myawc-code", groups.get(0).getMetaData().get("awc-code"));

        List<String> awcCodes = (List<String>) groups.get(1).getMetaData().get("awc-code");
        assertEquals("myawc-code1", awcCodes.get(0));
        assertEquals("myawc-code2", awcCodes.get(1));

    }
}

package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupDetailsResponseTest {
    @Test
    public void shouldCheckIfThereAreGroups() {
        GroupDetailsResponse groupDetailsResponse = new GroupDetailsResponse();
        assertTrue(groupDetailsResponse.hasNoGroups());

        groupDetailsResponse.setGroups(new ArrayList<Group>());
        assertTrue(groupDetailsResponse.hasNoGroups());

        groupDetailsResponse.setGroups(new ArrayList<Group>() {{
            add(new Group());
        }});
        assertFalse(groupDetailsResponse.hasNoGroups());
    }
}

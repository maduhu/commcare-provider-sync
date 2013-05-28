package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MetaTest {
    @Test
    public void shouldCheckIfMetaHasNextQueryParams(){
        Meta meta = new Meta("?offset=0&limit=0&format=json");
        assertTrue(meta.hasNext());

        meta = new Meta();
        assertFalse(meta.hasNext());
    }
}

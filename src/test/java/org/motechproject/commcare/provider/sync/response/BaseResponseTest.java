package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseResponseTest {
    @Test
    public void shouldSayIfMoreRecordsNeedToBeFetchedByCheckingTheNextQueryParam(){
        BaseResponse baseResponse = new BaseResponse();
        assertFalse(baseResponse.hasMoreRecordsToFetch());

        baseResponse = new BaseResponse(new Meta());
        assertFalse(baseResponse.hasMoreRecordsToFetch());

        baseResponse = new BaseResponse(new Meta("?offset=0&limit=0&format=json"));
        assertTrue(baseResponse.hasMoreRecordsToFetch());
    }
}

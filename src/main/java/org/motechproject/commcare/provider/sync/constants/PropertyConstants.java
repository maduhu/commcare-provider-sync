package org.motechproject.commcare.provider.sync.constants;

public class PropertyConstants {
    public static final String PROVIDER_SYNC_CRON_EXPRESSION = "provider.sync.cron.expression";
    public static final String GROUP_SYNC_CRON_EXPRESSION = "group.sync.cron.expression";

    public static final String USERNAME = "commcare.authentication.username";
    public static final String PASSWORD = "commcare.authentication.password";
    
    public static final String COMMCARE_BASE_URL = "commcare.base.url";
    public static final String COMMCARE_GET_PROVIDER_LIST_API_URL = "commcare.get.provider.list.api.url";
    public static final String COMMCARE_GET_GROUP_LIST_API_URL = "commcare.get.group.list.api.url";
    public static final String PROVIDER_BATCH_SIZE = "commcare.get.provider.batch.size";
    public static final String GROUP_BATCH_SIZE = "commcare.get.group.batch.size";

    public static final String URL_PARAMS = "?offset=0&limit=%s&format=json";
}

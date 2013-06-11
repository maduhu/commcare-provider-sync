package org.motechproject.commcare.provider.sync.constants;

public class PropertyConstants {
    public static final String PROVIDER_SYNC_CRON_EXPRESSION = "sync.cron.expression.provider";
    public static final String GROUP_SYNC_CRON_EXPRESSION = "sync.cron.expression.group";

    public static final String USERNAME = "commcare.authentication.username";
    public static final String PASSWORD = "commcare.authentication.password";
    
    public static final String COMMCARE_BASE_URL = "commcare.base.url";

    public static final String COMMCARE_LIST_URL = "commcare.list.url.%s";
    public static final String COMMCARE_BATCH_SIZE = "commcare.batch.size.%s";

    public static final String URL_PARAMS = "?offset=%s&limit=%s&format=json";
}

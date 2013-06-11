package org.motechproject.commcare.provider.sync.response;

public enum BatchJobType {
    PROVIDER("provider", ProviderDetailsResponse.class), GROUP("group", GroupDetailsResponse.class);
    private String qualifier;
    private Class responseType;

    private BatchJobType(String qualifier, Class responseType) {
        this.qualifier = qualifier;
        this.responseType = responseType;
    }
    
    public String qualify(String propertyName) {
        return String.format(propertyName, qualifier);
    }

    public Class commcareResponseType() {
        return responseType;
    }
}

package org.motechproject.commcare.provider.sync.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Provider implements Serializable {
    @JsonProperty
    private String id;
    @JsonProperty
    private String username;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("default_phone_number")
    private String defaultPhoneNumber;
    @JsonProperty
    private String email;
    @JsonProperty("phone_numbers")
    private List<String> phoneNumbers;
    @JsonProperty
    private List<String> groups;
    @JsonProperty("user_data")
    private Map<String, String> userData;
    @JsonProperty("resource_uri")
    private String resourceURI;

    public Provider() {
    }

    public Map<String, String> getUserData() {
        return userData;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

package org.motechproject.commcare.provider.sync.response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class Group {
    @JsonProperty("case_sharing")
    private Boolean caseSharing;
    @JsonProperty
    private String domain;
    @JsonProperty
    private String id;
    @JsonProperty("metadata")
    private Map<String, String> metaData;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<String> path;
    @JsonProperty
    private Boolean reporting;
    @JsonProperty("resource_uri")
    private String resourceUri;
    @JsonProperty
    private List<String> users;

    public Group() {
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }
}

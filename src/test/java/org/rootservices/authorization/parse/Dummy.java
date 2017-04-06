package org.rootservices.authorization.parse;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class used to test injecting values into annotated fields.
 */
public class Dummy {
    @Parameter(name="string", expected = {"string1"})
    private String string;

    @Parameter(name="uuid")
    private UUID id;

    @Parameter(name="uri")
    private URI uri;

    @Parameter(name="strings")
    private List<String> strings;

    @Parameter(name="uuids")
    private List<UUID> ids;

    @Parameter(name="uris")
    private List<URI> uris;

    @Parameter(name="opt_string", required = false)
    private Optional<String> optString;

    @Parameter(name="opt_uuid", required = false)
    private Optional<UUID> optId;

    @Parameter(name="opt_uri", required = false)
    private Optional<URI> optUri;

    @Parameter(name="opt_list", required = false)
    private List<String> optList;

    private String notAnnotated;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public List<URI> getUris() {
        return uris;
    }

    public void setUris(List<URI> uris) {
        this.uris = uris;
    }

    public Optional<String> getOptString() {
        return optString;
    }

    public void setOptString(Optional<String> optString) {
        this.optString = optString;
    }

    public Optional<UUID> getOptId() {
        return optId;
    }

    public void setOptId(Optional<UUID> optId) {
        this.optId = optId;
    }

    public Optional<URI> getOptUri() {
        return optUri;
    }

    public void setOptUri(Optional<URI> optUri) {
        this.optUri = optUri;
    }

    public List<String> getOptList() {
        return optList;
    }

    public void setOptList(List<String> optList) {
        this.optList = optList;
    }

    public String getNotAnnotated() {
        return notAnnotated;
    }

    public void setNotAnnotated(String notAnnotated) {
        this.notAnnotated = notAnnotated;
    }
}

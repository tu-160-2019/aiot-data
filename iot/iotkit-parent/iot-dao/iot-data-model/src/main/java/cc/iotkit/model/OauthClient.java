package cc.iotkit.model;

import lombok.Data;

/**
 * oauth2的client
 */
@Data
public class OauthClient implements Id<String> {

    private String id;

    private String clientId;

    private String name;

    private String clientSecret;

    private String allowUrl;

    private Long createAt;

}

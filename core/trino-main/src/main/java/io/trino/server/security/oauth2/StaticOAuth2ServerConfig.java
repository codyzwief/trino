/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.server.security.oauth2;

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Optional;

public class StaticOAuth2ServerConfig
{
    public static final String ACCESS_TOKEN_ISSUER = "http-server.authentication.oauth2.access-token-issuer";
    public static final String AUTH_URL = "http-server.authentication.oauth2.auth-url";
    public static final String TOKEN_URL = "http-server.authentication.oauth2.token-url";
    public static final String JWKS_URL = "http-server.authentication.oauth2.jwks-url";
    public static final String USERINFO_URL = "http-server.authentication.oauth2.userinfo-url";
    public static final String END_SESSION_URL = "http-server.authentication.oauth2.end-session-url";

    private Optional<String> accessTokenIssuer = Optional.empty();
    private URI authUrl;
    private URI tokenUrl;
    private URI jwksUrl;
    private Optional<URI> userinfoUrl = Optional.empty();
    private Optional<URI> endSessionUrl = Optional.empty();

    @NotNull
    public Optional<String> getAccessTokenIssuer()
    {
        return accessTokenIssuer;
    }

    @Config(ACCESS_TOKEN_ISSUER)
    @ConfigDescription("The required issuer for access tokens")
    public StaticOAuth2ServerConfig setAccessTokenIssuer(String accessTokenIssuer)
    {
        this.accessTokenIssuer = Optional.ofNullable(accessTokenIssuer);
        return this;
    }

    @NotNull
    public URI getAuthUrl()
    {
        return authUrl;
    }

    @Config(AUTH_URL)
    @ConfigDescription("URL of the authorization server's authorization endpoint")
    public StaticOAuth2ServerConfig setAuthUrl(URI authUrl)
    {
        this.authUrl = authUrl;
        return this;
    }

    @NotNull
    public URI getTokenUrl()
    {
        return tokenUrl;
    }

    @Config(TOKEN_URL)
    @ConfigDescription("URL of the authorization server's token endpoint")
    public StaticOAuth2ServerConfig setTokenUrl(URI tokenUrl)
    {
        this.tokenUrl = tokenUrl;
        return this;
    }

    @NotNull
    public URI getJwksUrl()
    {
        return jwksUrl;
    }

    @Config(JWKS_URL)
    @ConfigDescription("URL of the authorization server's JWKS (JSON Web Key Set) endpoint")
    public StaticOAuth2ServerConfig setJwksUrl(URI jwksUrl)
    {
        this.jwksUrl = jwksUrl;
        return this;
    }

    public Optional<URI> getUserinfoUrl()
    {
        return userinfoUrl;
    }

    @Config(USERINFO_URL)
    @ConfigDescription("URL of the userinfo endpoint")
    public StaticOAuth2ServerConfig setUserinfoUrl(URI userinfoUrl)
    {
        this.userinfoUrl = Optional.ofNullable(userinfoUrl);
        return this;
    }

    public Optional<URI> getEndSessionUrl()
    {
        return endSessionUrl;
    }

    @Config(END_SESSION_URL)
    @ConfigDescription("URL of the end session endpoint")
    public StaticOAuth2ServerConfig setEndSessionUrl(URI endSessionUrl)
    {
        this.endSessionUrl = Optional.ofNullable(endSessionUrl);
        return this;
    }
}

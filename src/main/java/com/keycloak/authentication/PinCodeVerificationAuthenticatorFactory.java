package com.keycloak.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class PinCodeVerificationAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "pincode-verification-authenticator";
    private static final PinCodeVerificationAuthenticator SINGLETON = new PinCodeVerificationAuthenticator();

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES;

    static {
        CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
                .property()
                .name("externalApiUrl")
                .label("External API URL")
                .helpText("URL of the external API to verify pincodes against.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .required(true)
                .defaultValue("YOUR_EXTERNAL_API_ENDPOINT") // Default value - remember to change in Keycloak Admin
                .add().build();
    }


    @Override
    public String getDisplayType() {
        return "Pincode Verification";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true; // Configurable because of API URL
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Verifies pincode against an external API.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void init(Config.Scope config) {
        // No special initialization needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post-initialization needed
    }

    @Override
    public void close() {
        // No resources to close
    }
}

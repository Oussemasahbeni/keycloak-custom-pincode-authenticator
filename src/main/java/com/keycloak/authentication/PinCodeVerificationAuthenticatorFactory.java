package com.keycloak.authentication;

import java.util.List;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class PinCodeVerificationAuthenticatorFactory implements AuthenticatorFactory {
  public static final String PROVIDER_ID = "pincode-verification-authenticator";
  private static final PinCodeVerificationAuthenticator SINGLETON =
      new PinCodeVerificationAuthenticator();

//  private static final List<ProviderConfigProperty> CONFIG_PROPERTIES;

//  static {
//    CONFIG_PROPERTIES =
//        ProviderConfigurationBuilder.create()
//            .property()
//            .name("externalApiUrl")
//            .label("External API URL")
//            .helpText("URL of the external API to verify pincodes against.")
//            .type(ProviderConfigProperty.STRING_TYPE)
//            .required(true)
//            .defaultValue("YOUR_EXTERNAL_API_ENDPOINT") // Default value
//            .add()
//            .property()
//            .name("clientsRequiringPinVerification")
//            .label("Clients Requiring PIN Verification")
//            .helpText(
//                "Client ids that should require PIN verification. Enter client ids separated by commas. You can find client ids in the 'Clients' section of your realm.")
//            .type(ProviderConfigProperty.STRING_TYPE)
//            .required(false)
//            .defaultValue("sabeel-web-app")
//            .add()
//            .build();
//  }

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
//    return CONFIG_PROPERTIES;
    return null;
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

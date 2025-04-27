package com.keycloak.authentication;

import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;

public class PinCodeVerificationAuthenticator implements Authenticator {

  private static final Logger log = Logger.getLogger(PinCodeVerificationAuthenticator.class);

  private static final String PINCODE_FORM_FIELD = "pincode";

  @Override
  public void authenticate(AuthenticationFlowContext context) {

    log.info("Started pincode checking");

    AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();

    String clientId = context.getAuthenticationSession().getClient().getClientId();
    log.info("Client id: " + clientId);

    String clientsRequiringPinVerificationConfig =
        configModel.getConfig().get("clientsRequiringPinVerification");
    Set<String> allowedClientNames = new HashSet<>();

    if (clientsRequiringPinVerificationConfig != null
        && !clientsRequiringPinVerificationConfig.trim().isEmpty()) {

      allowedClientNames.addAll(Arrays.asList(clientsRequiringPinVerificationConfig.split(",")));
    }

    if (allowedClientNames.isEmpty() || !allowedClientNames.contains(clientId)) {
      log.info(
          "Skipping PIN verification for client: "
              + clientId
              + " as it's not in the clients that require PIN verification: "
              + allowedClientNames);
      context.success();
      return;
    }

    log.info("Pin verification required for client: " + clientId);
    LoginFormsProvider forms = context.form();
    forms.setExecution(context.getExecution().getId());
    context.challenge(forms.createForm("verify-pincode.ftl"));
  }

  @Override
  public void action(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    String pincode = formData.getFirst(PINCODE_FORM_FIELD);

    log.info("Received pincode: " + pincode);

    if (pincode == null || pincode.isEmpty()) {
      log.info("Pincode is missing or empty");
      LoginFormsProvider forms = context.form();
      context.form().setError("requiredPincode");
      context.challenge(forms.createForm("verify-pincode.ftl"));
      return;
    }

    context.getAuthenticationSession().setAuthNote(PINCODE_FORM_FIELD, pincode);

    try {
      if (RestClient.sendRequest(pincode) == 200) {
        log.info("Pincode verification successful");
        context.success();
      } else {
        log.info("Invalid pincode provided");
        LoginFormsProvider forms = context.form();
        forms.setExecution(context.getExecution().getId());
        context.form().setError("invalidPincode");
        context.challenge(forms.createForm("verify-pincode.ftl"));
      }
    } catch (Exception e) {
      log.error("Error verifying pincode: " + e.getMessage());
      LoginFormsProvider forms = context.form();
      forms.setExecution(context.getExecution().getId());
      forms.addError(new FormMessage("verifyPinCodeErrorMessage"));
      context.challenge(forms.createForm("verify-pincode.ftl"));
    }
  }

  @Override
  public boolean requiresUser() {
    return false;
  }

  @Override
  public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
    return true; // Always configured
  }

  @Override
  public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    // No required actions needed
  }

  @Override
  public void close() {
    // Clean up resources
  }
}

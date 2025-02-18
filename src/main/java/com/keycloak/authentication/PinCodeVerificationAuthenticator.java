package com.keycloak.authentication;

import jakarta.ws.rs.core.MultivaluedMap;
import java.util.List;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;

public class PinCodeVerificationAuthenticator implements Authenticator {

  private static final Logger log = Logger.getLogger(PinCodeVerificationAuthenticator.class);

  private static final String PINCODE_FORM_FIELD = "pincode";
  private static final String BACKEND_BASE_URL = "BACKEND_BASE_URL";
  private static final List<String> CLIENTS_REQUIRING_PIN_VERIFICATION = List.of("sabeel-web-app");

  @Override
  public void authenticate(AuthenticationFlowContext context) {

    //    log.info("--- AuthenticationFlowContext Information ---");
    //    log.info("Execution ID: " + context.getExecution().getId());
    //    log.info("Flow Authenticator: " + context.getExecution().getAuthenticator());
    //    log.info("Flow ID: " + context.getExecution().getFlowId());
    //    log.info("Flow Path: " + context.getFlowPath());
    //    log.info("Authentication Session: " + context.getAuthenticationSession());
    //    if (context.getAuthenticationSession() != null) {
    //      log.info(
    //          "  Client ID in Session: "
    //              + context.getAuthenticationSession().getClient().getClientId());
    //      log.info("  User in Session: " +
    // context.getAuthenticationSession().getAuthenticatedUser());
    //    } else {
    //      log.info("  No Authentication Session Details Available.");
    //    }
    //    log.info("Realm: " + context.getRealm().getName());
    //    log.info("Authenticator Config Model: " + context.getAuthenticatorConfig());
    //    if (context.getAuthenticatorConfig() != null) {
    //      log.info("  Authenticator Config ID: " + context.getAuthenticatorConfig().getId());
    //      log.info("  Authenticator Config Config: " +
    // context.getAuthenticatorConfig().getConfig());
    //    } else {
    //      log.info("  Authenticator Config is NULL.");
    //    }
    //    log.info("--- End AuthenticationFlowContext Information ---");
    //    AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();

    log.info("Started pincode checking");

    String clientId = context.getAuthenticationSession().getClient().getClientId();
    log.info("Client id: " + clientId);

    //    String clientsRequiringPinVerificationConfig =
    //        configModel.getConfig().get("clientsRequiringPinVerification");
    //    Set<String> allowedClientNames = new HashSet<>();
    //
    //    if (clientsRequiringPinVerificationConfig != null
    //        && !clientsRequiringPinVerificationConfig.trim().isEmpty()) {
    //
    // allowedClientNames.addAll(Arrays.asList(clientsRequiringPinVerificationConfig.split(",")));
    //    }

    //    if (allowedClientNames.isEmpty() || !allowedClientNames.contains(clientId)) {
    //      log.info(
    //          "Skipping PIN verification for client: "
    //              + clientId
    //              + " as it's not in the clients that require PIN verification: "
    //              + allowedClientNames);
    //      context.success();
    //      return;
    //    }

    if (CLIENTS_REQUIRING_PIN_VERIFICATION.isEmpty()
        || !CLIENTS_REQUIRING_PIN_VERIFICATION.contains(clientId)) {
      log.info(
          "Skipping PIN verification for client: "
              + clientId
              + " as it's not in the clients that require PIN verification: "
              + CLIENTS_REQUIRING_PIN_VERIFICATION);
      context.success();
      return;
    }

    log.info("Pin verification required for client: " + clientId);
    LoginFormsProvider forms = context.form();
    forms.setExecution(context.getExecution().getId());
    context.challenge(forms.createForm("verify-pincode.ftl"));

    //     String userAgent =
    // context.getHttpRequest().getHttpHeaders().getHeaderString("User-Agent");
    //      boolean isMobile = userAgent != null &&
    // userAgent.matches(".*(Mobile|Android|iPhone|iPad).*");
    //        if (isMobile) {
    //            log.info("Processing action for mobile device");
    //            context.success();
    //        } else {
    //            log.info("Processing action for non-mobile device");
    //            LoginFormsProvider forms = context.form();
    //            forms.setExecution(context.getExecution().getId());
    //            context.challenge(forms.createForm("verify-pincode.ftl"));
    //        }
    // Show the form to enter the PIN

  }

  @Override
  public void action(AuthenticationFlowContext context) {

    String apiUrl = System.getenv(BACKEND_BASE_URL);
    log.debugf("BACKEND_BASE_URL: %s", apiUrl);

    if (apiUrl == null || apiUrl.isEmpty()) {
      throw new IllegalArgumentException(
          "Environment variable BACKEND_URL is not set or is empty.");
    }

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

package com.keycloak.authenticator;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import java.util.Map;

public class PinCodeVerificationAuthenticator implements Authenticator {

    private static final String PINCODE_FORM_FIELD = "pincode";
    private static final String EXTERNAL_API_URL = "YOUR_EXTERNAL_API_ENDPOINT"; // **IMPORTANT: Configure this in Factory!**

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String pincode = formData.getFirst(PINCODE_FORM_FIELD);

        if (pincode == null || pincode.isEmpty()) {
            LoginFormsProvider forms = context.form();
            forms.setExecution(context.getExecution().getId());
            forms.addError(new FormMessage("Pincode is required"));
            context.challenge(forms.createForm("pincode-verify.ftl"));
            return;
        }

        // A simple check to verify the pincode
        if (!pincode.equals("1234")) {
            LoginFormsProvider forms = context.form();
            forms.setExecution(context.getExecution().getId());
            forms.addError(new FormMessage("Invalid Pincode"));
            context.challenge(forms.createForm("pincode-verify.ftl"));
            return;
        }else {
            context.success();
        }

//        try {
//            Client client = ClientBuilder.newClient();
//            Response apiResponse = client.target(EXTERNAL_API_URL) // Using configured URL
//                    .request(MediaType.APPLICATION_JSON_TYPE)
//                    .post(Entity.json(Map.of("pincode", pincode)));
//
//            if (apiResponse.getStatus() == 200) {
//                context.success();
//            } else {
//                LoginFormsProvider forms = context.form();
//                forms.setExecution(context.getExecution().getId());
//                forms.addError(new FormMessage("Invalid Pincode"));
//                context.challenge(forms.createForm("pincode-verify.ftl"));
//            }
//        } catch (Exception e) {
//            LoginFormsProvider forms = context.form();
//            forms.setExecution(context.getExecution().getId());
//            forms.addError(new FormMessage("Error verifying pincode. Please try again later."));
//            context.challenge(forms.createForm("pincode-verify.ftl"));
//            // Log error: context.getSession().getContext().getRealm().getAdminEventProvider().onEvent(...);
//        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // No action needed in this example
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

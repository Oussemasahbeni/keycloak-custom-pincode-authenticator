# SAB Pincode Authenticator

## Overview

`sab-pincode-authenticator` is a custom authenticator for Keycloak. It enhances the standard authentication process by requiring users to enter a Pincode that is verified against an external API. This provides an additional layer of security, ensuring that users are validated through a secondary mechanism before gaining access.

This authenticator can be selectively applied to specific Keycloak clients.

## Features

*   **Keycloak Integration:** Seamlessly integrates as a custom execution within Keycloak authentication flows.
*   **Pincode Prompt:** Presents users with a dedicated form to enter their Pincode.
*   **External API Verification:** Validates the entered Pincode by making a request to a configurable external backend service.
*   **Client-Specific Application:** Can be configured to be active only for specified Keycloak clients.

## Configuration

To use this authenticator, you need to configure the following:

1.  **Backend API URL:**
    *   The authenticator communicates with an external API to verify the Pincode.
    *   Set the `SAB_BACKEND_BASE_URL` environment variable in your Keycloak server environment. This variable should point to the base URL of your backend service that handles Pincode verification (e.g., `http://localhost:8080` or `https://api.example.com`). The authenticator will append `/api/v1/codes/verify/{pincode}` to this base URL.

2.  **Authenticator Configuration in Keycloak:**
    *   After deploying the authenticator, add it to an authentication flow in the Keycloak admin console.
    *   This authenticator has one configuration property:
        *   **Clients Requiring PIN Verification (`clientsRequiringPinVerification`):**
            *   **Label:** "Clients Requiring PIN Verification"
            *   **Help Text:** "Client ids that should require PIN verification. Enter client ids separated by commas. You can find client ids in the 'Clients' section of your realm."
            *   **Type:** String
            *   **Required:** No (If left empty, or if a client is not in this list, the Pincode verification step will be skipped for that client).
            *   **Default Value:** `sabeel-web-app` (You can change or clear this).
            *   Enter a comma-separated list of Client IDs for which this Pincode verification should be enforced.

## Build Instructions

This project is built using Apache Maven.

1.  Ensure you have Java (version 21 or as specified in `pom.xml`) and Maven installed.
2.  Clone the repository (if you haven't already).
3.  Navigate to the project's root directory (where `pom.xml` is located).
4.  Run the following command to build the project and create the JAR file:
    ```bash
    mvn clean install
    ```
    This will generate a JAR file (e.g., `sab-pincode-authenticator-1.0.jar`) in the `target` directory.

## Deployment Instructions

To deploy the custom authenticator to Keycloak:

1.  **Copy the JAR:**
    *   Take the JAR file generated in the `target` directory (e.g., `sab-pincode-authenticator-1.0.jar`).
    *   Copy this JAR file to the `providers` directory of your Keycloak server distribution. (e.g., `/opt/keycloak/providers/`).

2.  **Build Keycloak (if necessary):**
    *   When Keycloak starts, it automatically detects new providers. However, sometimes a re-build of Keycloak's own artifacts is needed, especially if you've made changes or are upgrading.
    *   Navigate to your Keycloak `bin` directory (e.g., `/opt/keycloak/bin`).
    *   Run the build command:
        *   For Linux/macOS: `kc.sh build`
        *   For Windows: `kc.bat build`

3.  **Start/Restart Keycloak:**
    *   Start or restart your Keycloak server. The new authenticator should now be available.

## Usage Instructions

1.  **Log in to Keycloak Admin Console.**
2.  **Select your Realm.**
3.  **Navigate to "Authentication".**
4.  **Create or Copy a Flow:**
    *   You can either create a new authentication flow or copy an existing one (e.g., the "Browser" flow).
5.  **Add Execution:**
    *   Within your chosen flow, click on "Add execution".
    *   Select "Pincode Verification" (this is the display name of `sab-pincode-authenticator`) from the list of available providers.
6.  **Configure Requirement:**
    *   Set the requirement for the "Pincode Verification" execution (e.g., "Required", "Alternative", "Conditional"). Typically, this would be "Required" if you always want the Pincode step after a certain primary authentication (like username/password).
7.  **Configure Authenticator (Optional):**
    *   If you need to specify which clients this applies to, click on "Actions" -> "Config" for the "Pincode Verification" execution.
    *   Set the "Clients Requiring PIN Verification" field as described in the Configuration section.
8.  **Bind the Flow:**
    *   If you created a new flow or copied one, make sure to bind it to be used for browser authentication (or whichever specific context you need it for) under the "Bindings" tab in the "Authentication" section.

Now, when users from the configured clients attempt to log in, they will be prompted to enter a Pincode after their primary authentication.

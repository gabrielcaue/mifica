package com.mifica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Test application class that excludes security auto-configurations
 * that cause issues in integration tests.
 * 
 * Used in @SpringBootTest(classes = TestApplication.class) to ensure
 * proper test context initialization without OAuth2/SAML2 auto-configs.
 */
@SpringBootApplication(
    exclude = {
        org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration.class
    }
)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}

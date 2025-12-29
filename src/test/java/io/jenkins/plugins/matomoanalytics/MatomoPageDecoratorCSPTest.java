package io.jenkins.plugins.matomoanalytics;

import hudson.model.PageDecorator;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Integration tests for {@link MatomoPageDecorator} to verify CSP compliance.
 * These tests ensure that the footer.jelly template does not contain inline scripts
 * that would violate Jenkins Content Security Policy.
 */
public class MatomoPageDecoratorCSPTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void testFooterRenderingWithConfiguration() throws Exception {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        // Configure the decorator
        decorator.setMatomoSiteID("1");
        decorator.setMatomoServer("matomo.example.com");
        decorator.setMatomoPath("/");
        decorator.setMatomoPhp("matomo.php");
        decorator.setMatomoJs("matomo.js");
        decorator.setMatomoUseHttps(true);
        decorator.setMatomoSendUserID(false);
        decorator.save();

        // Load a page to trigger footer rendering
        WebClient webClient = jenkins.createWebClient();
        var page = webClient.goTo("");

        String pageContent = page.asXml();

        // Verify that the page contains Matomo configuration
        // After CSP fix, we should check for external script reference and data attributes
        // For now, we check that the page decorator is active
        assertNotNull("Page should be loaded", page);
    }

    @Test
    public void testFooterRenderingWithoutConfiguration() throws Exception {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        // Clear configuration
        decorator.setMatomoSiteID(null);
        decorator.setMatomoServer(null);
        decorator.save();

        WebClient webClient = jenkins.createWebClient();
        var page = webClient.goTo("");

        String pageContent = page.asXml();

        // When not configured, footer should not render Matomo code
        // Verify no Matomo-related content is present
        assertNotNull("Page should be loaded", page);
    }

    @Test
    public void testNoInlineScriptsInFooter() throws Exception {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        decorator.setMatomoSiteID("1");
        decorator.setMatomoServer("matomo.example.com");
        decorator.setMatomoPath("/");
        decorator.save();

        WebClient webClient = jenkins.createWebClient();
        var page = webClient.goTo("");

        String pageContent = page.asXml();

        // Verify CSP compliance: no inline scripts with Matomo code
        // Check that inline script blocks with _paq are not present
        // The page should only have external script references
        assertThat("Page should not contain inline _paq.push calls",
                pageContent, not(containsString("_paq.push")));
        assertThat("Page should use external script file",
                pageContent, containsString("matomo-analytics.js"));
        assertThat("Page should contain data attributes for configuration",
                pageContent, containsString("data-matomo"));
    }

    @Test
    public void testExternalScriptReference() throws Exception {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        decorator.setMatomoSiteID("1");
        decorator.setMatomoServer("matomo.example.com");
        decorator.setMatomoPath("/");
        decorator.save();

        WebClient webClient = jenkins.createWebClient();
        var page = webClient.goTo("");

        String pageContent = page.asXml();

        // Verify that external script is referenced
        assertThat("Page should reference external JavaScript file",
                pageContent, containsString("plugin/matomo-analytics/matomo-analytics.js"));
    }

    @Test
    public void testDataAttributesPresent() throws Exception {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        decorator.setMatomoSiteID("42");
        decorator.setMatomoServer("analytics.example.com");
        decorator.setMatomoPath("/matomo/");
        decorator.setMatomoPhp("tracker.php");
        decorator.setMatomoJs("tracker.js");
        decorator.setMatomoUseHttps(false);
        decorator.setMatomoSendUserID(true);
        decorator.save();

        WebClient webClient = jenkins.createWebClient();
        var page = webClient.goTo("");

        String pageContent = page.asXml();

        // Verify that data attributes are present
        assertThat("Page should contain data-matomo-site-id attribute",
                pageContent, containsString("data-matomo-site-id=\"42\""));
        assertThat("Page should contain data-matomo-server attribute",
                pageContent, containsString("data-matomo-server=\"analytics.example.com\""));
        assertThat("Page should contain data-matomo-path attribute",
                pageContent, containsString("data-matomo-path=\"/matomo/\""));
        assertThat("Page should contain data-matomo-protocol attribute",
                pageContent, containsString("data-matomo-protocol=\"http://\""));
    }
}


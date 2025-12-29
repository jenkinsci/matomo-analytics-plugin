package io.jenkins.plugins.matomoanalytics;

import hudson.model.PageDecorator;
import net.sf.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

/**
 * Tests for configuration and persistence of {@link MatomoPageDecorator}.
 */
public class MatomoPageDecoratorConfigurationTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void testDefaultValues() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);
        
        // Test default values
        assertNull("Default site ID should be null", decorator.getMatomoSiteID());
        assertNull("Default server should be null", decorator.getMatomoServer());
        assertNull("Default path should be null", decorator.getMatomoPath());
        assertNull("Default PHP file should be null", decorator.getMatomoPhp());
        assertNull("Default JS file should be null", decorator.getMatomoJs());
        assertTrue("Default should use HTTPS", decorator.isMatomoUseHttps());
        assertFalse("Default should not send user ID", decorator.isMatomoSendUserID());
    }

    @Test
    public void testConfigurationPersistence() throws Exception {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        // Set configuration
        String siteID = "99";
        String server = "test-matomo.example.com";
        String path = "/analytics/";
        String php = "track.php";
        String js = "track.js";

        decorator.setMatomoSiteID(siteID);
        decorator.setMatomoServer(server);
        decorator.setMatomoPath(path);
        decorator.setMatomoPhp(php);
        decorator.setMatomoJs(js);
        decorator.setMatomoUseHttps(false);
        decorator.setMatomoSendUserID(true);
        decorator.save();

        // Reload Jenkins instance to test persistence
        jenkins.getInstance().reload();

        // Verify configuration persisted
        MatomoPageDecorator reloadedDecorator = jenkins.getInstance()
                .getExtensionList(PageDecorator.class).get(MatomoPageDecorator.class);

        assertEquals("Site ID should persist", siteID, reloadedDecorator.getMatomoSiteID());
        assertEquals("Server should persist", server, reloadedDecorator.getMatomoServer());
        assertEquals("Path should persist", path, reloadedDecorator.getMatomoPath());
        assertEquals("PHP file should persist", php, reloadedDecorator.getMatomoPhp());
        assertEquals("JS file should persist", js, reloadedDecorator.getMatomoJs());
        assertFalse("HTTPS setting should persist", reloadedDecorator.isMatomoUseHttps());
        assertTrue("Send user ID setting should persist", reloadedDecorator.isMatomoSendUserID());
    }

    @Test
    public void testNullValuesHandling() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        // Set some values
        decorator.setMatomoSiteID("1");
        decorator.setMatomoServer("server.com");

        // Set to null
        decorator.setMatomoSiteID(null);
        decorator.setMatomoServer(null);
        decorator.setMatomoPath(null);
        decorator.setMatomoPhp(null);
        decorator.setMatomoJs(null);

        assertNull("Site ID should be null", decorator.getMatomoSiteID());
        assertNull("Server should be null", decorator.getMatomoServer());
        assertNull("Path should be null", decorator.getMatomoPath());
        assertNull("PHP file should be null", decorator.getMatomoPhp());
        assertNull("JS file should be null", decorator.getMatomoJs());
    }

    @Test
    public void testEmptyStringValues() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        decorator.setMatomoSiteID("");
        decorator.setMatomoServer("");
        decorator.setMatomoPath("");

        assertEquals("Empty site ID should be stored", "", decorator.getMatomoSiteID());
        assertEquals("Empty server should be stored", "", decorator.getMatomoServer());
        assertEquals("Empty path should be stored", "", decorator.getMatomoPath());
    }

    @Test
    public void testProtocolStringChanges() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        decorator.setMatomoUseHttps(true);
        assertEquals("Should return https://", "https://", decorator.getProtocolString());

        decorator.setMatomoUseHttps(false);
        assertEquals("Should return http://", "http://", decorator.getProtocolString());

        // Toggle back
        decorator.setMatomoUseHttps(true);
        assertEquals("Should return https:// again", "https://", decorator.getProtocolString());
    }

    @Test
    public void testMultipleConfigurationChanges() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        // First configuration
        decorator.setMatomoSiteID("1");
        decorator.setMatomoServer("server1.com");
        assertEquals("1", decorator.getMatomoSiteID());
        assertEquals("server1.com", decorator.getMatomoServer());

        // Change configuration
        decorator.setMatomoSiteID("2");
        decorator.setMatomoServer("server2.com");
        assertEquals("2", decorator.getMatomoSiteID());
        assertEquals("server2.com", decorator.getMatomoServer());

        // Change again
        decorator.setMatomoSiteID("3");
        decorator.setMatomoServer("server3.com");
        assertEquals("3", decorator.getMatomoSiteID());
        assertEquals("server3.com", decorator.getMatomoServer());
    }
}


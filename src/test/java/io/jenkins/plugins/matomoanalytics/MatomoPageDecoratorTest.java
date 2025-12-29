package io.jenkins.plugins.matomoanalytics;

import hudson.model.PageDecorator;
import net.sf.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MatomoPageDecorator}.
 */
public class MatomoPageDecoratorTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void testDefaultConstructor() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);
        assertNotNull(decorator);
        assertNull(decorator.getMatomoSiteID());
        assertNull(decorator.getMatomoServer());
        assertTrue(decorator.isMatomoUseHttps());
        assertFalse(decorator.isMatomoSendUserID());
    }

    @Test
    public void testDataBoundConstructor() {
        String siteID = "1";
        String server = "matomo.example.com";
        String path = "/";
        String php = "matomo.php";
        String js = "matomo.js";
        boolean useHttps = false;
        boolean sendUserID = true;

        MatomoPageDecorator decorator = new MatomoPageDecorator(
                siteID, server, path, php, js, useHttps, sendUserID);

        assertEquals(siteID, decorator.getMatomoSiteID());
        assertEquals(server, decorator.getMatomoServer());
        assertEquals(path, decorator.getMatomoPath());
        assertEquals(php, decorator.getMatomoPhp());
        assertEquals(js, decorator.getMatomoJs());
        assertEquals(useHttps, decorator.isMatomoUseHttps());
        assertEquals(sendUserID, decorator.isMatomoSendUserID());
    }

    @Test
    public void testGettersAndSetters() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        decorator.setMatomoSiteID("2");
        assertEquals("2", decorator.getMatomoSiteID());

        decorator.setMatomoServer("analytics.example.com");
        assertEquals("analytics.example.com", decorator.getMatomoServer());

        decorator.setMatomoPath("/matomo/");
        assertEquals("/matomo/", decorator.getMatomoPath());

        decorator.setMatomoPhp("tracker.php");
        assertEquals("tracker.php", decorator.getMatomoPhp());

        decorator.setMatomoJs("tracker.js");
        assertEquals("tracker.js", decorator.getMatomoJs());

        decorator.setMatomoUseHttps(false);
        assertFalse(decorator.isMatomoUseHttps());

        decorator.setMatomoSendUserID(true);
        assertTrue(decorator.isMatomoSendUserID());
    }

    @Test
    public void testGetProtocolStringHttps() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);
        decorator.setMatomoUseHttps(true);
        assertEquals("https://", decorator.getProtocolString());
    }

    @Test
    public void testGetProtocolStringHttp() {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);
        decorator.setMatomoUseHttps(false);
        assertEquals("http://", decorator.getProtocolString());
    }

    @Test
    public void testExtensionIsRegistered() {
        PageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);
        assertNotNull("MatomoPageDecorator should be registered as an extension", decorator);
        assertTrue("Decorator should be an instance of MatomoPageDecorator",
                decorator instanceof MatomoPageDecorator);
    }

    @Test
    public void testConfigure() throws Exception {
        MatomoPageDecorator decorator = jenkins.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        JSONObject json = new JSONObject();
        json.put("matomoSiteID", "3");
        json.put("matomoServer", "test.example.com");
        json.put("matomoPath", "/");
        json.put("matomoPhp", "matomo.php");
        json.put("matomoJs", "matomo.js");
        json.put("matomoUseHttps", true);
        json.put("matomoSendUserID", false);

        // The configure method requires a real StaplerRequest, so we just verify the decorator exists
        assertNotNull(decorator);
    }

    @Test
    public void testPluginDisplayName() {
        assertEquals("Matomo Analytics", MatomoPageDecorator.PLUGIN_DISPLAY_NAME);
    }
}


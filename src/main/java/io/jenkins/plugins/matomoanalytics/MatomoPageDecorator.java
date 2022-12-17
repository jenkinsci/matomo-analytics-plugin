package io.jenkins.plugins.matomoanalytics;

import hudson.Extension;
import hudson.model.PageDecorator;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class MatomoPageDecorator extends PageDecorator {

    public static final String PLUGIN_DISPLAY_NAME = "Matomo Analytics";
    private String matomoSiteID;
    private String matomoServer;
    private String matomoPath;
    private boolean matomoUseHttps = true;

    public MatomoPageDecorator() {
        super();
        load();
    }

    @DataBoundConstructor
    public MatomoPageDecorator(String matomoSiteID,
                               String matomoServer,
                               String matomoPath,
                               boolean matomoUseHttps) {
        this();
        setMatomoSiteID(matomoSiteID);
        setMatomoServer(matomoServer);
        setMatomoPath(matomoPath);
        setMatomoUseHttps(matomoUseHttps);
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json)
            throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    public String getMatomoSiteID() {
        return matomoSiteID;
    }

    public void setMatomoSiteID(String matomoSiteID) {
        this.matomoSiteID = matomoSiteID;
    }

    public String getMatomoServer() {
        return matomoServer;
    }

    public void setMatomoServer(String matomoServer) {
        this.matomoServer = matomoServer;
    }

    public String getMatomoPath() {
        return matomoPath;
    }

    public void setMatomoPath(String matomoPath) {
        this.matomoPath = matomoPath;
    }

    public boolean isMatomoUseHttps() {
        return matomoUseHttps;
    }

    public void setMatomoUseHttps(boolean matomoUseHttps) {
        this.matomoUseHttps = matomoUseHttps;
    }

    public String getProtocolString() {
        if(matomoUseHttps) {
            return "https://";
        } else {
            return "http://";
        }
    }
}

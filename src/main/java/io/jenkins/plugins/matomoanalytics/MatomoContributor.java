package io.jenkins.plugins.matomoanalytics;

import hudson.Extension;
import hudson.ExtensionList;
import jenkins.security.csp.Contributor;
import jenkins.security.csp.CspBuilder;
import jenkins.security.csp.Directive;
import org.kohsuke.accmod.restrictions.suppressions.SuppressRestrictedWarnings;

@Extension
@SuppressRestrictedWarnings({Contributor.class, CspBuilder.class})
public class MatomoContributor implements Contributor {
    @Override
    public void apply(CspBuilder cspBuilder) {
        MatomoPageDecorator decorator = ExtensionList.lookupSingleton(MatomoPageDecorator.class);
        String server = decorator.getMatomoServer();
        String siteId = decorator.getMatomoSiteID();
        if (server != null && siteId != null) {
            String matomoJs = decorator.getMatomoJs();
            if (matomoJs == null) {
                matomoJs = "matomo.js";
            }
            String path = decorator.getMatomoPath();
            if (path == null) {
                path = "";
            }
            String scriptUrl = decorator.getProtocolString() + server + path + matomoJs;
            cspBuilder.add(Directive.SCRIPT_SRC, scriptUrl);
        }
    }
}

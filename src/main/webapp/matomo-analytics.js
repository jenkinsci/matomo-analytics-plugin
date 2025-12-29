(function() {
    'use strict';
    
    // Find configuration element
    var configEl = document.getElementById('matomo-config');
    if (!configEl) {
        return;
    }
    
    // Read data attributes
    var server = configEl.getAttribute('data-matomo-server');
    var path = configEl.getAttribute('data-matomo-path') || '';
    var siteId = configEl.getAttribute('data-matomo-site-id');
    var php = configEl.getAttribute('data-matomo-php') || 'matomo.php';
    var js = configEl.getAttribute('data-matomo-js') || 'matomo.js';
    var protocol = configEl.getAttribute('data-matomo-protocol') || 'https://';
    var sendUserId = configEl.getAttribute('data-matomo-send-user-id') === 'true';
    var userId = configEl.getAttribute('data-matomo-user-id') || '';
    var domainsJson = configEl.getAttribute('data-matomo-domains') || '';
    
    // Validate required attributes
    if (!server || !siteId) {
        return;
    }
    
    // Initialize Matomo tracker
    var _paq = window._paq = window._paq || [];
    
    // Set domains if provided
    if (domainsJson && domainsJson.trim() !== '') {
        try {
            var domains = JSON.parse(domainsJson);
            if (domains && domains.length > 0) {
                _paq.push(['setDomains', domains]);
            }
        } catch (e) {
            // Ignore JSON parse errors
        }
    }
    
    // Tracker methods should be called before trackPageView
    _paq.push(['trackPageView']);
    _paq.push(['enableLinkTracking']);
    
    // Handle user ID
    if (sendUserId && userId && userId.trim() !== '') {
        _paq.push(['setUserId', userId.trim()]);
    } else {
        _paq.push(['setDoNotTrack', true]);
    }
    
    // Set tracker URL and site ID
    var trackerUrl = protocol + server + path + php;
    _paq.push(['setTrackerUrl', trackerUrl]);
    _paq.push(['setSiteId', parseInt(siteId, 10)]);
    
    // Dynamically load Matomo JS script
    (function() {
        var d = document;
        var u = protocol + server + path;
        var g = d.createElement('script');
        var s = d.getElementsByTagName('script')[0];
        g.async = true;
        g.src = u + js;
        if (s && s.parentNode) {
            s.parentNode.insertBefore(g, s);
        } else {
            d.head.appendChild(g);
        }
    })();
})();


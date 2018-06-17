(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

var gaID = 'UA-103140141-1';

ga('create', gaID, 'auto');
ga('set', 'anonymizeIp', true);
ga('send', 'pageview');

function gaOptout() {
    var disableStr = 'ga-disable-' + gaID;
    document.cookie = disableStr + '=true; domain=' + window.location.hostname + '; expires=Thu, 31 Dec 2099 23:59:59 UTC; path=/';
    window[disableStr] = true;
}

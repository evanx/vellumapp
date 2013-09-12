
    
function clickAbout() {
    console.log("clickAbout");
    $(".croc-nav-anchor").removeClass("active");
    $(".croc-info").hide();
    $("#croc-info-about").show();
}

function clickHome() {
    $(".croc-nav-anchor").removeClass("active");
    $(".croc-info").hide();
    $("#croc-info-landing").show();
}        

function clickContact() {
    $(".croc-nav-anchor").removeClass("active");
    $(".croc-info").hide();
    $("#croc-info-contact").show();
}

function startDocument() {
    console.log("startDocument");
    if (!redirectDocument()) {
        initDocument();
    }
}

function initDocument() {
    console.log("initDocument");
    $(".croc-home-clickable").click(clickHome);
    $(".croc-about-clickable").click(clickAbout);
    $(".croc-contact-clickable").click(clickContact);    
    initPersona();
}

var currentUser = null;

function clickLoginPersona() {
    console.log("login persona");
    navigator.id.request();
}

function clickLogoutPersona() {
    console.log("logout persona");
    navigator.id.logout();
}

function initPersona() {
    $(".croc-login-viewable").show();
    $(".croc-login-clickable").show();
    $('.croc-login-clickable').click(clickLoginPersona);
    $('.croc-logout-clickable').click(clickLogoutPersona);
    navigator.id.watch({
        loggedInUser: currentUser,
        onlogin: function(assertion) {
            console.log("onlogin");
            $.ajax({ 
                type: 'POST',    
                url: '/loginPersona',
                data: {
                    assertion: assertion
                },
                success: function(res, status, xhr) {
                    console.log("success");
                    console.log(res);
                    currentUser = res.email;
                    processLoginPersona(res);
                },
                error: function(xhr, status, err) {
                    console.log("error");
                }
            });
        },
        onlogout: function() {
            console.log("onlogout");
            currentUser = null;
            $.ajax({ 
                type: 'POST',    
                url: '/logoutPersona',
                success: function(res, status, xhr) {
                    console.log("success");
                    processLogout(res);
                },
                error: function(xhr, status, err) {
                    console.log("error");
                }
            });
        }
    });
}

function redirectDocument() {
    console.log("redicrectDocument " + window.location.protocol);
    if (window.location.protocol != "https:") {
        var host = location.host;
        var index = location.host.indexOf(':');
        if (index > 0) {
            host = location.host.substring(0, index) + ':8443';
        }
        window.location = "https://" + host + location.pathname + location.search + location.hash;
        console.log(window.location);
        return true;
    }
    return false;
}

function startClient() {
    gapi.client.setApiKey(apiKey);
    window.setTimeout(checkAuth, 1);
}

function checkAuth() {
    gapi.auth.authorize({
        client_id: clientId, 
        scope: scopes, 
        immediate: true
    }, processAuthResult);
}

function processAuthResult(authResult) {
    if (authResult && !authResult.error) {
        showBusyAuth();
        login(authResult.access_token);
    } else {
        showReadyAuth();
        console.log("login required");
    }
}

function showBusyAuth() {
    $('.croc-login-clickable').hide();
    $('.croc-login-viewable').hide();
    $('.croc-logout-clickable').hide();
    $('.croc-loggedin-viewable').hide();  
}

function login(accessToken) {
    console.log(accessToken);
    $.post(
        '/login',
        'accessToken=' + accessToken,
        processLogin
        );
}

function processLoginPersona(res) {
    console.log("login response received")
    if (res.email != null) {
        $('#croc-username-text').text(res.email);
        $('#croc-user-picture').hide();
        $('#croc-loggedin-qr-img').attr('src', res.qr);
        $('#croc-loggedin-title').text("Welcome, " + res.displayName);
        $('#croc-totp-text').text(res.totpSecret);
        $('#croc-totp-url').text(res.totpUrl);
        showLoggedIn();
        enableLoggedIn();
    }
}

function showLoggedInEmail(email) {
    $('#croc-user-picture').hide();
    $('#croc-username-text').text(email);
    $('#croc-loggedin-title').text("Welcome, " + email);
    showLoggedIn();
    enableLoggedIn();
}

function processLogin(res) {
    console.log("login response received")
    if (res.email != null) {
        $('#croc-username-text').text(res.email);
        $('#croc-user-picture').attr('src', res.picture);    
        $('#croc-loggedin-qr-img').attr('src', res.qr);
        $('#croc-loggedin-title').text("Welcome, " + res.name);
        $('#croc-totp-text').text(res.totpSecret);
        $('#croc-totp-url').text(res.totpUrl);
        showLoggedIn();
        enableLoggedIn();
    }
}

function enableLoggedIn() {
    $('.croc-logout-clickable').click(clickLogoutPersona);
    $('#croc-account-genKey').click(clickGenKey);
    $('#croc-account-signCert').click(clickSignCert);
    $('#croc-account-resetOtp').click(clickResetOtp);
}

function showLoggedIn() {
    $(".croc-info-landing-extra").hide();
    $('.croc-landing-viewable').hide();
    $('.croc-login-viewable').hide();
    $('.croc-login-clickable').hide();
    $('.croc-loggedin-viewable').show();
    $('.croc-logout-clickable').show();
    $('.croc-info').hide();
    $('#croc-loggedin').show();    
}

function clickAuth(event) {
    gapi.auth.authorize({
        client_id: clientId, 
        scope: scopes, 
        immediate: false
    }, processAuthResult);
    return false;
}

function showReadyAuth() {
    $('.croc-login-clickable').show();
    $('.croc-login-viewable').show();    
    $('.croc-login-clickable').click(clickAuth);
}

function getPlus() {
    gapi.client.load('plus', 'v1', function() {
        gapi.client.plus.people.get({
            'userId': 'me'
        }).execute(setMe);
    });
}

function setPlus(me) {
    $('#croc-login').hide();
    $('#croc-username-text').text(me.displayName);
    $('#croc-user-picture').attr('src', me.image.url);    
    $('#croc-username').show();
}
    
function showLanding() {
    showLoggedOut();
}

function showLoggedOut() {
    $(".croc-info-landing-extra").show();
    $(".croc-info").hide();
    $("#croc-info-landing").show();
    $('.croc-landing-viewable').show();
    $('.croc-login-clickable').show();
    $('.croc-login-viewable').show();
    $('.croc-login-clickable').click(clickAuth);
    $('.croc-loggedin-viewable').hide();
    $('.croc-logout-clickable').hide();    
}

function clickLogout(event) {
    $.post(
        '/logout',
        null,
        processLogout
        );
}

function processLogout(res) {
    console.log('logout response received');
    if (res.email != null) {
        $('#croc-username-text').text(null);
        $('#croc-user-picture').attr('src', null);
        showLoggedOut();
    }
}

function clickGenKey() {
    //$('#croc-genkey-modal').modal('show');
    $.post(
        '/genKey',
        null,
        processGenKey
        );
}

function clickSignCert() {
    $.post(
        '/signCert',
        null,
        processSignCert
        );    
}

function clickResetOtp() {
    $('#croc-resetotp-modal').modal('show');
    if (false) {
        $.post(
            '/resetOtp',
            null,
            processResetOtp
            );
    }
}

function okResetOtp() {
  console.log('okResetOtp');
  $('#croc-resetotp-modal').modal('hide');
    
}

function cancelResetOtp() {
  console.log('cancelResetOtp');
  $('#croc-resetotp-modal').modal('hide');
    
}

function okGenKey() {
    console.log('okGenKey');
    $('#croc-genkey-modal').modal('hide');
}

function cancelGenKey() {
  console.log('cancelGenKey');
  $('#croc-genkey-modal').modal('hide');    
}

function processGenKey() {
  console.log('processGenKey');
}

function processResetOtp() {
  console.log('resetOtp');
}


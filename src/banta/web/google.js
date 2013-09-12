
var googleServer = {
    log: function(message) {
        console.log('server log: ' + message);
    },
    ajax: function(req) {
        $.ajax(req);
    },
    googleLoginAuthorize: function() {
        gapi.auth.authorize({
            client_id: clientId,
            scope: scopes,
            immediate: true
        }, googleLoginAuthorizeRes);
    },
    getPlus: function() {
        gapi.client.load('plus', 'v1', function() {
            gapi.client.plus.people.get({
                'userId': 'me'
            }).execute(setMe);
        });
    },
    documentReady: function() {
        googleLoginLoaded();
        gapi.client.setApiKey(apiKey);
        window.setTimeout(this.googleLoginAuthorize, 1);
    }
};

function googleLoginLoaded() {
    $('.googleLogin-clickable').click(googleLoginClick);
    console.log('googleLoginReady');
    var googleAuthCookie = $.cookie("googleAuth");
    if (googleAuthCookie) {
        console.log("cookie googleAuth " + googleAuthCookie);
        $('.googleLogin-clickable').hide();
        server.googleLoginAuthorize();
    } else {
        $('.googleLogin-clickable').show();
        $('.personaLogin-clickable').show();
    }
}

function googleLoginClick() {
    server.googleLoginAuthorize();
}

function googleLoginAuthorizeRes(res) {
    console.log(res);
    if (res && !res.error) {
        googleLoginAccessToken(res.access_token);
    } else {
        showLoggedOut();
        console.log("login required");
    }
}

function googleLoginAccessToken(accessToken) {
    state.googleAccessToken = accessToken;
    console.log(accessToken);
    state.auth = 'google';
    server.ajax({
        type: 'POST',
        url: '/loginGoogle',
        data: 'accessToken=' + accessToken,
        success: loginRes,
        error: loginError
    });
}

function setPlus(me) {
    $('#login').hide();
    $('#username-text').text(me.displayName);
    $('#user-picture').attr('src', me.image.url);
    $('#username').show();
}

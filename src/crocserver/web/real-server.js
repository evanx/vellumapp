
var scopes = [
"https://www.googleapis.com/auth/plus.me", 
"https://www.googleapis.com/auth/userinfo.email", 
"https://www.googleapis.com/auth/userinfo.profile"
];

function initData() {    
}

function initServer() {
    $(document).ajaxError(
        function (event, jqXHR, ajaxSettings, thrownError) {
            console.log('ajax error [event:' + event + '], [jqXHR:' + jqXHR + '], [ajaxSettings:' + ajaxSettings + '], [thrownError:' + thrownError + '])');
            console.log(event);
            console.log(jqXHR);
            console.log(ajaxSettings);
            console.log(thrownError);
        }
        );    
}

function startGoogleClient() {
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

function login(accessToken) {
    console.log(accessToken);
    $.ajax({ 
        type: 'POST',    
        url: '/login',
        data: 'accessToken=' + accessToken,
        success: processLogin,
        error: processLoginError
    });
}

function clickLogin(event) {
    gapi.auth.authorize({
        client_id: clientId, 
        scope: scopes, 
        immediate: false
    }, processAuthResult);
    return false;
}

function getPlus() {
    gapi.client.load('plus', 'v1', function() {
        gapi.client.plus.people.get({
            'userId': 'me'
        }).execute(setMe);
    });
}

function clickLogout(event) {
    $.post(
        '/logout',
        null,
        processLogout
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

function submitGenKey(event) {
    console.log('submitGenKey');    
    if (false) {
        event.preventDefault();
        $.post(
            '/genKey',
            $("#croc-genkey-form").serialize(),
            processGenKeyForm
            );
    }
    if (false) {
        $.ajax({ 
            type: 'POST',    
            url: '/genkey',
            data: {
                password: password
            },
            success: processGenKeyForm,
            error: function(xhr, status, err) {
                console.log("error");
            }
        });
    }    
    return true;
}

function submitEditOrg(event) {
    console.log('submitEditOrg');    
    event.preventDefault();
    $.post(
        '/editOrg',
        $('#croc-editOrg-form').serialize(),
        processEditOrg
        ).error(errorEditOrg);
    return false;
}

function submitEditNetwork(event) {
    console.log('submitEditNetwork');    
    event.preventDefault();
    $.post(
        '/editNetwork',
        $('#croc-editNetwork-form').serialize(),
        processEditNetwork
        ).error(errorEditNetwork);
    return false;
}

function submitEditHost(event) {
    console.log('submitEditHost');    
    event.preventDefault();
    $.post(
        '/editHost',
        $('#croc-editHost-form').serialize(),
        processEditHost
        ).error(errorEditHost);
    return false;
}

function submitEditClient(event) {
    console.log('submitEditClient');    
    event.preventDefault();
    $.post(
        '/editClient',
        $('#croc-editClient-form').serialize(),
        processEditClient
        ).error(errorEditClient);
    return false;
}

function submitEditService(event) {
    console.log('submitEditService');    
    event.preventDefault();
    $.post(
        '/editService',
        $('#croc-editService-form').serialize(),
        processEditService
        ).error(errorEditService);
    return false;
}

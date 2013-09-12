function initTest() {
    console.log('initTest');
    initTestMeta();
}

var serverTest = {
    accessToken: '',
    googleAuthorize: function() {
        var res = {
            access_token: 'dummy_access_token'
        };
        googleAuthorizeRes(res);
    },
    ajax: function(req) {
        console.log('server.ajax: ' + req.url);
        res = mockRes(req);
        console.log(res);
        req.success(res);
    },
    googleClient: function() {        
    },
    getPlus: function() {        
    },
    initServer: function() {        
    },
    initData: function() {
    },
    initTest: function() {
    }
};

var serverReal = {
    accessToken: '',
    ajax: function(req) {
        $.ajax(req);
    },
    googleAuthorize: function() {
        gapi.auth.authorize({
            client_id: clientId, 
            scope: scopes, 
            immediate: true
        }, googleAuthorizeRes);
    },
    googleClient: function() {        
        gapi.client.setApiKey(apiKey);
        window.setTimeout(googleAuthorize, 1);
    },
    getPlus: function() {
        gapi.client.load('plus', 'v1', function() {
            gapi.client.plus.people.get({
                'userId': 'me'
            }).execute(setMe);
        });
    },
    initServer: function() {        
    },
    initData: function() {
        initData();
    },
    initTest: function() {
    }
};

var server = serverReal; 

function initServer() {
    if (window.location.protocol == "file:") {
        server = serverTest; 
    }
    server.initServer();
    server.initData();
    server.googleAuthorize();
    server.initTest();
}

function aboutClick() {
    console.log("about-clickable");
    $(".croc-nav-anchor").removeClass("active");
    $(".croc-info").hide();
    $("#croc-info-about").show();
}

function homeClick() {
    $(".croc-nav-anchor").removeClass("active");
    $(".croc-info").hide();
    $("#croc-info-landing").show();
}        

function reloadClick() {
    window.location.reload();
}        

function contactClick() {
    $(".croc-nav-anchor").removeClass("active");
    $(".croc-info").hide();
    $("#croc-info-contact").show();
}

function documentReadyRedirect() {
    console.log("documentReadyRedirect");
    if (!redirectDocument()) {
        documentReady();
    }
}
function redirectDocument() {
    console.log("redirectDocument " + window.location.protocol);
    if (window.location.protocol == "http:") {
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

function initLib() {
    if (!String.prototype.format) {
        String.prototype.format = function() {
            var args = arguments;
            return this.replace(/{(\d+)}/g, function(match, number) {
                return args[number];
            });
        };
    }
}

function documentReady() {
    initLib();
    console.log("documentReady");
    orgListLoad();
    orgEditLoad();
    $('.editNetwork-clickable').click(editNetworkClick);
    $('.editHost-clickable').click(editHostClick);
    $('.editClient-clickable').click(editClientClick);
    $('.editService-clickable').click(editServiceClick);
    $('.home-clickable').click(homeClick);
    $('.reload-clickable').click(reloadClick);
    $('.about-clickable').click(aboutClick);
    $('.contact-clickable').click(contactClick);
    $('.logout-clickable').click(logoutClick);
    $('.login-clickable').click(server.googleAuthorize());
    $('#editNetwork').load('editNetwork.html', function() {
        $('#editNetwork-form').submit(editNetworkSubmit);
    });
    $('#editHost').load('editHost.html', function() {
        $('#editHost-form').submit(editHostSubmit);
    });
    $('#editClient').load('editClient.html', function() {
        $('#editClient-form').submit(editClientSubmit);
    });
    $('#editService').load('editService.html', function() {
        $('#editService-form').submit(editServiceSubmit);
        initTest();
    });
    $('#croc-genkey-form').submit(genKeySubmit);
    $('.genKey-clickable').click(genKeyClick);
    $('.signCert-clickable').click(signCertClick);
    $('.resetOtp-clickable').click(resetOtpClick);
    $('#croc-secureUrl-anchor').attr('href', secureUrl);
    $('#croc-secureUrl-anchor').text(secureUrl);
    notify("Welcome");
    initServer();
}

function notify(message) {
    console.log("notify " + message);
}

function googleAuthorizeRes(res) {
    console.log(res);
    if (res && !res.error) {
        showBusyAuth();
        googleAuthorizeAccessToken(res.access_token);
    } else {
        showReadyAuth();
        console.log("login required");
    }
}

function loginError() {
    console.log("login error");
    showReadyAuth();
}

function loginRes(res) {
    console.log("login response received")
    if (res.email != null) {
        notify('Welcome, ' + res.name);
        $('#croc-username-text').text(res.email);
        $('#croc-user-picture').attr('src', res.picture);    
        $('#croc-loggedin-qr-img').attr('src', res.qr);
        $('#croc-loggedin-title').text("Welcome, " + res.name);
        $('#croc-totp-text').text(res.totpSecret);
        $('#croc-totp-url').text(res.totpUrl);
        $('#croc-totp-url').attr('href', res.qr);
        showLoggedIn();
    } else {        
        console.log(res);
    }
}

function showLoggedIn() {
    $('.croc-landing-viewable').hide();
    $('.croc-login-viewable').hide();
    $('.login-clickable').hide();
    $('.croc-info').hide();
    $('.croc-loggedin-viewable').show();
    $('.logout-clickable').show();
    $('#croc-loggedin-username').show();    
    $('#croc-loggedin-info').show();    
}

function showReadyAuth() {
    $('.login-clickable').show();
    $('.croc-login-viewable').show();    
    $('.login-clickable').click(server.loginClick);
}

function showBusyAuth() {
    $('.login-clickable').hide();
    $('.croc-login-viewable').hide();
    $('.logout-clickable').hide();
    $('.croc-loggedin-viewable').hide();  
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
    $(".croc-info").hide();
    $('.croc-loggedin-viewable').hide();
    $('.logout-clickable').hide();    
    $("#croc-info-landing").show();
    $('.croc-landing-viewable').show();
    $('.login-clickable').show();
    $('.croc-login-viewable').show();
}

function logoutRes(res) {
    console.log('logout response received');
    if (res.email != null) {
        $('#croc-username-text').text(null);
        $('#croc-user-picture').attr('src', null);
        showLoggedOut();
    }
}

function okResetOtp() {
    console.log('okResetOtp');
    $('#resetotp-modal').modal('hide');
    
}

function cancelResetOtp() {
    console.log('cancelResetOtp');
    $('#resetotp-modal').modal('hide');
    
}

function okGenKey() {
    console.log('okGenKey');
    $('#genkey-modal').modal('hide');
}

function cancelGenKey() {
    console.log('cancelGenKey');
    $('#genkey-modal').modal('hide');    
}

function processGenKey() {
    console.log('processGenKey');
}

function processResetOtp() {
    console.log('resetOtp');
}

function processGenKeyForm(res) {
    console.log('processGenKeyForm');
    console.log(res);
}

function buildInput(formName, field) {
    console.log(field);
    var fieldName = 'url';
    var fieldId = formName + '-' + fieldName;
    return '<input type="text" name="' + fieldName + '" placeholder="' + field.label + '" id="' + fieldId + '"/>';
}

function buildInputs(fieldset, formName, fields) {
    console.log(fields);
    fieldset.empty();
    for (var i = 0; i < fields.length; i++) {
        fieldset.append(buildInput(formName, fields[i]));
    }    
}

function editNetworkClick() {
    console.log('editNetwork-clickable');
    $('.croc-info').hide();
    $('#editNetwork').show();
}

function editHostClick() {
    console.log('editHost-clickable');
    $('.croc-info').hide();
    $('#editHost').show();
}

function editClientClick() {
    console.log('editClient-clickable');
    $('.croc-info').hide();
    $('#editClient').show();
}

function editServiceClick() {
    console.log('editService-clickable');
    $('.croc-info').hide();
    $('#editService').show();
}

function buildTr(handler, index) {
    var object = handler.list[index];
    var array = handler.columnArray(object);
    var html = "<tr onclick='" + handler.name + "ListRowClick(" + handler.id(object) + ")'>";
    for (var i = 0; i < array.length; i++) {
        html += '<td>' + array[i] + '</td>';
    }
    html += '</tr>';
    console.log(html);
    return html;
}

function buildTable(tbody, handler) {
    tbody.empty();
    for (var i = 0; i < handler.list.length; i++) {
        tbody.append(buildTr(handler, i));
    }    
}

function googleAuthorizeAccessToken(accessToken) {
    server.accessToken = accessToken;
    console.log(accessToken);
    server.ajax({ 
        type: 'POST',    
        url: '/login',
        data: 'accessToken=' + accessToken,
        success: loginRes,
        error: loginError
    });
}

function logoutClick(event) {
    server.ajax({
        type: 'POST',    
        url: '/logout',
        data: null,
        success: logoutRes,
        error: logoutResError
    });
}

function resetOtpClick() {
    $('#croc-resetotp-modal').modal('show');
    if (false) {
        $.post(
            '/resetOtp',
            null,
            processResetOtp
            );
    }
}

function genKeyClick() {
    //$('#croc-genkey-modal').modal('show');
    $.post(
        '/genKey',
        null,
        processGenKey
        );
}

function signCertClick() {
    $.post(
        '/signCert',
        null,
        processSignCert
        );    
}

function genKeySubmit(event) {
    console.log('genKeySubmit');    
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

function editNetworkSubmit(event) {
    console.log('editNetworkSubmit');    
    event.preventDefault();
    $.post(
        '/editNetwork',
        $('#croc-editNetwork-form').serialize(),
        processEditNetwork
        ).error(errorEditNetwork);
    return false;
}

function editHostSubmit(event) {
    console.log('editHostSubmit');    
    event.preventDefault();
    $.post(
        '/editHost',
        $('#croc-editHost-form').serialize(),
        processEditHost
        ).error(errorEditHost);
    return false;
}

function editClientSubmit(event) {
    console.log('editClientSubmit');    
    event.preventDefault();
    $.post(
        '/editClient',
        $('#croc-editClient-form').serialize(),
        processEditClient
        ).error(errorEditClient);
    return false;
}

function editServiceSubmit(event) {
    console.log('editServiceSubmit');    
    event.preventDefault();
    $.post(
        '/editService',
        $('#croc-editService-form').serialize(),
        processEditService
        ).error(errorEditService);
    return false;
}

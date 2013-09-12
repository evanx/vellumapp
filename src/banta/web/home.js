
var b = {};
var dom = {};
var server = mockServer;
var state = { 
    env: 'test',
};
var info = {
    components: [
        'contacts',
        'contact',
        'chat',
        'chats',
        'event',
        'events',
        'notifications'
    ]
};


function locationDev() {
    return window.location.hostname.startsWith('localhost') || window.location.hostname.startsWith("192.168");
}

function locationLive() {
    return window.location.hostname.indexOf('appcentral.info') > 0;
}

$(document).ready(function() {
    u.init();
    documentLoad();
});

function documentLoad() {
    console.log('documentLoad', window.location.hostname);
    state.loadedComponentsCount = info.components.length;
    foreach(info.components, function(component) {
        load(component);
    });
}

function componentsLoaded() {
    b.chat.loaded();
    b.chats.loaded();
    b.contact.loaded();
    b.contacts.loaded();
    b.events.loaded();
    b.event.loaded();    
    b.notifications.loaded();
    homeLoaded();
}

function load(name) {
    var hostname = '';
    if (window.location.hostname === 'evanx.neocities.org') {
        hostname = 'https://banta.appcentral.info/';
    }
    $('#' + name + '-container').load(hostname + name + '.html', function(responseText) {
        if (name === 'events') {
        }
        documentLoaded(name);
    });
}

function documentLoaded(component) {
    state.loadedComponentsCount--;
    console.log('documentLoaded', state.loadedComponentsCount, component);
    if (state.loadedComponentsCount === 0) {
        documentReady();
    }
}

function homeLoaded() {
    $('.home-clickable').click(homeClick);
    $('.reload-clickable').click(reloadClick);
    $('.about-clickable').click(aboutClick);
    $('.contactus-clickable').click(contactUsClick);
    $('.logout-clickable').click(logoutClick);
    $('#login-submit').click(loginSubmit);    
}

function documentReady() {
    console.log('documentReady');
    componentsLoaded();
    window.addEventListener("popstate", function(event) {
        windowState(event);
    });
    server.documentReady();
}

function windowState(event) {
    console.log("windowState", window.location.pathname);
    event.preventDefault();
    windowLocation(window.location.pathname);
}

function windowLocation(pathname) {
    console.log("windowLocation", pathname, window.location.pathname);
    if (pathname === '/#home') {
        homeClick();
    } else if (pathname === '/#contactUs') {
        contactUsClick();
    } else if (pathname === '/#eventInvite') {
        b.events.click();        
        b.event.newClicked();
        b.event.inviteClicked();
    } else if (pathname === '/#events') {
        b.events.click();
    } else if (pathname.startsWith('/#event')) {
        b.events.click();
    } else if (pathname === '/#aboutUs') {
        aboutClick();
    } else if (pathname === '/#contacts') {
        b.contacts.click();
    } else if (pathname.startsWith('/#contact/')) {
        b.contacts.click();
    } else if (pathname === '/#contactNew') {
        b.contact.newClicked();
    } else if (pathname === '/#chats') {
        b.chats.click();
    } else if (pathname.startsWith('/#chat/')) {
        b.chats.click();
    } else {
        homeClick();
    }
}

function removeCookies() {
    $.cookie.remove('googleAuth');
}

function showLanding() {
    showLoggedOut();
}

function loginSubmit() {
    var number = $('#login-id-input').val();
    console.log('loginSubmit', number);
    if (true && isEmpty(number)) {
        loginNumber();
    } else if (!u.validate.phoneNumber(number)) {
        $('#login-alert-div').text('Invalid phone number');
        $('#login-alert-div').show();
        $('#login-id-input').val('');
        $('#login-id-input').focus();
    } else {
        loginNumber(number);
    }
}

function loginNumber(number) {
    $('#login-alert-div').hide();
    state.auth = 'number';
    server.ajax({
        type: 'POST',
        url: '/loginNumber',
        data: number,
        success: loginRes,
        error: loginError
    });
}

function showLoggedOut() {
    $('#notifications-container').hide();
    $('.loggedin-viewable').hide();
    $('.logout-clickable').hide();
    $('.loggedout-viewable').show();
    showLogin();
}

function showLogin() {
    state.auth = null;
    $('#login-alert-div').hide();
    $('#login-id-input').val('');
    showPage('Banta', 'landing', 'landing', null);
    $('#login-id-input').focus();
}

function showLoggedInRes() {
    if (state.auth === null) {
        console.warn('no server auth');
        state.auth = 'unknown';
    }
    u.ui.notify('Welcome, ' + state.login.email);
    $('#loggedin-message').text("Welcome, " + state.login.name);
    if (false) {
        $('#loggedin-username-clickable').text(state.login.email);
        $('#loggedin-username-clickable').show();
    }
    showLoggedIn();
}

function showLoggedIn() {
    $('.login-clickable').hide();
    $('.page-container').hide();
    $('.loggedout-viewable').hide();
    $('.loggedin-viewable').show();
    $('.logout-clickable').show();
    $('#loggedin-username').show();
    $('#loggedin-info').show();
    $('#welcome-container').show();
    $('#notifications-container').show();
}

function loginRes(res) {
    console.log("loginRes", window.location.pathname);
    if (res.email !== null) {
        db.loginRes(res);
        showLoggedInRes();
        if (state.auth === 'google') {
            $.cookie("googleAuth", res.email);
        }
        var path = $.cookie('path');
        console.log('loginRes path', path);
        b.event.loggedIn();
        if (!isEmpty(path)) {
            windowLocation(path);
        } else {
            windowLocation(window.location.pathname);
        }
    }
}

function loginError() {
    console.log("googleLoginError");
    showLoggedOut();
}

function logoutClick(event) {
    if (state.auth === 'persona') {
        state.auth = null;
        personaLogoutClick();
    } else {
        state.auth = null;
        logoutReq();
    }
}

function logoutReq() {
    showLoggedOut();
    server.ajax({
        type: 'POST',
        url: '/logout',
        data: null,
        success: logoutRes,
        error: logoutError
    });
}

function logoutRes(res) {
    console.log("logoutRes");
    console.log(res);
}

function logoutError() {
    console.log("logoutError");
}

function showPageObj(object, id) {
    showPage(object.title, object.page, object.path, id);
}

function showPage(title, page, path, id) {
    $('#title').text(title);
    $('.page-container').hide();
    $('#' + page + '-container').show();
    setPath(path, id);
}

function setPath(path, id) {
    path = '/#' + path;
    if (id) {
        path += '/' + id.replace(/\s+/g, '_');
    }
    window.history.pushState(null, null, path);
    $.cookie('path', path);
    console.log('setPath', $.cookie('path'));
}

function aboutClick() {
    setPath('aboutUs');
    $('#title').text('About');
    $('.nav-item').removeClass("active");
    $('.page-container').hide();
    $("#about-container").show();
}

function homeClick() {
    state.chat = null;
    state.contact = null;
    state.purpose = null;
    setPath('home');
    $('.btn').removeClass('btn-primary');
    if (isEmpty(state.contacts)) {
    } else if (isEmpty(state.chats)) {
    } else if (state.chats) {
    } else {
    }
    $('#title').text('Banta');
    $('.nav-item').removeClass("active");
    $('.page-container').hide();
    if (state.auth === null) {
        $("#landing-container").show();
        $('#login-id-input').focus();
    } else {
        $("#home-container").show();
        $('#notifications-container').show();
    }
}

function contactUsClick() {
    setPath('cotactUs');
    $('#title').text('Contact us');
    $('.nav-item').removeClass("active");
    $('.page-container').hide();
    $("#contact-container").show();
}

function reloadClick() {
    console.log('reload');
    console.log(window.location.origin);
    if (true) {
        removeCookies();
    }
    window.location.reload();
}

function compareName(a, b) {
    if (a.name === b.name) {
        return 0;
    } else if (a.name.toLowerCase() > b.name.toLowerCase()) {
        return 1;
    }
    return -1;
}

function matchName(object, name) {
    return object.name === name;
}

function matchId(object, id) {
    return object.id === id;
}

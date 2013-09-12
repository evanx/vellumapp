var accessToken;
var tokenType;
var expiresIn;
var user;
var count; 
var detectedRedirectUrlString;
        
function click_login_google() {
    accessToken = null;
    var loginUrl = '${loginUrl}';
    var win = window.open(loginUrl, "Login with Google", 'width=800, height=600');
    count = 0; 
    detectedRedirectUrlString = null;
    var pollTimer =  window.setInterval(function() { 
        count++;
        if (count < 10) {
        } else if (count%20 == 0) {
            console.log("click_login_google timer");
        } else if (count > 200) {    
            window.clearInterval(pollTimer);
            console.log("cancel");
        } else if (win.document && win.document.URL) {
            var detectedRedirectUrlString = win.document.URL;
            if (detectedRedirectUrlString.indexOf('${redirectUrl}') != -1) {
                console.log("detected " + detectedRedirectUrlString);
                window.clearInterval(pollTimer);
                var index = detectedRedirectUrlString.indexOf('#');
                if (index > 0) {
                    detectedRedirectUrlString = detectedRedirectUrlString.substring(index + 1);
                    console.log("detectedRedirectUrlString " + detectedRedirectUrlString);
                    var regex = /([^&=]+)=([^&]*)/g;
                    while (true) {
                        var m = regex.exec(detectedRedirectUrlString)
                        if (m == null) break; 
                        if (m[1] == 'access_token') accessToken = m[2];
                    }
                    if (accessToken != null) {
                        sendToken(accessToken);    
                    }
                }                    
            }
            win.close();    
        }
    }, 200);
}

function setUser(user) {
    console.log(user);
    if (user.email != null) {
        $('#croc-login-btn').hide();
        $('#croc-username').show();
        $('#croc-username-text').text(user.email);
        $('#croc-user-picture').attr('src', user.picture);
    }
}

function sendToken(token) {
    $.ajax({
        url: '${serverUrl}/get/googleUserInfo?access_token=' + token,
        data: null,
        success: setUser,
        dataType: "json"
    });
}

function validateToken(token) {
    $.ajax({
        url: 'https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=' + token,
        data: null,
        success: setUser,
        dataType: "jsonp"  
    });
}

function getUserInfo() {
    $.ajax({
        url: 'https://www.googleapis.com/oauth2/v1/userinfo?access_token=' + accessToken,
        data: null,
        success: setUser,
        dataType: "jsonp"
    });
}

function getPlusUserInfo() {
    $.ajax({
        url: 'https://https://www.googleapis.com/plus/v1/people/?access_token=' + accessToken,
        data: null,
        success: setUser,
        dataType: "jsonp"
    });
}

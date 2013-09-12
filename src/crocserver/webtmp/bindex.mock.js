
function initDate() {
    $('#editOrg-url').val('myorg.com');
}

var evanLogin = {
    name: 'Evan Summers',
    email: 'evan.summers@gmail.com',
    picture: '',
    totpSecret: '',
    totpUrl: '',
    qr: ''
}

var evanLogout = {
    email: 'evan.summers@gmail.com'
}

var bizOrg = {
    orgName: 'biz',
    displayName: 'Biz (Pty) Ltd'
}

var hetznerNetwork = {
    networkName: 'hetzner',
    displayName: 'Hetzner CT1 DC',
    address: '192.168.1.0/24'
}

var biz1Host = {
    hostName: 'biz1',
    ip: '192.168.1.1'
}

var biz1RootClient = {
    hostName: 'biz1',
    clientName: 'root',
    certSubject: 'CN=root@biz1, O=biz, OU=biz1, S=local, L=Cape Town, C=za'
}

var biz1RootNightlyService = {
    hostName: 'biz1',
    clientName: 'root',
    serviceName: 'nightly',
    status: 'OK',
    reportTime: '2013-01-01 01:01:01'
}

function mockRes(req) {
    if (req.url == '/login') {
        return evanLogin;
    } else if (req.url == '/logout') {
        return evanLogout;
    }
    return { 
        error: 'mockRes ' + req.url
    }
}


var orgListHandler = {
    name: 'org',
    id: function(org) {
        return org.orgId;
    },
    columnArray: function(org) {
        return [org.orgId, org.orgUrl, org.orgCode, org.displayName];
    },
};

function orgListLoad() {
    $('#orgList-container').load('orgList.html', function() {
        orgListReady();
    });
}

function orgListReady() {
    $('.orgList-clickable').click(orgListClick);
}

function orgListClick() {
    server.ajax({
        type: 'POST',
        url: '/orgList',
        data: 'accessToken=' + server.accessToken,
        success: orgListRes,
        error: orgListError
    });
}

function orgListRes(res) {
    console.log('orgListRes');
    orgListHandler.list = res.list;
    buildTable($('#orgList-tbody'), orgListHandler);
    $('.croc-info').hide();
    $('#orgList-container').show();
}

function orgListError() {
    console.log('orgListError');
}

function orgListRowClick(id) {
    console.log(['orgListRowClick', id]);
    for (i = 0; i < orgListHandler.list.length; i++) {
        console.log(orgListHandler.list[i]);
        console.log(orgListHandler.id(orgListHandler.list[i]));
        if (orgListHandler.id(orgListHandler.list[i]) === id) {
            orgEditSet(orgListHandler.list[i]);
        }
    }
    orgEditClick();
}

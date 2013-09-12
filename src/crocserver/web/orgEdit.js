
function orgEditLoad() {
    $('#orgEdit-container').load('orgEdit.html', function() {
        orgEditReady();
    });
}

function orgEditReady() {
    $('.orgEdit-clickable').click(orgEditClick);    
    $('#orgEdit-form').submit(orgEditSubmit);    
}

function orgEditClick() {
    console.log('orgEditClick');
    console.log(orgMeta.orgEdit);
    $('.croc-info').hide();
    //buildInputs($('#orgEdit-fieldset'), '', orgMeta.orgEdit);
    $('#orgEdit-container').show();
}

function orgEditSet(org) {
    $('#orgEdit-orgId').val(org.orgId);
    $('#orgEdit-orgUrl').val(org.orgUrl);
    $('#orgEdit-orgCode').val(org.orgCode);
    $('#orgEdit-displayName').val(org.displayName);
    $('#orgEdit-region').val(org.region);
    $('#orgEdit-locality').val(org.locality);
    $('#orgEdit-country').val(org.country);
}

function orgEditSubmit(event) {
    console.log('orgEditSubmit');    
    event.preventDefault();
    server.ajax({
        url: '/orgEdit',
        data: $('#orgEdit-form').serialize(),
        success: orgEditRes,
        error: orgEditError
    });
    return false;
}

function orgEditRes(res) {
    console.log('processEditOrg');    
    console.log(res);
}

function orgEditError() {
    console.log('errorEditOrg');    
}


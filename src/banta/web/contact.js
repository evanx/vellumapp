
b.contact = {
    loaded: function() {
        b.contact.validator = $('#contact-form').validate(b.contact.validatorConfig);
        $('#contact-save').click(b.contact.save);
        $('#contact-cancel').click(b.contact.cancel);
        $('#contact-cancel').focus(b.contact.cancelFocus);
    },
    validatorConfig: {
        rules: {
            name: {
                minlength: 2,
                required: true,
                sanitary: true
            },
            mobile: {
                minlength: 10,
                maxlength: 10,
                digits: true,
                required: false
            },
            email: {
                required: false,
                email: true
            }
        },
        highlight: function(element) {
            $(element).closest('.control-group').removeClass('success').addClass('error');
            b.contact.buttons(false);
            $(element).focus();
            b.contact.errorElement = element;
        },
        success: function(element) {
            $(element).closest('.control-group').removeClass('error').addClass('success');
            b.contact.buttons(true);
        },
    },
    buttons: function(ok) {
        $('.contact-button').removeClass('btn-primary');
        if (ok) {
            $('#contact-save').addClass('btn-primary');
        } else {
            $('#contact-cancel').addClass('btn-primary');
        }
    },
    cancelFocus: function(event) {
        if (b.contact.validator.valid()) {
            $('#contact-save').focus();
        }
    },
    edit: function(contact) {
        state.contact = contact;
        console.log("edit", contact);
        b.contact.clear();
        b.contact.set(contact);
        $('#contact-legend').text('Edit contact');
        showPage('Edit contact', 'contact', 'contact', contact.name);
    },
    newClick: function() {
        state.contact = null;
        b.contact.clear();
        $('#contact-legend').text('New contact');
        showPage('New contact', 'contact', 'contactNew', null);
        b.contact.focus();
    },
    save: function(event) {
        event.preventDefault();
        b.contact.errorElement = null;
        if ($('#contact-form').valid()) {
            var contact = b.contact.get();
            console.log("contact.save", contact);
            db.contacts.put(contact);
            server.ajax({
                url: '/contact',
                data: $('#contact-form').serialize(),
                success: b.contact.res,
                error: b.contact.error,
                memo: contact
            });
        } else {
            b.contact.buttons(false);
        }
    },
    res: function(res) {
        console.log('contact.res');
        console.log(res);
        b.contacts.click();
    },
    error: function() {
        console.log('contact.error');
    },
    cancel: function() {
        console.log("contact.cancel");
        b.contact.clear();
        b.contacts.click();
    },
    clear: function() {
        console.log("contact.clear");
        b.contact.validator.resetForm();
        $('#contact-cancel').addClass('btn-primary');
        $('#contact-save').removeClass('btn-primary');
        b.contact.buttons(false);
        $('#contact-form > fieldset > div.control-group').removeClass('error');
        $('#contact-form > fieldset > div.control-group').removeClass('success');
        b.contact.set({
            name: '',
            mobile: '',
            email: ''
        });
    },
    set: function(o) {
        $('#contact-name-input').val(o.name);
        $('#contact-mobile-input').val(o.mobile);
        $('#contact-email-input').val(o.email);
    },
    get: function() {
        return {
            name: u.string.sanitize($('#contact-name-input').val()),
            mobile: $('#contact-mobile-input').val(),
            email: $('#contact-email-input').val()
        };
    },
    focus: function() {
        $('#contact-name-input').focus();
    },
};



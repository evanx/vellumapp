
b.event = {
    rules: {
        name: {
            minlength: 1,            
            required: true,
            sanitary: true,
        },
        host: {
            required: true,            
            contact: true
        },
        description: {
            required: false,
            sanitary: true
        },
        time: {
            minlength: 3,
            required: true,
            sanitary: true
        },
        day: {
            required: false
        },
        date: {
            date: true,
            required: true
        },
        duration: {
            required: false,
            sanitary: true
        },
        repeat: {
            required: false
        },
        reminder: {
            required: false
        },
    },
    messages: {
        date: 'Enter day or date'
    },
    highlight: function(element) {
        console.log("highlight", element);
        $(element).closest('.control-group').removeClass('success').addClass('error');
        b.event.buttons(false);
        $(element).focus();
        b.event.errorElement = element;
    },
    success: function(element) {
        console.log("success");
        $(element).closest('.control-group').removeClass('error').addClass('success');
        b.event.buttons(true);
    },
    valid: function() {
        if (!$('#event-form').valid()) {
            return false;
        }
        var event = b.event.get();
        if (!isEmpty(event.date) && isEmpty(event.day) && event.repeat === 'Weekly') {
            $('#event-day-input').val(u.date.formatWeekDay(event.date));
            console.log('valid day', event.day);
        }
        return true;
    },
    save: function(e) {
        console.log("event.save invitees", state.invitees);
        e.preventDefault();
        b.event.errorElement = null;
        if (b.event.valid()) {
            var event = b.event.get();
            event.invitees = [];
            foreach(state.invitees, function(invitee) {
                event.invitees.push(invitee.name);
            })
            console.log("event.save", event);
            server.ajax({
                url: '/eventSave',
                data: $('#event-form').serialize(),
                success: b.event.res,
                error: b.event.error,
                memo: event
            });
        } else {
            b.event.buttons(false);
        }
    },
    res: function(res) {
        console.log('res', res);
        db.events.put(res);
        b.events.click();
    },
    error: function() {
        console.log('error');
    },
    buttons: function(ok) {
        if (ok) {
            $('#event-save').addClass('btn-primary');
            $('#event-cancel').removeClass('btn-primary');
        } else {
            $('#event-save').removeClass('btn-primary');
            $('#event-cancel').addClass('btn-primary');
        }
    },
    loaded: function() {
        b.event.validator = $('#event-form').validate({
            rules: b.event.rules,
            highlight: b.event.highlight,
            success: b.event.success
        });
        $('#event-save').click(b.event.save);
        $('#event-add-invitee').click(b.event.inviteClicked);
        $('#event-cancel').click(b.event.cancel);
        $('#event-cancel').focus(b.event.cancelFocus);
        $('#event-date-input').datepicker();
        $('#event-date-input').datepicker("option", "dateFormat", "DD, d MM, yy");
        $('#event-duration-input').autocomplete({
            source: ['30m', '45m', '1h', '2h', '3h', '4h', '1d']
        });
        $('#event-reminder-input').autocomplete({
            source: ['15m', '45m', '1h', '2h', '1d', '2d']
        });
        b.event.$tbody = $('#event-invitees-tbody');
        b.event.$tbody.hide();
        b.event.$tbody.empty();
    },
    loggedIn: function() {
        console.log('event.loggedIn', state.contacts.length);
        state.contactNames = u.array.extractValues(state.contacts, 'name');
        $('#event-host-input').autocomplete({
            source: state.contactNames
        });
    },
    newClicked: function() {
        console.log('event.newClicked');
        state.event = null;
        b.event.clear();
        if (state.contact) {
            $('#event-host-input').val(state.contact.name);
        }
        $('#event-legend').text('New event');
        b.event.showPage();
        b.event.focus();
    },
    inviteClicked: function() {
        console.log('event.inviteClicked');
        b.contacts.chooseMulti('eventInvite', b.event.inviteesChosen, state.invitees, b.event.findHost());
    },
    findHost: function() {
        var hostName = $.trim($('#event-host-input').val());
        if (!isEmpty(hostName)) {
            var contact = db.contacts.get(hostName);
            u.array.remove(state.invitees, contact);
            return contact;
        }
        return null;
    },
    inviteesChosen: function(contacts) {
        console.log('event.inviteeChoosen', contacts.length);
        state.invitees = contacts;
        b.event.buildInvitees();
        b.event.showPage();                
    },
    inviteeRemoveChosen: function(contact) {
        console.log('event.inviteeRemoveChosen', state.invitees);
        u.array.remove(state.invitees, contact);
        console.log('event.inviteeRemoveChosen', contact, state.invitees);
        b.event.buildInvitees();
    },
    cancelFocus: function() {
        if (b.event.validator.valid()) {
            $('#event-save').focus();
        }
    },
    edit: function(event) {
        b.event.clear();
        state.event = event;
        state.invitees = db.events.getInvitees(event);
        b.event.buildInvitees();
        console.log("event.edit", event, state.invitees);
        b.event.set(event);
        $('#event-legend').text('Edit event');
        showPage('Edit event', 'event', 'event', event.name);
    },
    showPage: function() {
        showPage('New event', 'event', 'event', null);
    },
    buildInvitees: function() {
        if (state.invitees.length > 0) {
            state.invitees.sort(u.object.makeCompare('name'));
            b.event.$tbody.empty();            
            for (var i = 0; i < state.invitees.length; i++) {
                b.event.$tbody.append('<tr><td>' + state.invitees[i].name + '</td></tr>');
                b.event.$tbody.children('tr:last').click(state.invitees[i], u.event.makeGetData(b.event.inviteeRemoveChosen));
            }
            b.event.$tbody.show();
            $('#event-invitees-div').show();
        } else {
            $('#event-invitees-div').hide();
        }
    },
    cancel: function() {
        console.log("cancel");
        b.event.clear();
        b.events.click();
    },
    clear: function() {
        console.log("clear");
        state.invitees = [];
        b.event.validator.resetForm();
        $('#event-cancel').addClass('btn-primary');
        $('#event-save').removeClass('btn-primary');
        b.event.buttons(false);
        $('#event-form > fieldset > div.control-group').removeClass('error');
        $('#event-form > fieldset > div.control-group').removeClass('success');
        b.event.set({
            name: '',
            description: '',
            host: '',
            time: '',
            day: '',
            repeat: 'Once',
            date: '',
            duration: '1h',
            reminder: '1h',
        });
    },
    set: function(o) {
        $('#event-id-input').val(o.id);
        $('#event-name-input').val(o.name);
        $('#event-description-input').val(o.description);
        $('#event-reminder-input').val(o.reminder);
        $('#event-repeat-input').val(o.repeat);
        $('#event-host-input').val(o.host);
        $('#event-time-input').val(o.time);
        $('#event-date-input').val(o.date);
        $('#event-day-input').val(o.day);
        $('#event-duration-input').val(o.duration);
    },
    get: function() {
        console.log('get', state.event);
        return {
            id: $('#event-id-input').val(),
            name: $.trim($('#event-name-input').val()),
            description: $('#event-description-input').val(),
            host: $.trim($('#event-host-input').val()),
            repeat: $('#event-repeat-input').val(),
            reminder: $('#event-reminder-input').val(),
            time: u.string.sanitize($('#event-time-input').val()),
            date: $('#event-date-input').val(),
            day: $('#event-day-input').val(),
            duration: $('#event-duration-input').val()
        };
    },
    focus: function() {
        $('#event-time-input').focus();
    },
};

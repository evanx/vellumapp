
b.contacts = {
    page: 'contacts',
    path: 'contacts',
    defaultTitle: 'Contacts',
    purposeTitle: {
        chat: 'Contact to chat',
        eventHost: 'Event host',
        eventInvitee: 'Invitee',
    },
    dom: {
        input: '#contacts-search-input',
        form: '#contacts-search-form',
        tbody: '#contacts-tbody',
    },
    loaded: function() {
        foreachKey(b.contacts.dom, function(key, value) {
            b.contacts['$' + key] = $(value);
            if (value.endsWith('-clickable')) {
                b.contacts['$' + key].click(b.contacts[key + 'Click']);
            }
        });
        $('.contacts-clickable').click(b.contacts.click);
        $('.contacts-new-clickable').click(b.contacts.newClick);
        $('.contacts-edit-clickable').click(b.contacts.editClick);
        $('.contacts-reduce-clickable').click(b.contacts.reduceClick);
        $('.contacts-expand-clickable').click(b.contacts.expandClick);
        $('.contacts-search-clickable').click(b.contacts.searchClick);
        $('.contacts-clear-clickable').click(b.contacts.clearClick);
        $('.contacts-ok-clickable').click(b.contacts.okClick);
        b.contacts.$form.submit(b.contacts.searchClick);
    },
    newClick: function() {
        b.contact.newClick();
    },
    editClick: function() {
        if (state.selectedContacts.length === 1) {
            b.contact.edit(state.selectedContacts[0]);
        }
    },
    click: function() {
        console.log("contacts.click", state.purpose);
        b.contacts.reset();
        b.contacts.showPage();
    },
    reset: function() {
        state.availableContacts = state.contacts.slice(0);
        state.selectedContacts = [];
        state.chosenContacts = null;
        state.chosenContact = null;
    },
    showPage: function() {
        console.log("contacts.click", state.purpose, state.availableContacts);
        state.contact = null;
        b.contacts.build(state.availableContacts, null);
        b.contacts.title = b.contacts.defaultTitle;
        if (!isEmpty(state.purpose)) {
            b.contacts.title = b.contacts.purposeTitle[state.purpose];
        }
        showPageObj(b.contacts, null);
        //b.contacts.$input.focus();
    },
    searchFilter: function(contact) {
        return !u.array.contains(state.selectedContacts, contact) &&
                contact.name.startsWithIgnoreCase(b.contacts.$input.val());
    },
    build: function(contacts, filter) {
        console.log('contacts.build', contacts.length, state.selectedContacts.length);
        contacts.sort(compareName);
        b.contacts.$tbody.empty();
        for (var i = 0; i < contacts.length; i++) {
            if (!filter || filter(contacts[i])) {
                b.contacts.buildRow(contacts[i]);
            }
        }
    },
    buildRow: function(contact) {
        b.contacts.$tbody.append('<tr><td>' + contact.name + '</td></tr>');
        var tr = b.contacts.$tbody.children('tr:last');
        tr.click(contact, b.contacts.contactRowClick);
        if (u.array.contains(state.selectedContacts, contact)) {
            tr.children().addClass('selected');
        } else {
            tr.children().removeClass('selected');
        }
    },
    okClick: function(event) {
        console.log('okClick');
        event.preventDefault();
        if (state.chosenContacts) {
            state.chosenContacts(state.selectedContacts);
        }
    },
    reduceClick: function(event) {
        console.log('reduceClick');
        b.contacts.render(event, state.selectedContacts);
    },
    expandClick: function(event) {
        console.log('expandClick');
        b.contacts.$input.val('');
        b.contacts.render(event);
    },
    clearClick: function(event) {
        console.log('clearClick');
        b.contacts.$input.val('');
        b.contacts.render(event);
    },
    searchClick: function(event) {
        console.log('searchClick', event);
        b.contacts.render(event, null, b.contacts.searchFilter);
    },
    render: function(event, contacts, filter) {
        event.preventDefault();
        if (!contacts) {
            contacts = state.availableContacts;
        }
        console.log('render', contacts.length, b.contacts.$input.val());
        b.contacts.build(contacts, filter);
        b.contacts.$input.val('');
        b.contacts.$input.focus();
    },
    choose: function(purpose, chosenCallback, availableContacts) {
        state.purpose = purpose;
        state.chosenContact = chosenCallback;
        state.chosenContacts = null;
        state.availableContacts = availableContacts;
        state.selectedContacts = [];
        if (!u.object.containsKey(b.contacts.purposeTitle, purpose)) {
            console.warn('choose', state.purpose);
        }
        b.contacts.showPage();
    },
    chooseMulti: function(purpose, chosenCallback, selectedContacts, excludeContact) {
        b.contacts.reset();
        state.purpose = purpose;
        state.chosenContacts = chosenCallback;
        state.selectedContacts = selectedContacts;
        state.availableContacts = u.array.newRemove(state.contacts, excludeContact);
        if (!u.object.containsKey(b.contacts.purposeTitle, purpose)) {
            console.warn('chooseMulti', state.purpose);
        }
        b.contacts.showPage();
        setPath(purpose);
    },
    contactRowClick: function(event) {
        state.contact = event.data;
        if (isEmpty(state.purpose)) {
            b.contact.edit(state.contact);
        } else if (!isEmpty(state.chosenContact)) {
            state.chosenContact(state.contact);
        } else {
            var td = $(this).children('td').first();
            if (td.hasClass('selected')) {
                td.removeClass('selected');
                u.array.remove(state.selectedContacts, state.contact);
            } else {
                td.addClass('selected');
                state.selectedContacts.push(state.contact);
            }
        }
        b.contacts.buttons();
        console.log('contacts.contactRowClick', state.purpose, state.contact, state.selectedContacts.length);
    },
    buttons: function() {
        var selected = (state.selectedContacts.length > 0);
        console.log('contacts.buttons', selected);
        u.ui.enableLink($('#contacts-reduce-link'), selected);
        u.ui.enableLink($('#contacts-expand-link'), selected);
        u.ui.enableLink($('#contacts-ok-link'), selected);
        u.ui.enableLink($('#contacts-cancel-link'), selected);
    },
};


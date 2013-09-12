

b.chat = {
    loaded: function() {
        dom.chat = {};
        dom.chat.tbody = $('#chat-tbody');
        dom.chat.trHtml = dom.chat.tbody.html();
        $('#chat-send').click(b.chat.send);
        $('#chat-cancel').click(b.chat.cancel);
        $('#chat-draft-input').keypress(function(event) {
            console.log('keypress', event);
        });
    },
    click: function() {
        if (state.chat) {
            b.chat.chat(state.chat);
        } else if (state.contact) {
            console.warn('b.chat.click', state.contact);
        } else {
            b.chat.clickNew();
        }
    },
    clickNew: function() {
        state.chat = null;
        b.contacts.choose('chat', b.chat.chosenContact);
    },
    build: function() {
        dom.chat.tbody.empty();
        for (var i = 0; i < state.chat.messages.length; i++) {
            dom.chat.tbody.append(dom.chat.trHtml);
            var tr = dom.chat.tbody.children('tr:last-child');
            console.log('chatBuild', state.chat.messages[i]);
            if (state.chat.messages[i].contact) {
                tr.find('span.chat-contact').text(state.chat.messages[i].contact.name);
            } else {
                tr.find('span.chat-contact').text('');
            }
            tr.find('span.chat-time').text(u.date.format(state.chat.messages[i].time));
            tr.find('span.chat-message').text(state.chat.messages[i].textMessage);
            console.log('chatBuild', state.chat.messages[i].textMessage);
            tr.click(state.chat.messages[i], b.chat.messageRowClick);
        }
    },
    messageRowClick: function(e) {
        console.log('chat.messageRowClick', e.data);
    },
    chosenContact: function(contact) {
        console.log('chat.chosenContact', contact);
        var chatObject = new Chat([contact], []);
        b.chat.chat(chatObject);
    },
    chat: function(chat) {
        console.log('chat.chat', chat);
        state.chat = chat;
        b.chat.build();
        showPage(chat.name, 'chat', 'chat', chat.name);
    },
    res: function(event) {
        console.log('chat.res', event.data.name, event.data.text);
    },
    cancel: function() {
        homeClick();
    },
    send: function() {
        console.log('chat.send');
    },
    test: function() {
        var data = {
            name: 'Joe',
            text: 'Hello'
        };
        server.send('chat', data);
    },
};


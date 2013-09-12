
b.chats = {
    matcher: function(object, data) {
        return object.name === data;
    },
    loaded: function() {
        $('#chats-tbody span').text('');
        $('.chats-clickable').click(b.chats.click);
        $('.chat-new-clickable').click(b.chat.clickNew);
        dom.chats = {};
        dom.chats.tbody = $('#chats-tbody');
        dom.chats.tbodyHtml = $('#chats-tbody').html();
    },
    click: function() {
        console.log('chats.click');
        if (!state.chats) {
            console.warn('chats.click');
        } else {
            b.chats.build();
            showPage('Chats', 'chats', 'chats', null);
        }
    },
    build: function() {
        state.chats.sort(compareName);
        dom.chats.tbody.empty();
        for (var i = 0; i < state.chats.length; i++) {
            dom.chats.tbody.append(dom.chats.tbodyHtml);
            var tr = $("#chats-tbody > tr.:last-child");
            tr.find('span.chats-contact').text(state.chats[i].name);
            tr.find('span.chats-time').text(u.date.format(arrayLast(state.chats[i].messages).time));
            tr.find('span.chats-message').text(arrayLast(state.chats[i].messages).textMessage);
            tr.click(state.chats[i], b.chats.chatRowClick);
        }
    },
    chatRowClick: function(event) {
        console.log('chats.chatRowClick', event.data);
        b.chat.chat(event.data);
    },
    put: function(chat) {
        if (state.chat) {
            var index = u.array.matchIndexOf(state.chats, state.chat.name, b.chats.matcher);
            if (index >= 0) {
                state.chats[index] = chat;
            }
        } else {
            var index = u.array.matchIndexOf(state.chats, chat.name, b.chats.matcher);
            if (index !== null && index >= 0) {
                console.log('chatsPut', chat.name, index);
                state.chats[index] = chat;
            } else {
                state.chats.push(chat);
            }
        }
    },
};



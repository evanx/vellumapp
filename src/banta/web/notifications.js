
b.notifications = {
    loaded: function() {
        $('.notifications-clickable').click(b.notifications.click);
        b.notifications.tbody = $('#notifications-tbody');
        b.notifications.trHtml = b.notifications.tbody.html();
    },
    click: function() {
        console.log('click', state.notifications);
        if (isEmpty(state.notifications)) {
            console.warn('click no notifications');
        } else {
            b.notifications.build();
        }
        showPage(m.notificaitons.title, 'notifications', 'notifications', null);
    },
    build: function() {
        console.warn('notifications.build', state.notifications.length);
        state.notifications.sort(compareName);
        b.notifications.tbody.empty();
        for (var i = 0; i < state.notifications.length; i++) {
            b.notifications.tbody.append(b.notifications.trHtml);
            var tr = $("#notifications-tbody > tr:last-child");
            tr.find('span.event-contact').text(state.notifications[i].name);
            tr.find('span.event-time').text(u.date.format(arrayLast(state.notifications[i].messages).time));
            tr.find('span.event-message').text(arrayLast(state.notifications[i].messages).textMessage);
            tr.click(state.notifications[i], b.notifications.chosen);
        }
    },
    put: function(object) {
        console.log('notifications.put', object);
    },
    chosen: function(e) {
        console.log('notifications.chosen', e.data);
        b.event.edit(e.data);
    },
};

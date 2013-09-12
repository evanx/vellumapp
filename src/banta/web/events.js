

b.events = {
    loaded: function() {
        $('.events-clickable').click(b.events.click);
        $('.events-new-clickable').click(b.event.newClicked);
        if (false) {
            $('#events-tbody span').text('');
        }
        b.events.$tbody = $('#events-tbody');
        b.events.tbodyHtml = $('#events-tbody').html();
    },
    click: function() {
        console.log('click', state.events);
        if (isEmpty(state.events)) {
            console.warn('click no events');
        } else {
            b.events.build();
        }
        showPage('Events', 'events', 'events', null);
    },
    build: function() {
        console.log('events.build', state.events.length);
        state.events.sort(compareName);
        b.events.$tbody.empty();
        for (var i = 0; i < state.events.length; i++) {
            b.events.$tbody.append(b.events.tbodyHtml);
            var event = new BEvent(state.events[i]);
            console.log('events.build', event.formatDate());
            var dateRow = b.events.$tbody.find("tr.events-date-row").last();
            dateRow.find('span.events-day').text(event.formatDay());
            dateRow.find('span.events-date').text(event.formatDate());
            var infoRow = b.events.$tbody.find("tr.events-info-row").last();
            infoRow.find('span.events-name').text(event.formatName());
            infoRow.find('span.events-time').text(event.formatTime());
            infoRow.find('span.events-venue').text(event.formatVenue());
            infoRow.find('span.events-invitees').text(event.formatInvitees());
            infoRow.click(state.events[i], b.events.chosen);
            console.log('events.row', event.formatVenue(), event, state.events[i]);
        }
    },
    put: function(object) {
        console.log('events.put', object);
    },
    chosen: function(e) {
        console.log('events.chosen', e.data);
        b.event.edit(e.data);
    },
};

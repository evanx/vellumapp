
function BEvent(event) {
    this.event = event;
}

BEvent.prototype = {
    formatDay: function() {
        return u.date.formatWeekDay(this.event.date);
    },
    formatDate: function() {
        return u.date.formatTerse(this.event.date);
    },
    formatTime: function() {
        return this.event.time;
    },
    formatInvitees: function() {
        return u.array.join(this.event.invitees, ', ', function(element) {
            return element;
        });
    },     
    formatHost: function() {
        return this.event.host;
    },     
    formatName: function() {
        return this.event.name;
    },
    formatVenue: function() {
         if (!isEmpty(this.event.venue)) {
            return this.event.venue;
         }
         if (!isEmpty(this.event.host)) {
            return this.event.host;
         }
         return '';
    },
};


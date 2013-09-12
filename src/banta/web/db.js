
var db = {
    loginRes: function(res) {
        state.login = res;
        state.contacts = res.contacts;
        state.chats = res.chats;
        state.events = res.events;        
    },
    validateContactName: function(name) {
        console.log('db.validateContactName', name);
        for (var i = 0; i < state.contacts.length; i++) {
            var contact = state.contacts[i];
            console.log('db.validateContactName', contact.name, name, contact.name === name);
            if (contact.name === name) {
                return true;
            }
        }
        return false;
    },
    chats: {
        put: function(chat) {
            console.log('db.chats.put', chat);
            
        },
    },
    contacts: {
        put: function(contact) {
            console.log('db.contacts.put', contact);
            if (state.contact) {
                var index = b.contacts.getIndex(state.contact.name);
                console.log('put', state.contact.name, index);
                if (index >= 0) {
                    state.contacts[index] = contact;
                }
            } else {
                var index = b.contacts.getIndex(contact.name);
                if (index && index >= 0) {
                    console.log('put', contact.name, index);
                    state.contacts[index] = contact;
                } else {
                    state.contacts.push(contact);
                }
            }
        },
        getIndex: function(name) {
            return u.array.matchIndexOf(state.contacts, name, matchName);    
        },
        get: function(name) {
            var index = db.contacts.getIndex(name);
            if (index >= 0) {
                return state.contacts[index];
            }
            return null;            
        },
        getContacts: function(names) {
            var array = [];
            if (names && names.length > 0) {
                foreach(names, function(name) {
                    var contact = db.contacts.get(name);
                    array.push(contact);
                });
            }
            return array;
        }
    },
    events: {
        put: function(event) {
            var index = u.array.matchIndexOf(state.events, event.id, matchId);
            console.xlog('db.events.put update', event.id, index);
            if (index >= 0) {
                console.xlog('db.events.put update', event.id, index);
                state.events[index] = event;
            } else {
                state.events.push(event);
            }
        },  
        getInvitees: function(event) {
            return db.contacts.getContacts(event.invitees);
        }
    },
};

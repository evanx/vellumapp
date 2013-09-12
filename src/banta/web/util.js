
var u = {
    init: function() {
        u.string.init();
        u.date.init();
        u.array.init();
        u.validate.init();
        u.event.init();
    },
    object: {
        containsKey: function(object, key) {
            return u.number.ge(u.array.indexOf(Object.keys(object), key), 0);
        },
        makeCompare: function(property) {
            return function(a, b) {
                if (a[property] === b[property]) {
                    return 0;
                } else if (a[property].toLowerCase() > b[property].toLowerCase()) {
                    return 1;
                }
                return -1;
            }
        },
    },
    string: {
        init: function() {
            String.prototype.startsWith = function(string) {
                return this.indexOf(string) === 0;
            };
            String.prototype.indexOfIgnoreCase = function(string) {
                var index = this.indexOf(string);
                if (index >= 0) {
                    return index;
                }
                return this.toLowerCase().indexOf(string.toLowerCase());
            };
            String.prototype.startsWithIgnoreCase = function(string) {
                return this.indexOfIgnoreCase(string) === 0;
            };
            String.prototype.endsWith = function(string) {
                return this.indexOf(string) === (this.length - string.length);
            };
            String.prototype.contains = function(string) {
                return this.indexOf(string) === 0 || this.indexOf(string) > 0;
            };
            if (!String.prototype.format) {
                String.prototype.format = function() {
                    var args = arguments;
                    return this.replace(/{(\d+)}/g, function(match, number) {
                        return args[number];
                    });
                };
            }
            if (!String.prototype.isEmpty) {
                String.prototype.isEmpty = function() {
                    return isEmpty(this);
                };
            }
        },
        sanitize: function(text) {
            return text.replace(/[<>]/gi, '');
        },
    },
    array: {
        init: function() {
        },
        indexOf: function(array, element) {
            for (var i = 0; i < array.length; i++) {
                if (array[i] === element) {
                    return i;
                }
            }
            return -1;
        },
        matchIndexOf: function(array, data, matcher) {
            for (var i = 0; i < array.length; i++) {
                if (matcher(array[i], data)) {
                    return i;
                }
            }
            return -1;
        },
        addAll: function(array, other) {
            for (var i = 0; i < other.length; i++) {
                array.push(other[i]);
            }
            return array;
        },
        contains: function(array, element) {
            return u.array.indexOf(array, element) >= 0;
        },
        remove: function(array, element) {
            var index = u.array.indexOf(array, element);
            if (index >= 0) {
                array.splice(index, 1);
            }
            return array;
        },
        newRemove: function(array, element) {
            var index = u.array.indexOf(array, element);
            if (index >= 0) {
                var arr = [];
                u.array.addAll(arr, array);
                arr.splice(index, 1);
                return arr;
            }
            return array;
        },
        removeAll: function(array, other) {
            var arr = [];
            for (var i = 0; i < array.length; i++) {
                if (!u.array.contains(other, array[i])) {
                    arr.push(array[i]);
                }
            }
            array.length = 0;
            u.array.addAll(array, arr);
            return array;
        },
        newRemoveAll: function(array, other) {
            var arr = [];
            for (var i = 0; i < array.length; i++) {
                if (!u.array.contains(other, array[i])) {
                    arr.push(array[i]);
                }
            }
            return arr;
        },
        extractValues: function(array, key) {
            var values = [];
            for (var i = 0; i < array.length; i++) {
                //console.log('array.extractValues', i, array[i], array[i][key]);
                values.push(array[i][key]);
            }
            console.log('array.extractValues', array.length, key, values);
            return values;
        },
        join: function(array, delimiter, f) {
            var string = "";
            if (array) {
                for (var i = 0; i < array.length; i++) {
                    if (i > 0) {
                        string = string + delimiter;
                    }
                    string = string + f(array[i]);
                }
            }
            return string;        
        },
    },
    date: {
        init: function() {
            if (!Date.prototype.formatPretty) {
                Date.prototype.format = function() {
                    return u.format.format(this);
                }
            }
        },
        format: function(date) {
            if (!date) {
                return '';
            }
            if (moment().format('MMM Do') === moment(date).format('MMM Do')) {
                return moment(date).format('h:mm a');
            } else {
                return moment(date).format('h:mm a MMM Do');
            }
        },
        formatTerse: function(date) {
            if (!date) {
                return '';
            }
            return moment(date).format('MMM Do');
        },
        formatWeekDay: function(date) {
            if (!date) {
                return '';
            }
            return moment(date).format('dddd');
        },
    },
    number: {
        init: function() {
        },
        ge: function(number, value) {
            return number && number !== null && number >= value;
        },
        isIndex: function(number) {
            return u.number.ge(number, 0);
        }
    },
    validate: {
        init: function() {
            $.validator.addMethod("sanitary", u.validate.sanitary, "Please enter valid characters only.");
            $.validator.addMethod("contact", u.validate.contactName, "Please enter valid contact.");
        },
        contactName: function(value, element) {
            return db.validateContactName(value);
        },
        sanitary: function(value, element) {
            return !/[<>]/.test(value);
        },
        digits: function(value) {
            return /^\d+$/.test(value);
        },
        digitsLength: function(value, minLength, maxLength) {
            return value && u.validate.digits(value) && value.length >= minLength && value.length <= maxLength;
        },
        phoneNumber: function(value) {
            if (state.env === 'test' && value === '111') {
                return true;
            }
            if (value && value.length >= 10) {
                if (value.charAt(0) === '+') {
                }
                return /^\+?\d+$/.test(value);
            }
            return false;
        },
        highlight: function(element) {
            $(element).closest('.control-group').removeClass('success').addClass('error');
        },
        success: function(element) {
            $(element).addClass('valid');
            $(element).closest('.control-group').removeClass('error').addClass('success');
        },
    },
    event: {
        init: function() {
        },
        makeGetData: function(handler) {
            return function(event) {
                handler(event.data);
            };
        },
    },
    xconsole: {
        log: function() {        
        },
    },
    ui: {
        enableLink: function(component, enabled) {
            if (enabled) {
                component.removeClass('disabled');
            } else {
                component.addClass('disabled');
            }            
        },
        notify: function(data) {
            console.log(data);
        }
    }
};

console.xlog = function() {    
};

function redirectDocument() {
    console.log("redirectDocument " + window.location.protocol);
    if (window.location.protocol === "http:") {
        var host = location.host;
        var index = location.host.indexOf(':');
        if (index > 0) {
            host = location.host.substring(0, index) + ':8443';
        }
        window.location = "https://" + host + location.pathname + location.search + location.hash;
        console.log(window.location);
        return true;
    }
    return false;
}

function buildTr(array) {
    var html = "<tr>";
    for (var i = 0; i < array.length; i++) {
        html += '<td>' + array[i] + '</td>';
    }
    html += '</tr>';
    if (false) {
        console.log(html);
    }
    return html;
}

function buildTable(tbody, arrayer, list) {
    tbody.empty();
    for (var i = 0; i < list.length; i++) {
        tbody.append(buildTr(arrayer(list[i])));
    }
}

function foreach(array, handler) {
    console.log('foreach', handler.length);
    for (var i = 0; i < array.length; i++) {
        handler(array[i], i);
    }
}

function foreachKey(object, handler) {
    var keys = Object.keys(object);
    for (var i = 0; i < keys.length; i++) {
        var value = object[keys[i]];
        handler(keys[i], value, i);
    }
}

function arrayLast(array) {
    return array[array.length - 1];
}

function isEmpty(object) {
    return (object === undefined || object === null || object.length === 0);
}

function isTrue(object) {
    return (object !== undefined && object !== null && object === true);
}

function assertTrue(message, value) {
    if (!isTrue(value)) {
        alert('assert failed: ' + message);
        return false;
    } else {
        return true;
    }
}

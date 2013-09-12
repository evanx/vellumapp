

Test.max = 42;

Test.what = function() {
    return this.max;
};

function Test(name) {
    this.name = name;
    this.enabled = true;
}

Test.prototype.enabled = false;

Test.prototype.log2 = function(message) {
    console.log('Test_log2', [Test.what(), this.name, this.enabled], message);
    $('#heading').text('hello');
};

Test.prototype.log = function(message) {
    console.log('Test.log', [this.name, this.enabled, message]);
};

Test.prototype.test = function() {
    this.log2('something');
};

function Test2(name) {
   this.name = name + "2";
   this.enabled = true;
}

Test2.prototype = Object.create(Test.prototype);

$(document).ready(function() {
    utilInit();
    new Test2('test').test();
});


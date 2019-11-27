var page = require('webpage').create();
var url = "test-resources/test/test.html";

page.onConsoleMessage = function (message) {
    console.log(message);
};

page.open(url, function (status) {
    page.evaluate(function(){
        // Use your namespace instead of `cljs-test-example`:
        vlad.test.runner.run();
    });
    phantom.exit(0);
});

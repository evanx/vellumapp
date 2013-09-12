


function Bean(name) {
    this.name = name;
}

Bean.prototype.buildTable = function() {
    var html = [];
    html.push("<table>\n");
    html.push("<thead>\n");
    html.push("</thead>\n");
    html.push("<tbody>\n");
    html.push("<tr>\n");
    html.push("</tr>\n");
    html.push("</tbody>\n");
    html.push("</table>\n");   
    return html.join('');
}

function Property(name) {
    this.name = name;
}

function StringProperty(name) {
    this.name = name;
}

function NumberProperty(name) {
    this.name = name;
}

var orgBean = $.extend(new Bean("org"), {
    properties: [
        new NumberProperty("orgId"),
        new StringProperty("orgName"),
        new StringProperty("displayLabel")
    ]    
});

function initBean() {
    console.log(orgBean);
    console.log(orgBean.properties[0]);
    console.log(orgBean.buildTable());
}

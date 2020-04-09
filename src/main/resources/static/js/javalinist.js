function startSse() {
    var evtSource = new EventSource("/sse/users");
    evtSource.addEventListener("CREATED", function(e) {
        var data = JSON.parse(e.data);
        console.log(e.type, data);
        document.getElementById("sseContent").appendChild(renderEvent(e.type, data));
    }); evtSource.addEventListener("UPDATED", function(e) {
        var data = JSON.parse(e.data);
        console.log(e.type, data);
        document.getElementById("sseContent").appendChild(renderEvent(e.type, data));
    });evtSource.addEventListener("DELETED", function(e) {
        var data = JSON.parse(e.data);
        console.log(e.type, data);
        document.getElementById("sseContent").appendChild(renderEvent(e.type, data));
    })

    function renderEvent(eventName, data) {
        var el = document.createElement("div");
        el.innerHTML = eventName + ": " + JSON.stringify(data);
        return el;
    }
}
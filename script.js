function loadActionList() {
    var xmlhttp;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXElement("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            var pack = JSON.parse(xmlhttp.responseText);
            var actions = pack.actions;
            window.actions = actions;
            document.getElementById('ajax_progress').style.display = "none";
        }
    };
    xmlhttp.open("GET", "/action/get/list/", true);
    xmlhttp.send();
}

function invokeAction(eventName, params) {
    var index;
    for (index = 0; index < window.actions.length; index++) {
        var action = window.actions[index];
        if (action.action == eventName) {
            var args = action.args;
            var arg_i;
            var req = "/action/invoke/";
            var param = action.action;
            for (arg_i = 0; arg_i < args.length; arg_i++) {
                if (document.getElementById(args[arg_i].Arg) !== null) {
                    param += "&" + args[arg_i].Arg + "=" + document.getElementById(args[arg_i].Arg).value;
                } else {
                    param += "&" + args[arg_i].Arg + "=null";
                }
            }
            if (params !== undefined) {
                param += "&params="+params;
            }
            var xmlhttp;
            if (window.XMLHttpRequest) {
                xmlhttp = new XMLHttpRequest();
            } else {
                xmlhttp = new ActiveXElement("Microsoft.XMLHTTP");
            }
            document.getElementById("ajax_progress").style.display = 'block';
            xmlhttp.onreadystatechange = function() {
                if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                    var pack = JSON.parse(xmlhttp.responseText);
                    var updates = pack.updates;
                    var index;
                    document.getElementById("ajax_progress").style.display = 'none';
                    document.getElementById("ajax_progress").value = 0;
                    for (index = 0; index < updates.length; index++) {
                        var element = updates[index].element;
                        var html = updates[index].html;
                        if (element == "title") {
                            document.title = html;
                        } else if (element == "touch") {
                            html = JSON.parse(html);
                            activateSwipe(html.element, html.calls);
                        } else if (element == "untouch") {
                            var surface = document.getElementById(html);
                            if (surface.touch_ui !== undefined) {
                                surface.removeEventListener("touchstart", surface.touch_ui.touchstart);
                                surface.removeEventListener("touchmove", surface.touch_ui.touchmove);
                                surface.removeEventListener("touchend", surface.touch_ui.touchend);
                            }
                        } else {
                            document.getElementById(element).innerHTML = html;
                        }
                    }
                    window.scroll(0, 0);
                    var fade_;
                    window.setTimeout(function () {
                    	$('#messages').contents().each(function (i) {
                    		$(this).fadeTo(1000, 0);
                    	});
                    }, 2000);
                    window.setTimeout(function () {
                    	document.getElementById('messages').innerHTML = '';
                    	window.clearInterval(fade_);
                    }, 3000);
                }
                document.getElementById("ajax_progress").value = xmlhttp.readyState;
            };
            xmlhttp.open("POST", req, true);
            xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xmlhttp.send(param);
            document.getElementById("messages").innerHTML = "";
        }
    }
}

function activateSwipe(element, calls) {
    var surface = document.getElementById(element),
    startX,
    startY,
    distX,
    distY,
    threshold = 50,
    allowedTime = 200,
    elapsedTime,
    startTime;
    surface.touch_ui = new Object();
    surface.touch_ui.touchstart = function(e) {
        document.getElementById('messages').innerHTML = '';
        var touchobj = e.changedTouches[0];
        distX = 0;
        distY = 0;
        startX = touchobj.pageX;
        startY = touchobj.pageY;
        startTime = new Date().getTime();
        e.preventDefault();
    };
    surface.addEventListener('touchstart', surface.touch_ui.touchstart , false);
    
    surface.touch_ui.touchmove = function(e) {
        e.preventDefault();
    };
    surface.addEventListener('touchmove', surface.touch_ui.touchmove, false);
    
    surface.touch_ui.touchend = function(e) {
        var touchobj = e.changedTouches[0];
        distX = touchobj.pageX - startX;
        distY = touchobj.pageY - startY;
        elapsedTime = new Date().getTime() - startTime;
        if (Math.abs(distX) >= threshold && elapsedTime <= allowedTime && Math.abs(distY) <= 100) {
            if (distX > 0) {
                handleswipe('r', calls);
            } else if (distX < 0) {
                handleswipe('l', calls);
            }
        } else if (Math.abs(distY) >= threshold && elapsedTime <= allowedTime && Math.abs(distX) <= 100) {
            if (distY > 0) {
                handleswipe('d', calls);
            } else if (distY < 0) {
                handleswipe('u', calls);
            }
        }
    };
    surface.addEventListener('touchend', surface.touch_ui.touchend, false);
}

function handleswipe(direction, calls) {
    if (direction == 'u') {
        invokeAction(calls[0]);
    } else if (direction == 'r') {
        invokeAction(calls[1]);
    } else if (direction == 'd') {
        invokeAction(calls[2]);
    } else if (direction == 'l') {
        invokeAction(calls[3]);
    }
}
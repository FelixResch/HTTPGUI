function loadActionList(onload_action) {
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
			if (onload_action !== undefined)
				invokeAction(onload_action);
		}
	};
	xmlhttp.open("GET", "/action/get/list/", true);
	xmlhttp.send();
}

function addMessage(message) {
	document.getElementById('messages').innerHTML += '<div class="alert">'
			+ message + '</div>';
}

function handleUpdate(pack) {
	var updates = pack.updates;
	var index;
	var noscroll = false;
	document.getElementById("ajax_progress").style.display = 'none';
	document.getElementById("ajax_progress").value = 0;
	for (index = 0; index < updates.length; index++) {
		var element = updates[index].element;
		var html = updates[index].html;
		if (element == "title") {
			document.title = html;
		} else if (element == "touchsw") {
			html = JSON.parse(html);
			activateSwipeSideways(html.element, html.calls);
		} else if (element == "touch") {
			html = JSON.parse(html);
			activateSwipe(html.element, html.calls);
		} else if (element == "untouch") {
			var surface = document.getElementById(html);
			if (surface.touch_ui !== undefined) {
				surface.removeEventListener("touchstart",
						surface.touch_ui.touchstart);
				surface.removeEventListener("touchmove",
						surface.touch_ui.touchmove);
				surface.removeEventListener("touchend",
						surface.touch_ui.touchend);
			}
		} else if (element == "invoke") {
			var front = JSON.parse(html);
			invokeActionManual(front.element, front.params, front.param);
		} else if (element == "stream") {
			var front = JSON.parse(html);
			subscribeToEventStream(front.url, front.display);
		} else if (element == "buffer_img") {
			var img = document.createElement('img');
			img.src = html;
		} else if (element == "noscroll") {
			noscroll = true;
		} else {
			document.getElementById(element).innerHTML = html;
		}
	}
	if (!noscroll)
		window.scroll(0, 0);
	var fade_;
	window.setTimeout(function() {
		$('#messages').contents().each(function(i) {
			$(this).fadeTo(1000, 0);
		});
	}, 2000);
	window.setTimeout(function() {
		document.getElementById('messages').innerHTML = '';
		window.clearInterval(fade_);
	}, 3000);
}

function invokeAction(eventName, params, event) {
	var index;
	event = event || window.event;
	for (index = 0; index < window.actions.length; index++) {
		var action = window.actions[index];
		if (action.action == eventName) {
			var args = action.args;
			var arg_i;
			var req = "/action/invoke/";
			var param = action.action;
			for (arg_i = 0; arg_i < args.length; arg_i++) {
				if (document.getElementById(args[arg_i].Arg) !== null) {
					var elem = document.getElementById(args[arg_i].Arg);
					if (elem.value == null || elem.value == undefined
							|| elem.value == "") {
						param += "&"
							+ args[arg_i].Arg
							+ "="
							+ document.getElementById(args[arg_i].Arg).placeholder;
					} else {
						param += "&"
								+ args[arg_i].Arg
								+ "="
								+ document.getElementById(args[arg_i].Arg).value;
					}
				} else {
					if (event !== undefined) {
						try {
							param += "&" + args[arg_i].Arg + "="
									+ eval(args[arg_i].Arg);
						} catch (e) {
							param += "&" + args[arg_i].Arg + "=null";
						}
					} else
						param += "&" + args[arg_i].Arg + "=null";
				}
			}
			if (params !== undefined) {
				param += "&params=" + params;
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
					handleUpdate(pack);
				} else if (xmlhttp.readyState == 4 && xmlhttp.status != 200) {
					addMessage("Failed to Fetch updates");
					document.getElementById("ajax_progress").style.display = 'none';
					document.getElementById("ajax_progress").value = 0;
				}
				document.getElementById("ajax_progress").value = xmlhttp.readyState;
			};
			xmlhttp.open("POST", req, true);
			xmlhttp.setRequestHeader("Content-type",
					"application/x-www-form-urlencoded");
			xmlhttp.send(param);
			document.getElementById("messages").innerHTML = "";
		}
	}
}

function invokeActionManual(eventName, elements, params) {
	var index;
	var args = elements;
	var arg_i;
	var req = "/action/invoke/";
	var param = eventName;
	for (arg_i = 0; arg_i < args.length; arg_i++) {
		if (document.getElementById(args[arg_i]) !== null) {
			var elem = document.getElementById(args[arg_i]);
			if (elem.value == null || elem.value == undefined
					|| elem.value == "") {
				param += "&"
					+ args[arg_i]
					+ "="
					+ document.getElementById(args[arg_i]).placeholder;
			} else {
				param += "&"
						+ args[arg_i]
						+ "="
						+ document.getElementById(args[arg_i]).value;
			}
		} else {
			param += "&" + args[arg_i] + "=null";
		}
	}
	if (params !== undefined) {
		param += "&params=" + params;
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
			handleUpdate(pack);
		} else if (xmlhttp.readyState == 4 && xmlhttp.status != 200) {
			addMessage("Failed to Fetch updates");
			document.getElementById("ajax_progress").style.display = 'none';
			document.getElementById("ajax_progress").value = 0;
		}
		document.getElementById("ajax_progress").value = xmlhttp.readyState;
	};
	xmlhttp.open("POST", req, true);
	xmlhttp.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	xmlhttp.send(param);
	document.getElementById("messages").innerHTML = "";
}

function activateSwipeSideways(element, calls) {
	var surface = document.getElementById(element), tmpX, tmpY, startX, startY, distX, distY, threshold = 50, allowedTime = 200, elapsedTime, startTime;
	surface.touch_ui = new Object();
	surface.touch_ui.touchstart = function(e) {
		document.getElementById('messages').innerHTML = '';
		var touchobj = e.changedTouches[0];
		distX = 0;
		distY = 0;
		startX = tmpX = touchobj.pageX;
		startY = tmpY = touchobj.pageY;
		startTime = new Date().getTime();
	};
	calls[0] = null;
	calls[2] = null;
	surface.addEventListener('touchstart', surface.touch_ui.touchstart, false);

	surface.touch_ui.touchmove = function(e) {
		var touchobj = e.changedTouches[0];
		distX = touchobj.pageX - tmpX;
		distY = touchobj.pageY - tmpY;
		if (distX == 0 || distY / distX > 5) {
			window.scrollBy(0, distY);
		}
	};
	surface.addEventListener('touchmove', surface.touch_ui.touchmove, false);

	surface.touch_ui.touchend = function(e) {
		var touchobj = e.changedTouches[0];
		distX = touchobj.pageX - startX;
		distY = touchobj.pageY - startY;
		elapsedTime = new Date().getTime() - startTime;
		if (Math.abs(distX) >= threshold && elapsedTime <= allowedTime
				&& Math.abs(distY) <= 100) {
			if (distX > 0) {
				handleswipe('r', calls);
			} else if (distX < 0) {
				handleswipe('l', calls);
			}
		} else if (Math.abs(distY) >= threshold && elapsedTime <= allowedTime
				&& Math.abs(distX) <= 100) {
			if (distY > 0) {
				handleswipe('d', calls);
			} else if (distY < 0) {
				handleswipe('u', calls);
			}
		}
	};
	surface.addEventListener('touchend', surface.touch_ui.touchend, false);
}

function activateSwipe(element, calls) {
	var surface = document.getElementById(element), startX, startY, distX, distY, threshold = 50, allowedTime = 200, elapsedTime, startTime;
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
	surface.addEventListener('touchstart', surface.touch_ui.touchstart, false);

	surface.touch_ui.touchmove = function(e) {
		e.preventDefault();
	};
	surface.addEventListener('touchmove', surface.touch_ui.touchmove, false);

	surface.touch_ui.touchend = function(e) {
		var touchobj = e.changedTouches[0];
		distX = touchobj.pageX - startX;
		distY = touchobj.pageY - startY;
		elapsedTime = new Date().getTime() - startTime;
		if (Math.abs(distX) >= threshold && elapsedTime <= allowedTime
				&& Math.abs(distY) <= 100) {
			if (distX > 0) {
				handleswipe('r', calls);
			} else if (distX < 0) {
				handleswipe('l', calls);
			}
		} else if (Math.abs(distY) >= threshold && elapsedTime <= allowedTime
				&& Math.abs(distX) <= 100) {
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

function subscribeToEventStream(url, display) {
	if (!!window.EventSource) {
		var source = new EventSource(url);
		source.addEventListener('message', function(e) {
			document.getElementById(display).innerHTML = e.data;
		}, false);
		source.addEventListener('open', function(e) {
			// console.log('Subscribed to Stream');
		}, false);
		source.addEventListener('error', function(e) {
			// console.log('Error in Stream');
		}, false);
	} else {
		addMessage('Your browser is not able to show server sent events, hence it won\'t display this page correctly!');
		return;
	}
}
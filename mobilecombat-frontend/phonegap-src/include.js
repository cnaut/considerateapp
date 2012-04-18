﻿if (navigator.userAgent.toLowerCase().match(/android/)) {
	document.write("<script charset='utf-8' src='android\/cordova-1.6.0.js'><\/script>");
	document.write("<script type='text\/javascript' charset='utf-8' src='android\/cdv-plugin-fb-connect.js'><\/script>");
	document.write("<script type='text\/javascript' charset='utf-8' src='facebook_js_sdk.js'><\/script>");
   
} else if (navigator.userAgent.toLowerCase().match(/windows/)) {
	document.write("<script type='text\/javascript' charset='utf-8' src='wp\/cordova-1.6.0.js'><\/script>");
	document.write("<script type='text\/javascript' charset='utf-8' src='wp\/pg-plugin-fb-connect.js'><\/script>");
	document.write("<script type='text\/javascript' charset='utf-8' src='facebook_js_sdk.js'><\/script>");
	document.write("<script type='text\/javascript' charset='utf-8' src='wp\/ChildBrowser.js'><\/script>");
}
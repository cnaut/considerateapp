﻿var battleID;
var userID;
var maxNumPeopleInTable = 15;

var users_timeout;

var users;

/*
* Parse response from server and load users into table
*/
function onCombatantsRequestSuccess(serverResponse) {
  ids = serverResponse;
  if (ids.length != 0) {
	sendFacebookRequest("GET", null, "?fields=id,name,picture&ids=" + ids, onFacebookSuccess, onFacebookFail, false, "");
  }
}

function onCombatantsRequestFail(serverResponse) {

}

/*
* Function called when nearby.html is opened.
*/
function getNearbyUsers() {
  userID = window.name;
  sendXmlhttpRequest("GET", null, "allusers", onCombatantsRequestSuccess, onCombatantsRequestFail, false, "");
}

/*
* Load users dynamically onto table
*/
function onFacebookSuccess(serverResponse) {
  console.log("FB Success: "+ serverResponse);
  ids = ids.split(",");

  users = jQuery.parseJSON(serverResponse);
  var usersTable = document.getElementById('com_table');
  var numToDisplay = ids.length;
  if (numToDisplay > maxNumPeopleInTable) {
    numToDisplay = maxNumPeopleInTable;
  }

  console.log("length: " +numToDisplay);
  for (var i = 0; i < numToDisplay; i++) {
 	  var row = usersTable.insertRow(i);
 	  row.onclick = changeSelect;
 	 
 	 var photoCell = row.insertCell(0);
 	 var photo = document.createElement("IMG");
 	 
 	 photo.setAttribute("src", users[ids[i]].picture);
 	 photo.setAttribute("width", "50");
 	 photo.setAttribute("height", "50");
	 photoCell.appendChild(photo);

     var nameCell = row.insertCell(1);
     nameCell.className = "deselected";
     nameCell.innerHTML = users[ids[i]].name;
	 console.log("CHECKIN " + users[ids[i]].name); 	
	 }
 	//pollForBattle();
}

function onFacebookFail(serverResponse) {
  console.log("FB Fail: " +serverResponse);
}

/*
* Toggle whether or not a combatant is selected
*/
function changeSelect() {
  var row = this;
  var rowNum = row.rowIndex;
  var cell = row.cells[1];
  if (cell.className === "selected") {
    cell.className = "deselected";
    console.log("CHECKIN " + rowNum + " deselected");
  } else {
    cell.className = "selected";
    console.log("CHECKIN " + rowNum + " selected");
  }
}

/* 
* Save the battleID returned by the server after
* we send them the combatants and start the battle
*/
function onBattleRequestSuccess(serverResponse) {
  // On success, save the returned unique battle ID
  battleID = serverResponse;
  window.location = 'battle.html';
  console.log("CHECKIN TO BATTLE " + battleID);
}

function onBattleRequestFail(serverResponse) {

}

/*
* Send combatants to the server
*/
function sendBattleRequest() {
  var usersTable = document.getElementById('com_table').rows;
  var selectedUsers = new Array();
  for (var i = 0; i < usersTable.length; i++) {
    cell = usersTable[i].cells[1];
    if (cell.className === "selected") {		
      selectedUsers.push(ids[i]);
    }
  }
  var JSONtext = "{\"users\":" + JSON.stringify(selectedUsers, null) + "}";
  console.log("CHECKIN " + JSONtext);

  sendXmlhttpRequest("POST", JSONtext, "startbattle", onBattleRequestSuccess, onBattleRequestFail, false, "");
}

// Get nearby users once phone loads
function onDeviceReady() {
  console.log("CHECKIN");
  document.getElementById("fight_button").addEventListener("click", sendBattleRequest, false);
  getNearbyUsers();
}

document.addEventListener("deviceready", onDeviceReady, false);

var poll = 0;

function onPollRequestSuccess(serverResponse) {
  poll = poll + 1;
  console.log("BattleID:  " + serverResponse);
  console.log("Number:  " + poll + " for " + userID);

  clearTimeout(users_timeout);

  if (serverResponse != "no battle") {
    console.log("Let's battle!");
    window.location = 'battle.html';
  } else {
    console.log("pollForBattle()");
    users_timeout = setTimeout(pollForBattle, 10000);
  }
}

function onPollRequestFail(serverResponse) {
  clearTimeout(users_timeout);
  users_timeout = setTimeout(pollForBattle, 10000);
}

function pollForBattle() {
  var user = "{\"id\":\"" + userID + "\"}";
  sendXmlhttpRequest("POST", user, "getbattle", onPollRequestSuccess, onPollRequestFail, false, "");
}

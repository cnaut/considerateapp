var battleID;
var userID;
var maxNumPeopleInTable = 15;

var users_timeout;

var users;

/*
* Parse response from server and load users into table
*/
function onCombatantsRequestSuccess(serverResponse) {
  // Parse json string into a jquery dictionary
  users = jQuery.parseJSON(serverResponse);
  if (users.length != 0) {
    loadUsers(users);
  }
}

/*
* Function called when nearby.html is opened.
*/
function getNearbyUsers() {
  userID = window.name;
  sendXmlhttpRequest("GET", null, "allusers", onCombatantsRequestSuccess, false, "");
}

/*
* Load users dynamically onto table
*/
function loadUsers(users) {
  var usersTable = document.getElementById('com_table');

  var numToDisplay = users.length;
  if (numToDisplay > maxNumPeopleInTable) {
    numToDisplay = maxNumPeopleInTable;
  }

  for (var i = 0; i < numToDisplay; i++) {
    var row = usersTable.insertRow(i);
    row.onclick = changeSelect;

    var photoCell = row.insertCell(0);
    var photo = document.createElement("IMG");
    photo.setAttribute("src", baseURL + "user_photos/" + users[i].fields.photo);
    photo.setAttribute("width", "50");
    photo.setAttribute("height", "50");
    photoCell.appendChild(photo);

    var nameCell = row.insertCell(1);
    nameCell.className = "deselected";
    nameCell.innerHTML = users[i].fields.name;
    console.log("CHECKIN " + users[i].fields.name);
  }

  pollForBattle();
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

/*
* Send combatants to the server
*/
function sendBattleRequest() {
  var usersTable = document.getElementById('com_table').rows;
  var selectedUsers = new Array();
  for (var i = 0; i < usersTable.length; i++) {
    cell = usersTable[i].cells[1];
    if (cell.className === "selected") {
      selectedUsers.push(users[i].pk);
    }
  }
  var JSONtext = "{\"users\":" + JSON.stringify(selectedUsers, null) + "}";
  console.log("CHECKIN " + JSONtext);

  sendXmlhttpRequest("POST", JSONtext, "startbattle", onBattleRequestSuccess, false, "");
}

// Get nearby users once phone loads
function onDeviceReady() {
  console.log("CHECKIN");
  document.getElementById("fight_button").addEventListener("click", sendBattleRequest, false);
  getNearbyUsers();
}

document.addEventListener("deviceready", onDeviceReady, false);

var poll = 0;

function pollForBattle() {
  // Create an xmlhttprequest
  var xmlhttp = getXmlhttpRequest();

  // Open and send the get request
  xmlhttp.open("POST", baseURL + "getbattle", false);
  xmlhttp.send("userID=" + userID);

  poll = poll + 1;
  console.log("BattleID:  " + xmlhttp.responseText);
  console.log("Number:  " + poll + " for " + userID);

  clearTimeout(users_timeout);
  if (xmlhttp.responseText.length > 20) {
    console.log("Let's battle!");
    window.location = 'battle.html';
  } else {
    console.log("pollForBattle()");
    setTimeout(pollForBattle(), 10000);
  }
}
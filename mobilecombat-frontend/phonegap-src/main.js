var battleID;
var maxNumPeopleInTable = 15;

var userID;
var cells;
var users;

// Read a page's GET URL variables and return them as an associative array.
function getUrlVars() {
  var vars  , hash;
  var hashes = window.location.href.slice(window.location.href.indexOf('#') + 1).split('&');

  vars = hashes[0].split('=')[1];
   
  return vars;
}

/*
 * Function called when nearby.html is opened.
 */
function getNearbyUsers() {
    userID = getUrlVars();

    // Create an xmlhttprequest
    var xmlhttp = getXmlhttpRequest();

    // Open and send the get request
    xmlhttp.open("GET", baseURL + "allusers", false);
    xmlhttp.send();
    
    console.log(xmlhttp.responseText);
    // Parse json string into a jquery dictionary
    users = jQuery.parseJSON(xmlhttp.responseText);
    if(users.length != 0) { 
		loadUsers(users);
	}
}

/*
 * Load users dynamically onto table
 */ 
function loadUsers() {
  usersTable = document.getElementById('com_table');
  
  // Create the global array of the boolean variable 
  cells = new Array();
  
  var numToDisplay = users.length;
  if(numToDisplay > maxNumPeopleInTable) {
    numToDisplay = maxNumPeopleInTable;
  }


  for(var i = 0; i < numToDisplay; i++) {
    var row = usersTable.insertRow(i);
	row.onclick = changeSelect;
	var photoCell = row.insertCell(0);
	var photo = document.createElement("IMG");
    photo.setAttribute("src",  baseURL + "user_photos/" + users[i].fields.photo);
	photo.setAttribute("width", "50");
	photo.setAttribute("height", "50");
	photoCell.appendChild(photo);

    var nameCell = row.insertCell(1);
    nameCell.style.color = "white";
	cells[i] = false;
    console.log(users[i].fields.name);
	nameCell.innerHTML = users[i].fields.name;
  }
  pollForBattle();
}

function changeSelect() {
    var row = this;
    var rowNum = row.rowIndex;
    var cell = row.childNodes[1];
    if(cells[rowNum] == true) {
		cell.style.color = "white";
		cells[rowNum] = false;
		console.log("unselect");
    } else {
		cell.style.color = "blue";
		cells[rowNum] = true;
		console.log("select");
    }
}

/* 
 * Create, format, and send a post request
 */
function onRequestSuccess(serverResponse) {
    // On success, save the returned unique server ID
    battleID = serverResponse;
    window.location = 'battle.html';
}

function sendRequestBattle() {
    usersTable = document.getElementById('com_table');
    var selectedUsers = new Array();
    var index = 0;
    for(var i = 0; i < cells.length; i++) {
		if(cells[i] == true) {
			selectedUsers[index] = users[i].pk;
			index++;
		}
    }
    var JSONtext = "{\"users\":" + JSON.stringify(selectedUsers, null) + "}";
    console.log(JSONtext);

    sendXmlhttpRequest("POST", JSONtext, "startbattle", onRequestSuccess, false, "");
}

document.addEventListener("deviceready", onDeviceReady, false);

// once the device ready event fires, you can safely do your thing! -jm
function onDeviceReady() {
  console.log("nearby.html");
  getNearbyUsers();
}

function pollForBattle() {
  // Create an xmlhttprequest
  var xmlhttp = getXmlhttpRequest();
  // Open and send the get request
  xmlhttp.open("POST", baseURL + "getbattle", false);
  xmlhttp.send("userID=" + userID);

  console.log("Jabababab :  " + xmlhttp.responseText);
  console.log("PFSas :  " + userID);

  if (xmlhttp.responseText.length > 22 && xmlhttp.responseText.length < 27) {
    console.log("dfdgg");
    window.location = 'battle.html';
  } else {
    console.log("dfd");
    setTimeout(pollForBattle(), 3000);
  }
}
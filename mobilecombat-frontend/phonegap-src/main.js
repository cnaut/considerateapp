var userURL = "http://184.169.136.30:8004/";
var serverID;
var maxNumPeopleInTable = 10;

var cells;
var users;

/*
 * Get xmlhttprequest
 */
function getXmlhttpRequest() {
  var xmlhttp;
  try {
    xmlhttp = new XMLHttpRequest();
  } catch (trymicrosoft) {
    try {
      xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (othermicrosoft) {
      try {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
      } catch (failed) {
        alert("http request could not be created");
      }
    }
  }
  return xmlhttp;
}

/*
 * Function tto get photo from phone's photoalbum.
 */
function getPhoto() {
  // Retrieve image file location from specified source
  navigator.camera.getPicture(onSuccess, onFail,
							  {sourceType : Camera.PictureSourceType.SAVEDPHOTOALBUM});
}

/*
 * Callback function for success in getting photo.
 * Show photo in background and enable send button.
 */
function onSuccess(imageURI) {

  var image = document.getElementById('image');

  // Unhide image elements
  image.style.visibility = 'visible';

  var button = document.getElementById('save_button');
  button.disabled = false;

  // Show the captured photo
  image.src = imageURI;
}

/*
 * Callback function for failure in getting photo
 */
function onFail(message) {
  alert('Error Getting picture');
}

/* 
 * Create, format, and send a post request
 */
function sendRequest() {
  var image = document.getElementById("image");
  var name = document.getElementById("name");

  // Create an xmlhttprequest
  var xmlhttp = getXmlhttpRequest();

  // Format the request string
  var boundaryString = "AaBbCcX30";
  var boundary = "--" + boundaryString;
  var postContent = "\r\n" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"name\"\r\n\r\n" +
					name.value + "\r\n" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"photo\"; filename=" +
			        image.src + "\r\n" +
                    "Content-Type: application/octet-stream\r\n" + "\r\n" +
					getBase64Image(image) + "\r\n" + boundary + "\r\n";

  // Open port to URL
  xmlhttp.open("POST", userURL + "adduser", true);

  // Declare the callback function for request
  xmlhttp.onreadystatechange = function() {
	if (xmlhttp.readyState == 4) {
      if(xmlhttp.status == 200) {
		// On success, save the returned unique server ID
		serverID = xmlhttp.responseText;
        console.log(xmlhttp.responseText);
		window.location = 'nearby.html';
	  }
    }
  }

  // Set Request header to multipart data
  xmlhttp.setRequestHeader('Content-Type', 'multipart/form-data; boundary=' + boundaryString);

  // Send the request
  xmlhttp.send(postContent);
}

/*
 * Encode the image into Base64 data.
 */
function getBase64Image(img) {
  // Create an empty canvas element
  var canvas = document.createElement("canvas");
  canvas.width = img.width;
  canvas.height = img.height;

  // Copy the image contents to the canvas
  var ctx = canvas.getContext("2d");
  ctx.drawImage(img, 0, 0);

  // Get the data-URL formatted image
  // Firefox supports PNG and JPEG. You could check img.src to
  // guess the original format, but be aware the using "image/jpg"
  // will re-encode the image.
  var dataURL = canvas.toDataURL("image/png");
  console.log("data: " + dataURL);
  return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
}

/*
 * Function called when nearby.html is opened.
 */
function getNearbyUsers() {

  // Create an xmlhttprequest
  var xmlhttp = getXmlhttpRequest();

  // Open and send the get request
  xmlhttp.open("GET", userURL + "allusers", false);
  xmlhttp.send();
  
  console.log(xmlhttp.responseText);
  // Parse json string into a jquery dictionary
  users = jQuery.parseJSON(xmlhttp.responseText);
  if(users.length != 0) 
    loadUsers(users);
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
    photo.setAttribute("src",  userURL + "user_photos/" + users[i].fields.photo);
	photo.setAttribute("width", "50");
	photo.setAttribute("height", "50");
	photoCell.appendChild(photo);

    var nameCell = row.insertCell(1);
    nameCell.style.color = "white";
	cells[i] = false;
    console.log(users[i].fields.name);
	nameCell.innerHTML = users[i].fields.name;
  }
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
  jQuery.post(userURL + "startbattle", JSONtext);
  //, function(battleID) {
  //  console.log("battleID : " + battleID);
	//window.location = 'battle.html';
  //});
}
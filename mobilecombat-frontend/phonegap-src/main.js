var userURL = "http://184.169.136.30:8002/";
var serverID;

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

  return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
}

function getNearbyUsers() {

  // Create an xmlhttprequest
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

  // Open and send the get request
  xmlhttp.open("GET", userURL + "allusers", false);
  xmlhttp.send();
  
  // Parse json string into a jquery dictionary
  var users = jQuery.parseJSON(xmlhttp.responseText);
  if(users.length != 0) 
    loadUsers(users);
}

function loadUsers(users) {
  usersTable = document.getElementById('com_table');
  for(var i = 0; i < users.length; i++) {
    var row = usersTable.insertRow(i);
    var cell = row.insertCell(0);
	cell.setAttribute("selected", "y");
    console.log(users[i].fields.name);
	cell.innerHTML = users[i].fields.name;
  }
  usersTable.onload = addCellListeners();
}

function addCellListeners() {
  function changeSelect() {
    var cell = typeof this;
    if(cell.getAttribute("selected") == "y") {
      cell.style.color = "white";
	  cell.setAttribute("selected", "n");
      console.log("unselect");
    } else {
	  cell.style.color = "blue";
	  cell.setAttribute("selected", "y");
      console.log("select");
    }
  }
  /* Check that the getElementById method is
  * supported before trying to use it.
  */
  if(document.getElementById) {
    /* Change the string, 'com_table', to reflect the actual id. */
    var table = document.getElementById('com_table'), rows;
    /* Ensure a reference was obtained and
     * that we can access the rows.
     */
    if(table && (rows = table.rows)) { 
      /* Add the listener to each row in the table. */
	  console.log("here");
      for(var i = 0, n = rows.length; i < n; ++i) {
        rows[i].cells[0].onclick = changeSelect();
      }
	  
	  console.log("here1");
    }
  }
}
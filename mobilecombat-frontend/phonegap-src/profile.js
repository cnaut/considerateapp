var userID;

/* 
 * Saves the userID returned by the server after
 * we send in our profile
 */
function onRequestSuccess(serverResponse) {
    // On success, save the returned unique user ID
    userID = serverResponse;
    window.location = 'nearby.html';
	console.log("PROFILE TO CHECKIN");
}

/*
 * Send profile information to the server
 */
function sendUserRequest() {
    var image = document.getElementById("image");
    var name = document.getElementById("name");

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

    sendXmlhttpRequest("POST", postContent, "adduser", onRequestSuccess, true, boundaryString);
}

/*
 * Get photo from phone's photo album.
 */
function getPhoto() {
    // Retrieve image file location from specified source
    navigator.camera.getPicture(onPhotoSuccess, onPhotoFail,
				{sourceType : Camera.PictureSourceType.SAVEDPHOTOALBUM});
}

/*
 * Callback function for success in getting photo.
 * Show photo in background and enable send button.
 */
function onPhotoSuccess(imageURI) {

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
function onPhotoFail(message) {
    alert('Error Getting picture');
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
    console.log("PROFILE image data: " + dataURL);
    return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
}

// Log that we are in the PROFILE page
function onDeviceReady() {
    console.log("PROFILE");
	document.getElementById("photo_button").addEventListener("click", getPhoto, false);
	document.getElementById("save_button").addEventListener("click", sendUserRequest, false);
}

function onBodyLoad()
{
	console.log("HERE")
    //if phonegap, need to toggle these
    //if (typeof navigator.device == "undefined"){
    	//document.addEventListener("deviceready", onDeviceReady, false);
    //} else {
    	onDeviceReady();
    //}

}

console.log("YO")
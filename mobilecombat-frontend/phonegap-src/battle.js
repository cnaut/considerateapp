//timer variables
var c=0;
var f=0;
var t;
var g;
var timer_is_on=0;

//accelerometer variables
var watchID = null;
var initial = 0;
var accX = 0;
var accY = 0;
var accZ = 0;
var valueX;
var valueY;
var valueZ;

function stopWatch() {
    if (watchID) {
		navigator.accelerometer.clearWatch(watchID);
		watchID = null;
    }
}

function onSuccess(acceleration) {
    if (initial == 0) {
		console.log("zerooooooooooo");
        accX = acceleration.x;
        accY = acceleration.y;
        accZ = acceleration.z;
        valueX.value = "X: " + acceleration.x;
        valueY.value = "Y: " + acceleration.y;
        valueZ.value = "Z: " + acceleration.z;
        initial = 1;
    } else {
        console.log(accX);
        if (acceleration.x != accX) {
			document.getElementById("accelerometer").innerHTML = "You Lose!";
			stopCount();
			stopWatch();       
			//ADD CODEEEEEE
		}
    }
}



//watch acceleration
function startWatch() {
    valueX = document.getElementById("valueX");
    valueY = document.getElementById("valueY");
    valueZ = document.getElementById("valueZ");
    var options = {frequency: 500};
    watchID = navigator.accelerometer.watchAcceleration(onSuccess, onError, options);
}

function onError() {
    alert('onError!');
}

function countUp() {
    document.getElementById('count_up').innerHTML = f;
    f = f+1;
    g = setTimeout("countUp()",1000);
}

function timedCount() {
    document.getElementById('timer').innerHTML = (5-c);
    c = c+1;
    if (c != 6) {
        t = setTimeout("timedCount()",1000);
    } else {
        countUp();
        startWatch();
    }
}

function startTimer() {
    if (!timer_is_on) {
        timer_is_on = 1;
        timedCount();
    }
}

function stopCount() {
    clearTimeout(g);
}

window.addEventListener('load', startTimer, false);
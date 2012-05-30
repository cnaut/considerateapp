var userID;
var battleID;
var errorRange = 0.2;

//timer variables
var count_down_time = 0;
var count_up_time = 0;
var battle_timeout;
var timer_is_on = 0;
var player_count = 0;

//accelerometer variables
var watchID = null;
var initial = 0;
var accX = 0;
var accY = 0;
var accZ = 0;
var valueX;
var valueY;
var valueZ;

function endBattleCallback() {
  window.location = "index.html";
}

function stopWatch() {
  if (watchID) {
    navigator.accelerometer.clearWatch(watchID);
    watchID = null;
  }
}

function countUp() {
  document.getElementById("message").innerHTML = "Begin " + count_up_time;
  //Every 5 seconds, check to see if the battle has ended 
  if (count_up_time % 5 == 0) {
    sendXmlhttpRequest("POST", "id=" + userID, "getbattle", onGetBattleRequestSuccess, false, "");
  }
  count_up_time = count_up_time + 1;
}

function onSuccess(acceleration) {
  countUp();
  if (initial == 0) {
    accX = acceleration.x;
    accY = acceleration.y;
    accZ = acceleration.z;
    initial = 1;
  } else {
    if ((acceleration.x > accX + errorRange) || (acceleration.x < accX - errorRange) || (acceleration.y > accY + errorRange) || (acceleration.y < accY - errorRange) || (acceleration.z > accZ + errorRange) || (acceleration.z < accZ - errorRange)) {
      stopWatch();
      document.getElementById("message").innerHTML = "You Lose!";
      sendXmlhttpRequest("GET", "userid=" + userID + "&battleid=" + battleID, "declaredefeat", false, "");
      navigator.notification.alert("You lost the battle. Better luck next time.", endBattleCallback, "Defeat", "OK");
    }
  }
}

//watch acceleration
function startWatch() {
  var options = { frequency: 1000 };
  watchID = navigator.accelerometer.watchAcceleration(onSuccess, onError, options);
}

function onError() {
  alert('Accelerator error!');
}

function onGetBattleRequestSuccess(serverResponse) {
  // On success, save the returned unique battle ID
  battleID = serverResponse;
  console.log("CHECK FOR LOSER IN BATTLE " + battleID);
  if (battleID.match(/no battle/) == null) {
    stopCount();
    stopWatch();
    navigator.notification.alert("You have won the battle!!", endBattleCallback, "Victory", "OK");
  }
}

function timedCount() {
  document.getElementById("message").innerHTML = "Flip over your phone!  The game will start in: " + (5 - count_down_time);
  count_down_time = count_down_time + 1;
  if (count_down_time < 6) {
    setTimeout("timedCount()", 1000);
  } else {
    startWatch();
  }
}

function startTimer() {
  userID = window.name;
  if (!timer_is_on) {
    timer_is_on = 1; 
    timedCount();
  }
}

window.addEventListener('load', startTimer, false);

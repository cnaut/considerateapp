var userID;
var battleID;
var errorRange = 0.1;

//timer variables
var count_down_time = 0;
var count_up_time = 0;
var battle_timeout;
var timer_is_on = 0;

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

function onSuccess(acceleration) {
  if (initial == 0) {
    console.log("Let the battle begin");
    accX = acceleration.x;
    accY = acceleration.y;
    accZ = acceleration.z;
    valueX.value = "X: " + acceleration.x;
    valueY.value = "Y: " + acceleration.y;
    valueZ.value = "Z: " + acceleration.z;
    initial = 1;
  } else {
    console.log(accX);
    if ((acceleration.x > accX + errorRange) || (acceleration.x < accX - errorRange) || (acceleration.y > accY + errorRange) || (acceleration.y < accY - errorRange) || (acceleration.z > accZ + errorRange) || (acceleration.z < accZ - errorRange)) {
      stopCount();
      stopWatch();
      document.getElementById("accelerometer").innerHTML = "You Lose!";
      sendXmlhttpRequest("GET", "userid=" + userID + "&battleid=" + battleID, "getbattle", onGetBattleRequestSuccess, false, "");
      navigator.notification.alert("You lost the battle. Better luck next time.", endBattleCallback, "Defeat", "OK");
    }
  }
}

//watch acceleration
function startWatch() {
  valueX = document.getElementById("valueX");
  valueY = document.getElementById("valueY");
  valueZ = document.getElementById("valueZ");
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
  if (battleID == null) {
    stopCount();
    stopWatch();
    navigator.notification.alert("You have won the battle!!", endBattleCallback, "Victory", "OK");
  }
}

function countUp() {
  document.getElementById('count_up').innerHTML = count_up_time;
  //Every 5 seconds, check to see if the battle has ended 
  if (count_up_time % 5 == 0) {
    sendXmlhttpRequest("POST", "id=" + userID, "getbattle", onGetBattleRequestSuccess, false, "");
  }
  count_up_time = count_up_time + 1;
  stopCount();
  battle_timeout = setTimeout("countUp()", 1000);
}

function timedCount() {
  document.getElementById('timer').innerHTML = (5 - count_down_time);
  count_down_time = count_down_time + 1;
  if (count_down_time < 6) {
    setTimeout("timedCount()", 1000);
  } else {
    countUp();
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

function stopCount() {
  clearTimeout(battle_timeout);
}

window.addEventListener('load', startTimer, false);

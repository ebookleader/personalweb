var socket = null;
var sessionUser = $('#sessionUser').val();

$(document).ready(function() {
    if (sessionUser) {
        connectSocket();
    }
})

function connectSocket() {
    // console.log("socket connect start");
    var websocket = new SockJS("/ws/comment");
    socket = websocket;

    websocket.onopen = function() { // socket 연결됐을때
        // console.log("socket open");
    };

    websocket.onmessage = function(event) { // 서버에서 메세지 받았을때

        var arr = event.data.split(','); // writer, id, message, date
        // console.log(arr);

        let socketMessage = document.getElementById("socketMessage");
        socketMessage.style.display = 'block';
        socketMessage.style.backgroundColor = "#AED6F1";

        let dropDownBtn = document.getElementById("dropdownMenuButton1");
        dropDownBtn.style.backgroundColor = "#AED6F1";
        document.getElementById("alarmHref").href = arr[1];
        document.getElementById("alarmMessage").innerText = arr[2];
        document.getElementById("alarmDate").innerText = arr[3];

        var alarmCnt = document.getElementById("alarmCount").innerText;
        document.getElementById("alarmCount").innerText = parseInt(alarmCnt) + 1;

        setTimeout(function () {
            dropDownBtn.style.backgroundColor = "#2c3e50";
            socketMessage.style.backgroundColor = "white";
        }, 7000);
    };

    websocket.onclose = function() { // socket 닫힐때
        // console.log("socket close");
    }
}
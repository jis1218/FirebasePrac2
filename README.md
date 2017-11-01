
##### noit를 보낼 때도 기기 아이디를 이용해 보내면 된다.
##### elipsize를 end로 해주면 된다.

##### 각 아이디마다 token을 하나씩 받게 된다.
##### 그리고 지금 로그인 되어 있어야 Notification을 받을 수 있다.
##### FirebaseInstanceId.getInstance()는 현재 로그인 되어 있는 아이디의 Instance를 받아온다.

//Retrofit 스레드 + Httpconnection
        //네트워킹 하는 툴

##### Retrofit은 무엇인가??
##### Retrofit adapts a Java interface to HTTP calls by using annotations on the declared methods to define how requests are made. Create instances using the builder and pass your interface to create(java.lang.Class<T>) to generate an implementation

##### Call Class
##### An invocation of a Retrofit method that sends a request to a webserver and returns a response. Each call yields its own HTTP request and response pair. Use clone() to make multiple calls with the same parameters to the same webserver; this may be used to implement polling or to retry a failed call.
##### Calls may be executed synchronously with execute(), or asynchronously with enqueue(retrofit2.Callback<T>). In either case the call can be canceled at any time with cancel(). A call that is busy writing its request or reading its response may receive a IOException; this is working as designed.

##### Firebase를 이용한 클라우드 메시지 서비스 http 프로토콜
#####앱 서버는 모든 HTTP 요청을 다음 엔드포인트로 연결해야 합니다.
##### https://firebase.google.com/docs/cloud-messaging/http-server-ref

##### to

##### 이 매개변수는 메시지의 수신자를 지정합니다.
##### 이 값은 기기의 등록 토큰, 기기 그룹의 알림 키 또는 단일 주제(/topics/ 프리픽스 포함)가 될 수 있습니다. 여러 주제로 보내려면 condition 매개변수를 사용하세요.

##### registration_ids

##### 이 매개변수는 멀티캐스트 메시지(둘 이상의 등록 토큰으로 전송된 메시지)의 수신자를 지정합니다.
##### 이 값은 멀티캐스트 메시지를 보낼 등록 토큰의 배열이어야 합니다. 배열에 포함될 수 있는 등록 토큰 수는 1~1,000개입니다. 단일 기기로 메시지를 보내려면 to 매개변수를 사용하세요.
##### 멀티캐스트 메시지에는 HTTP JSON 형식만 사용할 수 있습니다.


##### Noti 흐름

##### token값과 msg값을 받는다.
```Java
String token = textView2.getText().toString();
String msg = editMsg.getText().toString();
```

##### body에 넣어 보내줄 String 값을 만들어 token과 msg를 넣어준다.
```Java
String json = "{\"to\":\"" + token + "\", \"msg\":\""+msg + "\"}";
```
##### Retrofit 객체를 만들어준다.
```Java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.138:8090/")
                .build();
```
##### Interface와 결합 후 body를 RequestBody에 넣어준다.
```
IRetro service = retrofit.create(IRetro.class);
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), json);
```

##### Call을 통해 Retrofit 메서드를 호출하여 웹 서버에 요청을 보내고 응답을 반환한다.
```Java
Call<ResponseBody> remote = service.sendNotification(body);
```

##### 서버의 형태
```
var http = require("http");
var httpUrlConnection = require("request") //npm install request로 다운 필요
const fcmServerUrl = "https://fcm.googleapis.com/fcm/send" //앱 서버는 모든 HTTP 요청을 다음 엔드포인트로 연결해야 합니다.
const serverKey = "AAAAasgGxaA:APA91bH6bs0JTEmMAq8FVFjUiJDVjDnCGvcGI8IJvGXl0GwF5QK8dpTPKthYAYmWXXblgJiiP6EncVsakM04X21ipYEIf36UHMtgN4fZ89bMrQDZZbJ2wOUOmiJMC0zsCKcy78tjgnbP"; //프로젝트 고유의 서버키

var msg = {
    to : "",
    notification : {
        title : "메시지 테스트",
        body : ""
    }
}
var server = http.createServer(function(request, response){
    // post 메시지 수신
    if(request.url == "/sendNotification"){ //안드로이드에서 Interface에 @POST annotation으로 sendNotification을 붙여주고 있음
        var postdata = "";
        request.on("data", function(data){
        postdata += data; //JSON 형태로 날라옴
    })

    request.on("end", function(){
        var postObj = JSON.parse(postdata)
        msg.to = postObj.to;
        msg.notification.body = postObj.msg;
        // 메시지를 fcm 서버로 전송
        httpUrlConnection(
            // http 메시지 객체
            {
                url : fcmServerUrl,
                method : "POST",
                headers : {
                    "Authorization" : "key=" + serverKey,
                    "Content-Type" : "application/json"
                },
                body : JSON.stringify(msg)

            },
            // 콜백함수
            function(error, response2, body){
                var result = {
                    code : response2.statusCode,
                    msg : body
                }
                response.writeHead(200, {"Content-Type":"plain/text"});
                response.end(JSON.stringify(result))
            }
        )
    })
}else{
    response.end("404 page not found");
}
});

server.listen(8090, function(){
    console.log("server is running")
});
```

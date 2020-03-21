# javalinist
Trying out javalin. Running demo at http://javalinist.isgoodness.com/

# Sse
Go to the url above and run this code in the console. Try to create, update or delete operation bellow to see.

```javascript
var evtSource = new EventSource("/sse/users");
evtSource.addEventListener("CREATED", function(e) {
    var data = JSON.parse(e.data);
    console.log("CREATED", data)
}); evtSource.addEventListener("UPDATED", function(e) {
    var data = JSON.parse(e.data);
    console.log("UPDATED", data)
});evtSource.addEventListener("DELETED", function(e) {
    var data = JSON.parse(e.data);
    console.log("DELETED", data)
})
```
# Operation on /users
## Create user
```bash
curl -X POST 'http://javalinist.isgoodness.com/users?name=Javalin'
```
## Update user
```bash
curl -X PATCH 'http://javalinist.isgoodness.com/users/{userId}?name=Javalinist'
```
## Delete user
```bash
curl -X DELETE 'http://javalinist.isgoodness.com/users/{userId}
```
## List all
```bash
curl -X GET 'http://javalinist.isgoodness.com/users
```

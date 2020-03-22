# javalinist
Trying out javalin. Running demo at http://javalinist.isgoodness.com/

# Server-sent events, sse
Go to http://javalinist.isgoodness.com/sse and try to create, update or delete operation bellow to see.

# Operation on /users
## Create user
```bash
curl -X POST 'http://javalinist.isgoodness.com/users?name=Javalin'
```
## Get user
```bash
curl -X GET 'http://javalinist.isgoodness.com/users/{userId}'
```
## Update user
```bash
curl -X PATCH 'http://javalinist.isgoodness.com/users/{userId}?name=Javalinist'
```
## Delete user
```bash
curl -X DELETE 'http://javalinist.isgoodness.com/users/{userId}'
```
## List all
```bash
curl -X GET 'http://javalinist.isgoodness.com/users'
```

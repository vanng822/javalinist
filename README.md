# javalinist
Trying out javalin. Running demo at http://javalinist.isgoodness.com/

# Server-sent events, sse
Go [here](/sse) and try to create, update or delete operation bellow to see.

# Try with swagger ui

[Go to swagger ui](/swagger)

# Operation on /users using curl

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
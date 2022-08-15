# Meetups

API REST to create meetups and calculate the number of beer boxes needed to supply them.

## Built on 🛠

* [Springboot](https://start.spring.io/)
* [Maven](https://maven.apache.org/)
* [Swagger.io](https://editor.swagger.io/) 
* [Mysql](https://www.mysql.com/)

## Starting 🚀

### Requirements 📋

Tools needed to run the service locally:

- JDK 1.8+.
- MySQL 5.6+.
- Maven 3.2+.

### Installing 🔧

- Set database's user and password in the file [application.properties](https://github.com/narumayase/meetup/blob/main/src/main/resources/application.properties).
- Run the file [query.sql](https://github.com/narumayase/meetup/blob/main/query.sql) to create the database, tables and some test users. 
- Run:
```
$mvn spring-boot:run
```
This will compile and start the service locally. 

It is necessary to login to use the endpoints. Please check the docs on [Swagger](https://github.com/narumayase/meetup/blob/main/swagger.yaml) or [here](https://github.com/narumayase/meetup/tree/main#how-to-use).

## Unit tests
```
$mvn test
```
The main use cases requested in the requirements are tested:

- Get weather details.
- Get number of beer boxex per meetup.
- Create a meetup with guests.
- Add an user to a meetup.
- Checkin of an user to a meetup.

## Role scheme 📋
1) Users with roll "admin" can create all type of users.
Default user when starting the app:
    - user: juan.padilla
    - pass: myPass123
3) Users with role "user" can't create users. Default password of any generated user is "password123".

## How to use

To see details of the status codes of each endpoint see swagger.yaml.

```
200 - Success.
400 - Incorrect date format, past date or the date exceeds 15 days. Date format: yyyy-MM-dd HH:mm:ss. Example: 2020-12-10 22:00:11.
401 - Unknown or wrong token.
403 - User rol forbidden. wrong username or password.
404 - Not found meetup or user.
500 - Unexpected error or there was an error calling weather client.
```

Here are some examples of how to run a full flow.

1 - Login

```
POST http://localhost:8080/user/login

request:
{
    "username":"juan.padilla",
    "password":"myPass123"
}

response:
200
{
    "token": "f581127f-ea3c-445b-93e9-d97ab56b932f"
}
```
This token is the one that will be used in the rest of the endpoints to appear as admin or user.

2 - Weather query for a specific date to find out how many beer boxes to buy (this does not create a meetup), this can only be done by users with the admin role:
```
GET http://localhost:8080/meetup/beers?date=2020-12-11 22:00:11&guests=25

header: 
"Authorization" : "f581127f-ea3c-445b-93e9-d97ab56b932f"

response:
200
{
    "boxes": 8,
    "temperature": 25.4,
    "date": "2020-12-11 22:00:11",
    "guestsAmount": 25
}
```
Returns the temperature and the number of beer boxes to supply the meetup.

3 - Check the weather for a specific date, this can be done by any user:

```
GET http://localhost:8080/weather?date=2020-12-13 22:22:22

header: 
"Authorization" : "f581127f-ea3c-445b-93e9-d97ab56b932f"

response:
200
{
    "temperature": 25.4
}
```

4 - Create a meetup on a specific date with specific invitees (users). This can be done by users with the admin role.

```
POST http://localhost:8080/meetup

header: 
"Authorization" : "f581127f-ea3c-445b-93e9-d97ab56b932f"

request:

{
    "description":"let's get some beers",
    "date":"2020-12-11 22:00:11",
    "guests":[
        {"username":"juan.padilla"},
        {"username":"rodrigo.muñoz"},
        {"username":"santiago.sata"},
        {"username":"romina.matias"},
        {"username":"maria.perez"},
        {"username":"lucia.vazquez"},
        {"username":"celeste.cid"},
        {"username":"lion.li"},
        {"username":"griselda.morina"}
    ]
}

response:
200
```

5 - An user can be added to a meetup, it is necessary to know the id of the meetup:

```
PATCH http://localhost:8080/meetup

header: 
"Authorization" : "4d34f923-4713-4abd-a319-6ba1b4a23d9f"

request:
{
    "id":35
}

response:
200
```

6 - A user can confirm that he attended a meetup, it is necessary to know the id of the meetup:

```
PATCH http://localhost:8080/meetup/checkin

header: 
"Authorization" : "4d34f923-4713-4abd-a319-6ba1b4a23d9f"

request:
{
    "id":35
}

response:
200
```

7 - Finally, you can check the meetups and users:

- meetups
```
GET http://localhost:8080/meetup

header: 
"Authorization" : "4d34f923-4713-4abd-a319-6ba1b4a23d9f"

response:
200
[
    {
        "id": 35,
        "boxes": 3,
        "temperature": 25.4,
        "description": "let's get some beers",
        "date": "2020-12-11 04:31:43.0",
        "guests": [
            {
                "username": "juan.padilla",
                "checkin": false
            },
            {
                "username": "rodrigo.muñoz",
                "checkin": false
            },
            {
                "username": "santiago.sata",
                "checkin": false
            },
            {
                "username": "romina.matias",
                "checkin": false
            },
            {
                "username": "maria.perez",
                "checkin": false
            },
            {
                "username": "lucia.vazquez",
                "checkin": false
            },
            {
                "username": "celeste.cid",
                "checkin": false
            },
            {
                "username": "lion.li",
                "checkin": false
            },
            {
                "username": "griselda.morina",
                "checkin": false
            },
            {
                "username": "rodrigo.muñoz",
                "checkin": false
            },
            {
                "username": "root",
                "checkin": true
            }
        ]
    }
]
```
- users
```
GET http://localhost:8080/user

header: 
"Authorization" : "4d34f923-4713-4abd-a319-6ba1b4a23d9f"

response:
200
[
    {
        "username": "root",
        "role": "admin"
    },
    {
        "username": "rodrigo.muñoz",
        "role": "user"
    },
    {
        "username": "santiago.sata",
        "role": "user"
    },
    {
        "username": "romina.matias",
        "role": "user"
    },
    {
        "username": "maria.perez",
        "role": "user"
    },
    {
        "username": "lucia.vazquez",
        "role": "user"
    },
    {
        "username": "celeste.cid",
        "role": "user"
    },
    {
        "username": "lion.li",
        "role": "user"
    },
    {
        "username": "griselda.morina",
        "role": "user"
    },
    {
        "username": "juan.padilla",
        "role": "admin"
    },
    {
        "username": "rodrigo.muñoz",
        "role": "user"
    }
]

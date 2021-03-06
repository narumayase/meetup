# Meetups

API Rest para crear meetups y calcular la cantidad de cajas de cerveza necesarias para aprovisionarlas.

## Construido con 馃洜

* [Springboot](https://start.spring.io/) - El framework usado para generar el proyecto base.
* [Maven](https://maven.apache.org/) - Manejador de dependencias.
* [Swagger.io](https://editor.swagger.io/) - Usado para documentar la API.
* [Mysql](https://www.mysql.com/) - Motor de base de datos y herramientas para usarla.

## Comenzando 馃殌

### Pre-requisitos 馃搵

Herramientas necesarias para la ejecuci贸n local del servicio:

- JDK 1.8+.
- MySQL 5.6+.
- Maven 3.2+.

### Instalaci贸n 馃敡

- Configurar usuario y contrase帽a de la base de datos en el archivo /src/resources/application.properties.
- Ejecutar el archivo query.sql para crear la base de datos, las tablas y algunos usuarios de prueba. 
- Ejecutar:
```
$mvn spring-boot:run
```
Esto compila e inicia el servicio localmente.

Luego ya se puede empezar a usar la API. Es necesario iniciar sesi贸n para poder usar los endpoints. Ante cualquier duda, la API est谩 documentada en el archivo swagger.yaml. Igualmente en la 煤ltima secci贸n de este readme se encuentran los pasos para probar un camino feliz.

## Ejecuci贸n de pruebas unitarias 
```
$mvn test
```
Se testean los casos de uso principales solicitados en los requerimientos:

- Consulta de clima.
- Consulta de cantidad de cajas de cerveza por meetup.
- Creaci贸n de meetup con personas invitadas.
- Adhesi贸n de un usuario a una meetup.
- Checkin de un usuario a una meetup.

## Esquema de roles 馃搵
1) Los usuarios con rol "admin" pueden crear todo tipo de usuarios. 
Usuario por defecto al iniciar la app:
    - usuario: juan.padilla
    - contrase帽a: myPass123
3) Los usuarios con rol "user" no pueden crear usuarios. Las contrase帽as por defecto de todos los usuarios autogenerados es "password123".

## Roadmap (pendientes)

Estas son todas las cosas que quedan en el backlog para mejoras futuras:

- Terminar tests.
- Notificaciones.
- Usar otro m茅todo de hasheo que no sea MD5.
- Securizaci贸n con https.
- Mejor manejo de errores y de excepciones.
- Logging para monitoreo de la API.
- Vencimiento de token de sesi贸n y/o logout.
- Dockerizaci贸n.

## C贸mo usar la API

Para ver en detalle los diferentes c贸digos de error que puede devolver el servicio en cada endpoint ver el archivo swagger.yaml.

```
200 - Llamada resuelta correctamente.
400 - El formato de la fecha es incorrecto, la fecha es del pasado 贸 la fecha supera los 15 d铆as. Formato de la fecha: yyyy-MM-dd HH:mm:ss. Ejemplo: 2020-12-10 22:00:11.
401 - Token incorrecto o inexistente.
403 - Usuario con rol no permitido. Usuario o contrase帽a incorrectos.
404 - Meetup o usuario no encontrados.
500 - Error inesperado u ocurri贸 un error al obtener el clima consultando al cliente.
```

A continuaci贸n unos ejemplos de c贸mo ejecutar un flujo completo. 

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
Este token es el que se utilizar谩 en el resto de los endpoints para apersonarse como admin o user.

2 - Consulta de clima de una fecha espec铆fica para saber cu谩ntas cajas de cerveza comprar (esto no crea una meetup), esto solo lo pueden hacer usuarios con rol admin:

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
Devuelve la temperatura y la cantidad de cajas de cervezas para aprovisionar la meetup.

3 - Consulta del clima de una fecha espec铆fica, esto lo puede hacer cualquier usuario:

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

4 - Crear una meetup en una fecha espec铆fica con invitados espec铆ficos (usuarios). Esto lo pueden hacer usuarios con rol admin.

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
        {"username":"rodrigo.mu帽oz"},
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

5 - Un usuario puede agregarse a una meetup, es necesario conocer el id de la meetup:

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

6 - Un usuario puede confirmar que asisti贸 a una meetup, es necesario conocer el id de la meetup:

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

7 - Por 煤ltimo, se pueden consultar las meetups y los usuarios:

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
                "username": "rodrigo.mu帽oz",
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
                "username": "rodrigo.mu帽oz",
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
        "username": "rodrigo.mu帽oz",
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
        "username": "rodrigo.mu帽oz",
        "role": "user"
    }
]
```
## Autor 馃搵
```
Ayel茅n Baglietto
ayelenbaglietto@gmail.com
```
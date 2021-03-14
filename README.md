# curso06entregable01

Proyecto 1

Descripción: Crear una framework de pruebas utilizando Java + Maven + testNG que permita ejecutar pruebas a un servicio rest. (api testing).

Evaluación: 70%
Detalles:

● Utilizar el framework desarrollado en clases como base para el examen: https://github.com/coffeestainio/restassured-boilerplate
● Utilizar la aplicación de prueba provista por el profesor para ejecutar las pruebas: https://api-coffee-testing.herokuapp.com/
● Autenticación básica: testuser/testpass
● Dependencia entre posts y comments: Los comentarios se relacionan a un post. Por ejemplo, el post 1, puede tener comentarios 1 y 2; y solamente son accesibles si se utiliza el post correspondiente.

Instrucciones:

1. Dado los siguientes endpoints:

v1.POST("/post", TokenAuthMiddleware(), post.Create)
v1.GET("/posts", TokenAuthMiddleware(), post.All)
v1.GET("/post/:id", TokenAuthMiddleware(), post.One)
v1.PUT("/post/:id", TokenAuthMiddleware(), post.Update)
v1.DELETE("/post/:id", TokenAuthMiddleware(), post.Delete)

v1.POST("/comment/:postid", basicAuth(), comment.Create)
v1.GET("/comments/:postid", basicAuth(), comment.All)
v1.GET("/comment/:postid/:id", basicAuth(), comment.One)
v1.PUT("/comment/:postid/:id", basicAuth(), comment.Update)
v1.DELETE("/comment/:postid/:id", basicAuth(), comment.Delete)

2. Que reciben los siguientes body | payload.

a. Para post:
{
“title”:”some_title”,
“content”:”some_content”
}

b. Para comment:
{
“name”:”some_name”,
“comment”:”some_ccomment”
}

3. Validar (Para cada endpoint/ruta):

a. Al menos una respuesta positiva
b. Al menos una respuesta negativa
c. La Seguridad

Método de evaluación: Cambiar uno de los endpoints en el sistema de prueba y al menos un test debería de fallar.

Entregable:

1. Crear un repositorio para el curso:
2. Crear una carpeta que se llame: /api_test/
3. Agregar el código del proyecto dentro de tal forma que:
4. Al correr `mvn clean test` se ejecutan los tests

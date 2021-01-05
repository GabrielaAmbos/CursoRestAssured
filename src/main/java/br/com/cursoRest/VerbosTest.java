package br.com.cursoRest;

import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class VerbosTest {

    @Test
    public void deveSalvarUsuario() {
        given()
                    .log().all()
                    .contentType("application/json")
                    .body("{ \"name\": \"Gabriela\", \"age\": 22 }")
                .when()
                    .post("http://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Gabriela"))
                    .body("age", is(22));
    }

    @Test
    public void naoDeveSalvarUsuarioSemNome() {
        given()
                    .log().all()
                    .contentType("application/json")
                    .body("{ \"age\": 22 }")
                .when()
                    .post("http://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("id", is(nullValue()))
                    .body("error", is("Name é um atributo obrigatório"));
    }

    @Test
    public void deveSalvarUsuarioViaXML() {
        given()
                .log().all()
                .contentType(ContentType.XML)
                .body("  <user>\n" +
                        "    <name>Gabriela</name>\n" +
                        "    <age>22</age>\n" +
                        "</user>")
                .when()
                .post("http://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("Gabriela"))
                .body("user.age", is("22"));
    }

    @Test
    public void deveAlterarUsuario() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{ \"name\": \"Usuario alterado\", \"age\": 22 }")
                .when()
                .put("http://restapi.wcaquino.me/users/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(notNullValue()))
                .body("name", is("Usuario alterado"))
                .body("age", is(22));
    }

    @Test
    public void deveCustomizarURL() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{ \"name\": \"Usuario alterado\", \"age\": 22 }")
                .when()
                .put("http://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(notNullValue()))
                .body("name", is("Usuario alterado"))
                .body("age", is(22));
    }

    @Test
    public void deveCustomizarURLParte2() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{ \"name\": \"Usuario alterado\", \"age\": 22 }")
                .pathParam("entidade", "users")
                .pathParam("userId", "1")
                .when()
                .put("http://restapi.wcaquino.me/{entidade}/{userId}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(notNullValue()))
                .body("name", is("Usuario alterado"))
                .body("age", is(22));
    }

    @Test
    public void deveRemoverUsuario() {
        given()
                .log().all()
                .when()
                    .delete("http://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(204);
    }

    @Test
    public void deveRemoverUsuarioInexistente() {
        given()
                .log().all()
                .when()
                .delete("http://restapi.wcaquino.me/users/1000")
                .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Registro inexistente"));
    }
}



package br.com.cursoRest;

import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserJsonTest {

    @Test
    public void deveVerificarPrimeiroNivel() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/1")
                .then()
                .statusCode(200)
        .body("id", is(1))
        .body("name", containsString("Silva"))
        .body("age", greaterThan(18));
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas() {
        Response response = request(Method.GET, "http://restapi.wcaquino.me/users/1");

        //path
        assertEquals(new Integer(1), response.path("id"));
        assertEquals(new Integer(1), response.path("%s","id"));

        //JsonPath
        JsonPath jpath = new JsonPath(response.asString());
        assertEquals(1, jpath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1, id);
    }

    @Test
    public void deveVerificarSegundoNivel() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/2")
                .then()
                .statusCode(200)
        .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    public void deveVerificarLista() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/3")
                .then()
                .statusCode(200)
                .body("filhos", hasSize(2))
        .body("filhos[0].name", is("Zezinho"))
        .body("filhos[1].name", is("Luizinho"))
        .body("filhos.name", hasItems("Zezinho", "Luizinho"));
    }

    @Test
    public void deveRetornarErroUsuarioInexistente() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/4")
                .then()
                .statusCode(404)
        .body("error", is("Usuário inexistente"));
    }

    @Test
    public void deveVerificarListaRaiz() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
        .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
        .body("age[1]", is(25))
        .body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
        .body("salary", contains(1234.5678f, 2500, null));
    }

    @Test
    public void devoFazerVerificacoesAvancadas() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("age.findAll{it <= 25}.size()", is(2))
                .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
                .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
                .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
                .body("find{it.age <= 25}.name", is("Maria Joaquina"))
                .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
                .body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
                .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"));
    }

    @Test
    public void devoUnirJsonPathComJava() {
        ArrayList<String> names =
        given()
                .when()
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}");
        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.get(0).equalsIgnoreCase("Maria Joaquina"));
    }
}

package br.com.cursoRest;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserXMLTest {
    @Test
    public void devoTrabalharComXML() {
        given()
                .when()
                    .get("http://restapi.wcaquino.me/usersXML/3")
                .then()
                    .statusCode(200)
                    .rootPath("user") //adiciona a rota
                    .body("name", is("Ana Julia"))
                    .body("@id", is("3"))

                    .rootPath("user.filhos")
                    .body("name.size()", is(2))
                    .body("name[0]", is("Zezinho"))

                    .detachRootPath("filhos") //retira um caminho da rota
                    .body("filhos.name[1]", is("Luizinho"))
                    .body("filhos.name", hasItem("Zezinho"))

                    .appendRootPath("filhos") //adiciona um caminho na rota
                    .body("name", hasItems("Zezinho","Luizinho"));
    }

    @Test
    public void devoFazerPesquisasAvancadasComXML() {
        given()
                .when()
                    .get("http://restapi.wcaquino.me/usersXML/")
                .then()
                    .statusCode(200)
                    .body("users.user.size()", is(3))
                    .body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
                    .body("users.user.@id", hasItems("1", "2", "3"));
    }

    @Test
    public void devoFazerPesquisasAvancadasComXMLEJava() {
        String nome = given()
                .when()
                    .get("http://restapi.wcaquino.me/usersXML/")
                .then()
                    .statusCode(200)
                    .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nome.toUpperCase());
    }

    @Test
    public void devoFazerPesquisasAvancadasComXPath() {
        given()
                .when()
                    .get("http://restapi.wcaquino.me/usersXML/")
                .then()
                    .statusCode(200)
                    .body(hasXPath("count(/users/user)", is("3")))
                    .body(hasXPath("/users/user[@id = '1']"))
                    .body(hasXPath("//user[@id = '2']"))
                    .body(hasXPath("//name[text() = 'Zezinho']/../../name", is("Ana Julia")))
                    .body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))
                    .body(hasXPath("//name", is("João da Silva")));

    }
}

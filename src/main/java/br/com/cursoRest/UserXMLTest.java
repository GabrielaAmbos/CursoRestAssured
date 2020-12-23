package br.com.cursoRest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserXMLTest {
    @BeforeClass
    public static void setup() {
        baseURI = "http://restapi.wcaquino.me";
        //  port = 80;
        //  basePath = "/v2";
    }

    @Test
    public void devoTrabalharComXML() {
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.log(LogDetail.ALL);
        RequestSpecification build = reqBuilder.build();

        given()
                .log().all()
                .when()
                    .get("/usersXML/3")
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
                    .get("/usersXML/")
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
                    .get("/usersXML/")
                .then()
                    .statusCode(200)
                    .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nome.toUpperCase());
    }

    @Test
    public void devoFazerPesquisasAvancadasComXPath() {
        given()
                .when()
                    .get("/usersXML/")
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

package br.com.cursoRest;

import io.restassured.http.Method;
import io.restassured.response.*;
import org.hamcrest.Matchers;
import org.junit.Test;
import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class HelloWorldTest {
    @Test
    public void testHelloWorld() {
        Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");

        assertTrue((response.getBody().asString().equals("Ola Mundo!")));
        assertTrue((response.statusCode() == 200));
        assertTrue("O status code deveria ser 200",(response.statusCode() == 200));
        assertEquals(200, response.statusCode());

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured() {
        Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        get("http://restapi.wcaquino.me/ola").then().statusCode(200);

        //BDD
        given()    //Pré condições
        .when()    //Ação
                .get("http://restapi.wcaquino.me/ola")
        .then()    //Resultado esperado == Assertivas
                .statusCode(200);
    }

    @Test
    public void devoConhecerMatcherHamcrests() {
        assertThat("Maria", Matchers.is("Maria"));
        assertThat(128, Matchers.is(128));
        assertThat(128, Matchers.isA(Integer.class));

        List<Integer> impares = Arrays.asList(1, 2, 3, 4, 5);
        assertThat(impares, hasSize(5));
        assertThat(impares, contains(1,2,3,4,5));
        assertThat(impares, hasItem(1));

        assertThat("Maria", is(not("João")));
        assertThat("Maria", not("João"));
    }

    @Test
    public void devoValidarOBody() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/ola")
                .then()
                .statusCode(200)
                .body(is("Ola Mundo!"))
        .body(containsString("Mundo"))
        .body(is(not(nullValue())));
    }
}

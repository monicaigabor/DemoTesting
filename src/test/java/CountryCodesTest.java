import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;


import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.core.IsEqual.equalTo;

public class CountryCodesTest {

    @BeforeClass
    public void setupEnvironment(){
        RestAssured.baseURI = "http://restcountries.eu";
        RestAssured.basePath = "/rest/v2";
    }

    @Test
    public void inexistingCountryCode(){
        given()
                   .log().all().
                when()
                  .get("/alpha/abc").
                then()
                  .log().all()
                  .statusCode(404)
                  .body("status", equalTo(404))
                  .body("message", equalTo("Not Found"));
    }

    @Test
    public void invalidCountryCodeLength(){
        given()
                .log().all().
                when()
                   .get("/alpha/1234").
                then()
                   .log().all()
                   .statusCode(400)
                   .body("status", equalTo(400))
                   .body("message", equalTo("Bad Request"));
    }

    // Success test
    @Test
    public void getAllCountryCode(){
        given()
                .log().all().
                when()
                .get("/alpha/DE").
                then()
                .log().all()
                .statusCode(200);
    }


    // Failing test - Expecting DE
    @Test
    public void checkValidCountryCode(){
        given()
                .log().all().
                when()
                .get("/alpha?codes=DE").
                then()
                .log().all()
                .statusCode(200)
                .body("[0].name", equalTo("Germany"));
    }

    // Success test - Expecting DE
    @Test
    public void checkValidCountryCodeData(){
        given()
                .log().all().
                when()
                .get("/alpha?codes=DE").
                then()
                .log().all()
                .statusCode(200)
                .body("[0].name", equalTo("Romania"));
    }

    // Check the TLD returned in a list
    @Test
    public void checkValidCountryCodesBody() {

        List list = new ArrayList();
        list.add(".de");

        given().
                contentType("application/x-www-form-urlencoded").
                log().all().
                when().
                get("alpha?codes=DE").
                then().
                log().all().
                statusCode(200).
                body("[0].topLevelDomain", Matchers.equalTo(list));
    }

    // Check the Borders returned in a list
    @Test
    public void checkValidCountryCodesBodyBorders() {

        List list = new ArrayList();
        list.add("AUT");
        list.add("BEL");
        list.add("CZE");
        list.add("DNK");
        list.add("FRA");
        list.add("LUX");
        list.add("NLD");
        list.add("POL");
        list.add("CHE");
        list.add("RO");

        given().
                contentType("application/x-www-form-urlencoded").
                log().all().
                when().
                get("alpha?codes=DE").
                then().
                log().all().
                statusCode(200).
                body("[0].borders", Matchers.equalTo(list));
    }

    @DataProvider
    public Object[][] countryCodesList(){
        return new Object[][]{
          {"RO", "Romania"},
          {"IT", "Italy"},
          {"AT", "Austria"}
        };
    }

    @Test(dataProvider = "countryCodesList")
    public void checkValidCountryCodes(String countryCode, String countryName) {
        given().
          contentType("application/x-www-form-urlencoded").
          log().all().
          when().
          get("alpha?codes=" + countryCode).
          then().
          log().all().
          statusCode(200).
          body("[0].name", Matchers.equalTo(countryName));
    }
}

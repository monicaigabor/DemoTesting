import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.core.IsEqual.equalTo;

public class CountryCodesTest {

    @BeforeClass
    public void setupEnvironment(){
        RestAssured.baseURI = "http://restcountries.eu";
        RestAssured.basePath = "/rest/v2";
    }

    @DataProvider
    public Object[][] countryCodesList(){
        return new Object[][]{
          {"RO", "Romania"},
          {"IT", "Italy"},
          {"AT", "Austria"}
        };
    }

    @Test
    public void checkValidCountryCode(){
        given()
            .log().all().
        when()
            .get("/alpha?codes=RO").
        then()
            .log().all()
            .statusCode(200)
            .body("[0].name", equalTo("Romania"));
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

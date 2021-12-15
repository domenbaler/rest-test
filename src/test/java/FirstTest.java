import org.testng.annotations.Test;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class FirstTest{

    @Test
    public void test_get() {
        given().
                when().
                get("http://localhost:8080/rest").
                then().
                assertThat().
                    statusCode(200).
                and().
                    contentType(ContentType.JSON).
                and().
                    body("NumberOfSuccessfulCalls",equalTo("0")).
                and().
                    body("NumberOfUnsuccessfulCalls", equalTo("0"));
    }

    @Test(dependsOnMethods="test_get")
    public void test_valid_param() {
        given().
                when().
                post("http://localhost:8080/rest/4").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void test_invalid_param() {
        given().
                when().
                post("http://localhost:8080/rest/5").
                then().
                assertThat().
                statusCode(400);
    }

}

package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.AddSectionRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.http.MediaType;

public class LineApi {
    private LineApi() {
    }

    public static ExtractableResponse<Response> create(LineRequest lineRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        return response;
    }

    public static ExtractableResponse<Response> findAll() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> find(String location) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(location)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> update(String location, LineUpdateRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(location)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String location) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> addSection(String linelocation, AddSectionRequest body) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(linelocation + "/sections")
                .then().log().all()
                .extract();

        return response;
    }

    public static ExtractableResponse<Response> removeSection(String linelocation, Long stationId) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .param("stationId", stationId)
                .when().delete(linelocation + "/sections")
                .then().log().all()
                .extract();

        return response;
    }
}

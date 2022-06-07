package nextstep.subway.station;

import static nextstep.subway.utils.RequestParamUtils.generateRequestParam;
import static nextstep.subway.utils.RestAssuredUtils.delete;
import static nextstep.subway.utils.RestAssuredUtils.get;
import static nextstep.subway.utils.RestAssuredUtils.post;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    public static final String BASE_URL = "/stations";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames =
            RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고 When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all();

        // when
        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // Given
        final String stationNameProperty = "name";
        Map<String, String> firstStationRequestParams = generateRequestParam(stationNameProperty, "논현역");
        Response generateFirstStationsResponse = post(BASE_URL, firstStationRequestParams).extract().response();
        assertThat(generateFirstStationsResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Map<String, String> secondStationRequestParams = generateRequestParam(stationNameProperty, "신논현역");
        Response generateSecondStationsResponse = post(BASE_URL, secondStationRequestParams).extract().response();
        assertThat(generateSecondStationsResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // When
        Response getAllStationsResponse = get(BASE_URL).extract().response();

        // Then
        assertThat(getAllStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stationNames = getAllStationsResponse.jsonPath().getList("name", String.class);
        assertThat(stationNames)
            .hasSize(2)
            .as("지하철역 목록 조회 응답에 생성한 두개의 지하철역 이름 포함 여부 검증")
            .contains(firstStationRequestParams.get(stationNameProperty), secondStationRequestParams.get(stationNameProperty));
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // Given
        final String stationNameProperty = "name";
        Map<String, String> requestParam = generateRequestParam(stationNameProperty, "선릉역");
        Response generateStationResponse = post(BASE_URL, requestParam).extract().response();
        assertThat(generateStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        final String stationId = generateStationResponse.body().jsonPath().getString("id");
        final String urlTemplate = String.format(BASE_URL.concat("/%s"), stationId);

        // When
        Response deleteResponse = delete(urlTemplate).extract().response();

        // Then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        Response getAllStationsResponse = get(BASE_URL).extract().response();
        assertThat(getAllStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stationNames = getAllStationsResponse.jsonPath().getList(stationNameProperty, String.class);
        assertThat(stationNames)
            .as("지하철역 목록 조회 응답에 삭제한 지하철역 미포함 여부 검증")
            .doesNotContain(requestParam.get(stationNameProperty));
    }
}

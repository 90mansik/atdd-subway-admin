package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;
    ExtractableResponse<Response> lineCreateResponse;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        Long upStationId = StationAcceptanceTest.createOneStation("강남역").jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.createOneStation("광교중앙역").jsonPath().getLong("id");
        lineCreateResponse = createOneLine("신분당선", "red", upStationId, downStationId, 10);
    }

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // BeforeEach 에서 실행

        // then
        assertThat(lineCreateResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> response = getAllLines();
        assertThat(response.jsonPath().getList("name", String.class)).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long upStationIdOfBundang = StationAcceptanceTest.createOneStation("수원역").jsonPath().getLong("id");
        Long downStationIdOfBundang = StationAcceptanceTest.createOneStation("압구정로데오").jsonPath().getLong("id");
        createOneLine("분당선", "yellow", upStationIdOfBundang, downStationIdOfBundang, 10);

        // when
        ExtractableResponse<Response> response = getAllLines();

        // then
        assertThat(response.jsonPath().getList("$", LineResponse.class)).hasSize(2);
        assertThat(response.jsonPath().getList("name", String.class)).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // BeforeEach 에서 실행

        // when
        LineResponse lineResponse = getOneLine(lineCreateResponse.jsonPath().getLong("id")).jsonPath()
                .getObject("$", LineResponse.class);

        // then
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // BeforeEach 에서 실행

        // when
        updateOneLine(lineCreateResponse.jsonPath().getLong("id"), "짱비싼선", "black");

        // then
        LineResponse lineResponse = getOneLine(lineCreateResponse.jsonPath().getLong("id")).jsonPath()
                .getObject("$", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("짱비싼선");
        assertThat(lineResponse.getColor()).isEqualTo("black");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        // BeforeEach에서 실행

        // when
        deleteOneLine(lineCreateResponse.jsonPath().getLong("id"));

        // then
        ExtractableResponse<Response> response = getAllLines();
        assertThat(response.jsonPath().getList("name", String.class)).doesNotContain("신분당선");
    }

    public static ExtractableResponse<Response> createOneLine(String lineName, String lineColor, Long upStationId,
                                                              Long downStationId, Integer distance) {
        LineRequest request = new LineRequest(lineName, lineColor, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateOneLine(Long id, String lineName, String lineColor) {
        LineRequest request = new LineRequest(lineName, lineColor, null, null, null);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteOneLine(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getOneLine(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getAllLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/")
                .then().log().all()
                .extract();
    }
}

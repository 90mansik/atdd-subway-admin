package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @LocalServerPort
    int port;

    private int upLastStationId;
    private int downLastStationId;
    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        // db 초기화
        databaseCleanup.execute();

        createStations();
    }

    private void createStations() {
        upLastStationId = 지하철역을_생성한다("강남역").jsonPath().get("id");
        downLastStationId = 지하철역을_생성한다("역삼역").jsonPath().get("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        //given
        노선_한개_생성한다();

        //when
        List<String> allLineNames = 모든_노선_이름을_조회한다();

        //then
        노선_이름이_조회된다(allLineNames, "2호선");
    }



    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void findLines() {
        //given
        노선_여러개_생성();

        //when
        ExtractableResponse<Response> allLines = 모든_노선을_조회한다();

        //then
        노선의_수가_일치한다(allLines, 2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철노선 조회")
    @Test
    void findLine() {
        // given
        ExtractableResponse<Response> savedLine = 노선_한개_생성한다();

        // when
        ExtractableResponse<Response> result = 특정_노선을_조회한다(savedLine.jsonPath().getInt("id"));

        // then
        지하철_노선_정보_확인(savedLine, result);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void modifyLine() {
        // given
        ExtractableResponse<Response> savedLine = 노선_한개_생성한다();
        int id = savedLine.jsonPath().get("id");

        // when
        ExtractableResponse<Response> result = 노선_정보를_수정한다(id);

        // then
        지하철_노선_정보_수정_확인(id, result);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> savedLine = 노선_한개_생성한다();
        int id = savedLine.jsonPath().get("id");

        // when
        특정_노선을_제거한다(id);

        // then
        해당_노선의_정보가_삭제된다(id);
    }

    private void 해당_노선의_정보가_삭제된다(int id) {
        ExtractableResponse<Response> response = 특정_노선을_조회한다(id);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    private void 지하철_노선_정보_수정_확인(int id, ExtractableResponse<Response> result) {
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> savedLine = 특정_노선을_조회한다(id);

        assertAll(
                ()-> assertThat(savedLine.jsonPath().getString("name")).isEqualTo("새로운 노선"),
                ()-> assertThat(savedLine.jsonPath().getString("color")).isEqualTo("파란색"),
                ()-> assertThat(savedLine.jsonPath().getInt("distance")).isEqualTo(33)
        );
    }

    private ExtractableResponse<Response> 노선_정보를_수정한다(int id) {
        LineRequest updateRequest = LineRequest.builder()
                .name("새로운 노선")
                .color("파란색")
                .distance(33)
                .build();

        return RestAssured.given()
                .body(updateRequest).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/line/" + id)
                .then()
                .log().all()
                .extract();
    }


    private ExtractableResponse<Response> 노선_한개_생성한다() {
        LineRequest request = LineRequest.builder()
                .name("2호선")
                .color("red darken-2")
                .distance(100)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();

        return 노선을_생성한다(request);
    }
    public ExtractableResponse<Response> 노선을_생성한다(LineRequest request) {
        return RestAssured.given()
                .body(request).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private List<String> 모든_노선_이름을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private void 노선_이름이_조회된다(List<String> allLineNames, String lineName) {
        assertThat(allLineNames).containsAnyOf(lineName);
    }

    private void 노선_여러개_생성() {
        LineRequest request1 = LineRequest.builder()
                .name("2호선")
                .color("red darken-2")
                .distance(100)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();
        노선을_생성한다(request1);

        LineRequest request2 = LineRequest.builder()
                .name("분당선")
                .color("yellow darken-1")
                .distance(20)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();
        노선을_생성한다(request2);
    }


    private ExtractableResponse<Response> 모든_노선을_조회한다() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private void 노선의_수가_일치한다(ExtractableResponse<Response> allLines, int size) {
        assertThat(allLines.jsonPath().getList("name", String.class)).hasSize(size);
    }

    private ExtractableResponse<Response> 특정_노선을_조회한다(int id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/line/" + id)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_정보_확인(ExtractableResponse<Response> savedLine, ExtractableResponse<Response> result) {
        assertAll(
                () -> assertThat(result.jsonPath().getInt("id")).isEqualTo(savedLine.jsonPath().getInt("id")),
                () -> assertThat(result.jsonPath().getString("name")).isEqualTo(savedLine.jsonPath().getString("name")),
                ()-> assertThat(result.jsonPath().getString("color")).isEqualTo(savedLine.jsonPath().getString("color")),
                ()-> assertThat(result.jsonPath().getInt("distance")).isEqualTo(savedLine.jsonPath().getInt("distance"))
        );
    }

    private ExtractableResponse<Response> 특정_노선을_제거한다(int id) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/line/" + id)
                .then().log().all()
                .extract();
    }



}

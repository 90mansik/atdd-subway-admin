package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.utils.AcceptanceApiFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노션 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;
    private int 강남역_ID;
    private int 교대역_ID;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.clean();

        강남역_ID = 지하철역_생성("강남역").jsonPath().getInt("id");
        교대역_ID = 지하철역_생성("교대역").jsonPath().getInt("id");
    }

    @Test
    @DisplayName("지하철 노선을 생성 후 지하철 노선 목록 조회 테스트")
    void createLineAndFindLines() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 강남역_ID, 교대역_ID);

        // then
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        List<String> 지하철노선_조회_결과 = 지하철노선_목록_조회();
        assertAll(
                () -> assertThat(지하철노선_조회_결과).size().isEqualTo(1),
                () -> assertThat(지하철노선_조회_결과).containsAnyOf("2호선")
        );
    }

    @Test
    @DisplayName("지하철 노선을 2개 생성 후 지하철 노선 목록 조회 테스트")
    void createLineAndFindLines2() {
        ExtractableResponse<Response> 지하철_2호선_생성결과 = 지하철노선_생성("2호선", "bg-green-600", 강남역_ID, 교대역_ID);
        ExtractableResponse<Response> 지하철_3호선_생성결과 = 지하철노선_생성("3호선", "bg-orange-600", 강남역_ID, 교대역_ID);

        assertAll(
                () -> assertThat(지하철_2호선_생성결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(지하철_3호선_생성결과.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        );

        List<String> 지하철노선_조회_결과 = 지하철노선_목록_조회();

        assertAll(
                () -> assertThat(지하철노선_조회_결과).size().isEqualTo(2),
                () -> assertThat(지하철노선_조회_결과).containsAnyOf("2호선"),
                () -> assertThat(지하철노선_조회_결과).containsAnyOf("3호선")
        );
    }

    @Test
    @DisplayName("지하철노선 1개를 생성하고 단건 조회 테스트")
    void createLineAndFindLine() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 강남역_ID, 교대역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long id = 지하철노선_생성_결과.jsonPath().getLong("id");

        ExtractableResponse<Response> 지하철노선_조회_결과 = 지하철노선_조회(id);

        assertAll(
                () -> assertThat(지하철노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철노선_조회_결과.jsonPath().getString("name")).isEqualTo("2호선")
        );
    }


    @Test
    @DisplayName("지하철 노선 수정 후 단건 조회 테스트")
    void updateLineAndFindLine() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 강남역_ID, 교대역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long id = 지하철노선_생성_결과.jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철노선_수정_결과 = 지하철노선_수정(id, "3호선", "bg-orange-600");
        assertThat(지하철노선_수정_결과.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> 지하철노선_조회_결과 = 지하철노선_조회(id);
        assertAll(
                () -> assertThat(지하철노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철노선_조회_결과.jsonPath().getString("name")).isEqualTo("3호선")
        );
    }

    @Test
    @DisplayName("지하철 노선 삭제 후 단건 조회 테스트")
    void deleteLine() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 강남역_ID, 교대역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long id = 지하철노선_생성_결과.jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철노선_삭제_결과 = 지하철노선_삭제(id);
        assertThat(지하철노선_삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> 지하철노선_조회_결과 = 지하철노선_조회(id);
        assertThat(지하철노선_조회_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}

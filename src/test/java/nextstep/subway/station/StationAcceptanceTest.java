package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = createStationWithStationName("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getStationList().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        createStationWithStationName("강남역");

        // when
        ExtractableResponse<Response> response = createStationWithStationName("강남역");

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
        // given
        createStationWithStationName("강남역");
        createStationWithStationName("교대역");

        // when
        List<String> stationNames = getStationList().jsonPath().getList("name", String.class);

        // then
        assertThat(stationNames).hasSize(2);
        assertThat(stationNames).containsExactly("강남역", "교대역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Long stationId = createStationWithStationName("강남역").jsonPath().getLong("id");

        // when
        deleteStationWithStationName(stationId);

        // then
        List<String> stationNames = getStationList().jsonPath().getList("name", String.class);
        assertThat(stationNames).hasSize(0);
        assertThat(stationNames).doesNotContain("강남역");
    }

    public static ExtractableResponse<Response> createStationWithStationName(String stationName){
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> getStationList() {
        return RestAssured
            .given().log().all()
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> deleteStationWithStationName(Long stationId) {
        return RestAssured.given().log().all()
            .when().delete("/stations/{id}", stationId)
            .then().log().all()
            .extract();
    }
}

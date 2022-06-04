package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceFactory.ID값으로_지하철노선_조회;
import static nextstep.subway.line.LineAcceptanceFactory.지하철노선_생성;
import static nextstep.subway.section.SectionAcceptanceFactory.지하철_구간_삭제;
import static nextstep.subway.section.SectionAcceptanceFactory.지하철구간_생성;
import static nextstep.subway.station.StationAcceptanceFactory.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    private static final int 소요산_신창구간_길이 = 20;
    @LocalServerPort
    int port;

    private StationResponse 신창역;
    private StationResponse 서울역;
    private StationResponse 소요산역;
    private LineResponse 일호선;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        소요산역 = 지하철역_생성("소요산역").as(StationResponse.class);
        신창역 = 지하철역_생성("신창역").as(StationResponse.class);
        서울역 = 지하철역_생성("서울역").as(StationResponse.class);
        일호선 = 지하철노선_생성("1호선", 소요산역.getId(), 신창역.getId(), "파란색", 소요산_신창구간_길이).as(LineResponse.class);
    }

    /**
     * When 지하철 구간에 새로운 구간을 추가하면
     * Then 노선에 구간을 등록한다.
     */
    @Test
    @Transactional
    void 역_사이에_새로운_역을_등록한다() {
        ExtractableResponse<Response> 일호선_구간 = 지하철구간_생성(일호선.getId(), 소요산역.getId(), 서울역.getId(), 10);

        assertThat(일호선_구간.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * When 지하철 구간에 상행 종점역이 추가하여 노선이 연장되면
     * Then 신규 상행 구간이 추가된다.
     */
    @Test
    void 새로운_역을_상행_종점으로_등록() {
        StationResponse 신규_상행역 = 지하철역_생성("신규상행역").as(StationResponse.class);
        ExtractableResponse<Response> 상행역_연장구간 = 지하철구간_생성(일호선.getId(), 신규_상행역.getId(), 소요산역.getId(), 5);

        assertThat(상행역_연장구간.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse 일호선_노선 = ID값으로_지하철노선_조회(일호선.getId());

        List<String> 일호선_역이름들 = 일호선_노선.getStationNames();

        assertThat(일호선_역이름들).contains("신규상행역");
    }

    /**
     * When 지하철 구간에 하행 종점역이 추가하여 노선이 연장되면
     * Then 신규 하행 구간이 추가된다.
     */
    @Test
    void 새로운_역을_하행_종점으로_등록() {
        StationResponse 신규_하행역 = 지하철역_생성("신규하행역").as(StationResponse.class);
        ExtractableResponse<Response> 하행역_연장구간 = 지하철구간_생성(일호선.getId(), 신창역.getId(), 신규_하행역.getId(), 5);

        assertThat(하행역_연장구간.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse 일호선_노선 = ID값으로_지하철노선_조회(일호선.getId());

        List<String> 일호선_역이름들 = 일호선_노선.getStationNames();

        assertThat(일호선_역이름들).contains("신규하행역");
    }

    /**
     * When 구간내에 추가되는 구간의 길이가 기존 구간보다 크거나 같은면
     * Then 구간추가가 실패한다.
     */
    @Test
    void 역사이에_신규역등록실패_기존구간의_길이보다_신규길이가_크거나같은경우() {
        int 기존구간과_동일한_구간길이 = 소요산_신창구간_길이;
        ExtractableResponse<Response> 동일한_길이의_구간등록_결과 = 지하철구간_생성(일호선.getId(), 소요산역.getId(), 서울역.getId(), 기존구간과_동일한_구간길이);

        assertThat(동일한_길이의_구간등록_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 상행역과 하행역이 이미 등록되어 있고
     * When 동일한 구간을 등록하면
     * Then 구간추가가 실패한다.
     */
    @Test
    void 상행역과_하행역이_모두_등록되어_있으면_추가할_수_없다() {
//        given
        ExtractableResponse<Response> 일호선_소요산_서울_구간등록 = 지하철구간_생성(일호선.getId(), 소요산역.getId(), 신창역.getId(), 10);
        ExtractableResponse<Response> 일호선_서울_신창_구간등록 = 지하철구간_생성(일호선.getId(), 서울역.getId(), 신창역.getId(), 10);

//        when
        ExtractableResponse<Response> 일호선_소요산_서울_구간_재등록 = 지하철구간_생성(일호선.getId(), 소요산역.getId(), 서울역.getId(), 5);
        ExtractableResponse<Response> 일호선_서울_신창_구간_재등록 = 지하철구간_생성(일호선.getId(), 서울역.getId(), 신창역.getId(), 5);

//        then
        assertThat(일호선_소요산_서울_구간_재등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(일호선_서울_신창_구간_재등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선에 상행역과 하행역이 등록되어 있고
     * When 상행, 하행을 포함하지 않는 구간을 등록하면
     * Then 구간추가가 실패한다.
     */
    @Test
    void 상행역과_하행역중_하나도_포함되어있지_않으면_추가할_수_없다() {
//        given
        StationResponse 인천역 = 지하철역_생성("인천역").as(StationResponse.class);
        StationResponse 동인천 = 지하철역_생성("동인천").as(StationResponse.class);

//          when
        ExtractableResponse<Response> 일호선_수원_신창_구간등록 = 지하철구간_생성(일호선.getId(), 인천역.getId(), 동인천.getId(), 10);

        assertThat(일호선_수원_신창_구간등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given 노선에 상행역, 중간역, 하행역이 등록되어 있고
     * When 상행역을 제거하면
     * Then 중간역 - 하행역 구간으로 재배치된다.
     */
    @Test
    void 상행역을_제거한다() {
        ExtractableResponse<Response> 일호선_소요산_서울역_구간 = 지하철구간_생성(일호선.getId(), 소요산역.getId(), 서울역.getId(), 10);

        ExtractableResponse<Response> extract = 지하철_구간_삭제(일호선.getId(), 소요산역.getId());

        assertThat(extract.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}

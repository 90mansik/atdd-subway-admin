package nextstep.subway.domain;

import static nextstep.subway.domain.Station.createStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class SectionsTest {
    Sections sections;

    @BeforeEach
    void setUp() {
        final Section initSection = Section.builder()
                .distance(10)
                .upStation(createStation("주안역"))
                .downStation(createStation("동인천역"))
                .build();

        final Section addSection = Section.builder()
                .distance(4)
                .upStation(createStation("동인천역"))
                .downStation(createStation("서울역"))
                .build();


        sections = new Sections(initSection);
        sections.addSection(addSection);
    }


    @Test
    @DisplayName("이미 상행선과 하행선이 존재하는 경우 등록이 되지 않는다.")
    void existUpStationAndDownStation() {
        final Section newSection = Section.builder().upStation(createStation("주안역"))
                .downStation(createStation("서울역"))
                .distance(5).build();

        assertThatIllegalArgumentException().isThrownBy(() -> sections.addSection(newSection));
    }


    @Test
    @DisplayName("기존 역 사이의 길이보다 크거나 같으면 등록을 할 수 없음")
    void overSizeDistance() {
        //given
        final Section selSection = Section.builder()
                .distance(12)
                .upStation(createStation("인천역"))
                .downStation(createStation("서울역"))
                .build();

        //when & then
        assertThatIllegalStateException()
                .isThrownBy(() -> sections.addSection(selSection));
    }


    @Test
    @DisplayName("상행선종점을 찾는다.")
    void getLastUpStation() {
        sections.addSection(Section.builder()
                .distance(2)
                .upStation(createStation("서울역"))
                .downStation(createStation("신림역"))
                .build());

        Station lastUpStation = sections.getLastUpStation();

        assertThat(lastUpStation).isEqualTo(createStation("주안역"));

    }

    @Test
    @DisplayName("하행선종점 찾는다.")
    void getLastDownStation() {
        sections.addSection(Section.builder()
                .distance(2)
                .upStation(createStation("서울역"))
                .downStation(createStation("신림역"))
                .build());

        Station lastDownStation = sections.getLastDownStation();

        assertThat(lastDownStation).isEqualTo(createStation("신림역"));

    }

    @Test
    @DisplayName("구간이 하나이면 제거 할수 없다.")
    void oneSectionIsNoDelete() {
        final Station 주안역 = createStation("주안역");
        final Station 동인천역 = createStation("동인천역");
        final Section initSection = Section.builder()
                .distance(10)
                .upStation(주안역)
                .downStation(동인천역)
                .build();

        Sections sections = new Sections(initSection);

        assertThatIllegalStateException().isThrownBy(() -> sections.deleteSectionStation(주안역));
    }

    @Test
    @DisplayName("구간에 존재하지 않은 역은 제거 할수 없다.")
    void isNotContainStationIsNoDelete() {
        assertThatIllegalStateException().isThrownBy(()
                -> sections.deleteSectionStation(createStation("광명역")));
    }


    @Test
    @DisplayName("상행역 종점을 제거 한다.")
    void lastUpStationRemove() {
        final Station 주안역 = createStation("주안역");
        sections.deleteSectionStation(주안역);
        final List<Section> list = sections.getList();
        assertAll(() -> {
            assertThat(list).hasSize(1);
            assertThat(sections.getLastUpStation()).isEqualTo(createStation("동인천역"));
        });
    }
}

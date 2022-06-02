package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.exception.NotFoundException;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_STATION_ID", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "DOWN_STATION_ID", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(SectionBuilder sectionBuilder) {
        this.upStation = sectionBuilder.upStation;
        this.downStation = sectionBuilder.downStation;
        this.distance = sectionBuilder.distance;
    }

    public static SectionBuilder builder(Station upStation, Station downStation, Distance distance) {
        return new SectionBuilder(upStation, downStation, distance);
    }

    public static class SectionBuilder {
        private Long id;
        private final Station upStation;
        private final Station downStation;
        private final Distance distance;

        private SectionBuilder(Station upStation, Station downStation, Distance distance) {
            validateUpStationNotNull(upStation);
            validateDownStationNotNull(downStation);
            this.upStation = upStation;
            this.downStation = downStation;
            this.distance = distance;
        }

        private void validateUpStationNotNull(Station upStation) {
            if (Objects.isNull(upStation)) {
                throw new NotFoundException("상행역 정보가 없습니다.");
            }
        }

        private void validateDownStationNotNull(Station downStation) {
            if (Objects.isNull(downStation)) {
                throw new NotFoundException("하행역 정보가 없습니다.");
            }
        }

        public SectionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public Section build() {
            return new Section(this);
        }
    }

    public void addLine(Line line) {
        validateLineNotNull(line);
        this.line = line;
    }

    private void validateLineNotNull(Line line) {
        if (Objects.isNull(line)) {
            throw new NotFoundException("노선 정보가 없습니다.");
        }
    }

    public void update(Section newSection) {
        if (isEqualsUpStation(newSection)) {
            updateUpStationByNewSection(newSection);
        }
        if (isEqualsDownStation(newSection)) {
            updateDownStationByNewSection(newSection);
        }
    }

    private boolean isEqualsUpStation(Section newSection) {
        return this.upStation().equals(newSection.upStation());
    }

    private void updateUpStationByNewSection(Section newSection) {
        distance.minus(newSection.distance);
        this.upStation = newSection.downStation();
    }

    private boolean isEqualsDownStation(Section newSection) {
        return this.downStation().equals(newSection.downStation());
    }

    private void updateDownStationByNewSection(Section newSection) {
        distance.minus(newSection.distance);
        this.downStation = newSection.upStation();
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public Distance distance() {
        return distance;
    }

    public Line line() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

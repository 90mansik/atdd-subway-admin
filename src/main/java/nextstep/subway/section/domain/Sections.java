package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        validateSection(section);
        addAndChangeSection(section);
    }

    public List<Station> orderStationsOfLine() {
        Section section = sections.stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("구간이 존재하지 않습니다."));

        Section preSection = this.findStartSection(section);
        List<Station> stations = new ArrayList<>();
        stations.add(preSection.getUpStation());

        while (preSection != null) {
            stations.add(preSection.getDownStation());
            preSection = this.findPostSection(preSection);
        }

        return stations;
    }

    public int size() {
        return this.sections.size();
    }

    private void validateSection(Section section) {
        Optional<Section> optionalUpSection = findByStation(section.getUpStation());
        Optional<Section> optionalDownSection = findByStation(section.getDownStation());

        if (!sections.isEmpty() && !optionalUpSection.isPresent() && !optionalDownSection.isPresent()) {
            throw new IllegalArgumentException("종점역이 노선에 등록되어있지 않습니다.");
        }

        if (optionalUpSection.isPresent() && optionalDownSection.isPresent()) {
            throw new IllegalArgumentException("이미 상하행종점역이 모두 노선에 존재합니다.");
        }
    }

    private void addAndChangeSection(Section section) {
        Section currentSection = findCurrentSection(section);
        if (currentSection != null) {
            currentSection.changeSection(section);
        }

        this.sections.add(section);
    }

    private Section findStartSection(Section section) {
        Optional<Section> optionalSection = findByDownStation(section.getUpStation());
        return optionalSection.map(this::findStartSection).orElse(section);
    }

    private Optional<Section> findByDownStation(Station station) {
        return this.sections.stream()
            .filter(s -> s.containDownStation(station))
            .findFirst();
    }

    private Optional<Section> findByUpStation(Station station) {
        return this.sections.stream()
            .filter(s -> s.containUpStation(station))
            .findFirst();
    }

    private Optional<Section> findByStation(Station station) {
        return this.sections.stream()
            .filter(s -> s.containUpStation(station) || s.containDownStation(station))
            .findFirst();
    }

    private Section findCurrentSection(Section section) {
        Optional<Section> optionalUpSection = findByUpStation(section.getUpStation());
        Optional<Section> optionalDownSection = findByDownStation(section.getDownStation());

        return optionalUpSection.orElseGet(() -> optionalDownSection.orElse(null));
    }

    private Section findPostSection(Section preSection) {
        return sections.stream()
            .filter(section -> section.isPostSection(preSection))
            .findFirst()
            .orElse(null);
    }

}

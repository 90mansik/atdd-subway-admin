package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(Station upStation, Station downStation, Distance distance) {
        if (this.lineStations.isEmpty())  {
            lineStations.add(new LineStation(distance, upStation, downStation, true));
            return;
        }
        LineStation addTarget = findAddTarget(upStation, downStation);

        this.lineStations.add(
                addTarget.addStation(upStation, downStation, distance)
        );
    }

    public List<Station> getStationsSortedByUpToDown() {
        List<Station> result = new ArrayList<>();
        LineStation lineStation = findStartLineStation();

        result.add(lineStation.getUpStation());
        result.add(lineStation.getDownStation());

        for (int i = 0; i < this.lineStations.size() - 1; i++) {
            lineStation = findNextLineStation(lineStation.getDownStation());
            result.add(lineStation.getDownStation());
        }

        return result;
    }

    private LineStation findAddTarget(Station upStation, Station downStation) {
        LineStation findByUpStation = findLineStationByUpStationId(upStation);
        LineStation findByDownStation = findLineStationByDownStationId(downStation);

        if (findByUpStation != null && findByDownStation != null) {
            throw new IllegalArgumentException("기존에 등록된 같은 상/하행역을 등록할 수 없습니다.");
        }
        if (findByUpStation == null && findByDownStation == null) {
            throw new IllegalArgumentException("노선에 등록되지 않은 역을 추가할 수 없습니다.");
        }

        if (findByUpStation != null) {
            return findByUpStation;
        }
        return findByDownStation;
    }

    private LineStation findLineStationByUpStationId(Station upStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isSameUpStation(upStation) || lineStation.isAddNewLast(upStation))
                .findFirst()
                .orElse(null);
    }

    private LineStation findLineStationByDownStationId(Station downStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isSameDownStation(downStation) || lineStation.isAddNewFirst(downStation))
                .findFirst()
                .orElse(null);
    }

    private LineStation findStartLineStation() {
        List<LineStation> findResult = this.lineStations
                .stream()
                .filter(LineStation::isStart)
                .collect(Collectors.toList());

        if (findResult.size() != 1) {
            throw new IllegalStateException("노선의 시작점이 1개가 아닙니다.");
        }

        return findResult.get(0);
    }

    private LineStation findNextLineStation(Station nextStation) {
        List<LineStation> findResult = this.lineStations
                .stream()
                .filter(value -> value.getUpStation().isSameId(nextStation.getId()))
                .collect(Collectors.toList());

        if (findResult.size() != 1) {
            throw new IllegalStateException(nextStation.getName() + " 의 다음역 정보가 1개가 아닙니다.");
        }

        return findResult.get(0);
    }
}

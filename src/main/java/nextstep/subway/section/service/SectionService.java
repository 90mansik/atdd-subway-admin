package nextstep.subway.section.service;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        List<Station> stations = stationRepository.findAllById(
            Arrays.asList(sectionRequest.getDownStationId(), sectionRequest.getUpStationId()));

        Station downStation = stations.stream()
            .filter(station -> station.getId().equals(sectionRequest.getDownStationId())).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("하행역이 존재하지 않습니다."));

        Station upStation = stations.stream()
            .filter(station -> station.getId().equals(sectionRequest.getUpStationId())).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("상행역이 존재하지 않습니다."));

        line.addSection(new Section(upStation, downStation, new Distance(sectionRequest.getDistance())));

        return LineResponse.from(line);
    }

    @Transactional
    public void removeSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = stationRepository.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException("삭제하려는 지하철역이 존재하지 않습니다."));

        line.removeStationInSection(station);
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));
    }

}

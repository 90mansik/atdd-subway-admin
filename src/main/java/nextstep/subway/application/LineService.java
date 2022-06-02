package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine(getStation(lineRequest.getUpStationId()),
                getStation(lineRequest.getDownStationId()));
        Line persistStation = lineRepository.save(line);
        return LineResponse.of(persistStation);
    }

    @Transactional
    public LineResponse updateLine(long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
        Line persistStation = lineRepository.save(line);
        return LineResponse.of(persistStation);
    }

    @Transactional
    public Line addSection(Long id, SectionRequest sectionRequest) {
        Line line = getLine(id);
        line.addSection(sectionRequest.toSection(getStation(sectionRequest.getUpStationId()),
                getStation(sectionRequest.getDownStationId())));
        lineRepository.save(line);
        return line;
    }

    public Line deleteSection(Long id, Long stationId) {
        Line line = getLine(id);
        line.deleteSection(getStation(stationId));
        lineRepository.save(line);
        return line;
    }

    private Line getLine(long id) {
        return lineRepository.findById(id).orElseThrow(() -> new NotFoundException("등록된 노선이 없습니다."));
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException("등록된 지하철역이 없습니다."));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return LineResponse.of(getLine(id));
    }
}

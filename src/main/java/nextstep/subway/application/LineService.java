package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest lineRequest) {
        final Station upStation = findStationsById(lineRequest.getUpStationId());
        final Station downStation = findStationsById(lineRequest.getDownStationId());
        final Section section = new Section(upStation, downStation, lineRequest.getDistance());

        final Line persistLine = lineRepository.save(lineRequest.toLine(section));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        final List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long lineId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException(Line.class));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Line.class));
        line.update(lineRequest);
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse addSection(final Long id, final SectionRequest sectionRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Line.class));
        final Station upStation = findStationsById(sectionRequest.getUpStationId());
        final Station downStation = findStationsById(sectionRequest.getDownStationId());

        line.addSection(sectionRequest.toSection(upStation, downStation));

        return LineResponse.of(line);
    }

    private Station findStationsById(final Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException(Station.class));
    }
}

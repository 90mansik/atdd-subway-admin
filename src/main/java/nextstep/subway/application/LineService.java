package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.global.exception.BadRequestException;
import nextstep.subway.global.exception.ExceptionType;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        Line line = lineRepository.save(
            Line.of(
                lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getDistance(),
                upStation, downStation
            )
        );

        return LineResponse.from(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findLineAndStations();
        return lines.stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.from(line);
    }

    @Transactional(readOnly = true)
    public Line findOne(Long id) {
        return lineRepository.findLineAndStationsById(id)
            .orElseThrow(() -> new BadRequestException(ExceptionType.INVALID_LINE_ID));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        Line line = findLineById(id);
        lineRepository.delete(line);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        line.deleteSection(stationService.findById(stationId));
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new BadRequestException(ExceptionType.INVALID_LINE_ID));
    }

    private Station findStationById(Long id) {
        return stationService.findById(id);
    }
}

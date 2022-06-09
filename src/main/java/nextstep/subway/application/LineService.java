package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {
	private final StationRepository stationRepository;
	private final LineRepository lineRepository;
	private final SectionRepository sectionRepository;

	public LineService(StationRepository stationRepository, LineRepository lineRepository,
			SectionRepository sectionRepository) {
		this.stationRepository = stationRepository;
		this.lineRepository = lineRepository;
		this.sectionRepository = sectionRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Optional<Station> upStation = stationRepository.findById(lineRequest.getUpStationId());
		Optional<Station> downStation = stationRepository.findById(lineRequest.getDownStationId());

		Section section = sectionRepository.save(lineRequest.toSection(upStation.get(), downStation.get()));
		Line line = lineRepository.save(lineRequest.toLine(section));
		return LineResponse.of(line);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream().map(line -> LineResponse.of(line)).collect(Collectors.toList());
	}

	public LineResponse findLine(Long lineId) {
		Optional<Line> line = lineRepository.findById(lineId);
		return LineResponse.of(line.get());
	}

	@Transactional
	public void updateNameAndColor(Long lineId, LineRequest lineRequest) {
		Line line = lineRepository.getById(lineId);
		line.updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(() -> new StationNotFoundException());
		Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(() -> new StationNotFoundException());
		
		Section section = sectionRepository.save(sectionRequest.toSection(upStation, downStation));
		Line line = lineRepository.getById(lineId);
		line.add(section);
	}

	@Transactional
	public void removeSectionByStationId(Long lineId, Long stationId) {
		Optional<Line> line = lineRepository.findById(lineId);
		Optional<Station> removeStation = stationRepository.findById(stationId);

		line.orElseThrow(() -> new LineNotFoundException())
				.removeSection(removeStation.orElseThrow(() -> new StationNotFoundException()));
	}

}

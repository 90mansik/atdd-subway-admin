package nextstep.subway.line.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query(value = "select l from Line l "
        + "join fetch l.sections ")
    List<Line> findLineAndStations();

    @Query(value = "select l from Line l "
        + "join fetch l.sections "
        + "where l.id = :id")
    Optional<Line> findLineAndStationsById(Long id);
}

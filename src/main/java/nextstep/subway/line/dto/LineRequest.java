package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public LineRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public LineRequest() {
    }

    public Line toLine(Section section) {
        return new Line(name, color, section);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

}

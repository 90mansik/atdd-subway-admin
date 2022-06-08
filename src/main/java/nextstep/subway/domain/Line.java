package nextstep.subway.domain;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.sun.istack.NotNull;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	@NotNull
	private String color;

	@Embedded
	private Sections sections = new Sections();

	protected Line() {
	}

	public Line(String name, String color, Section section) {
		this.name = name;
		this.color = color;
		add(section);
	}

	public void add(Section section) {
		sections.add(section);
	}

	public void updateNameAndColor(String name, String color) {
		this.name = name;
		this.color = color;
	}
	
	public void removeSection(Optional<Station> removeStation) {
		sections.removeSection(removeStation);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public Sections getSections() {
		return sections;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Line other = (Line) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Line [id=" + id + ", name=" + name + ", color=" + color + ", sections=" + sections + "]";
	}
}

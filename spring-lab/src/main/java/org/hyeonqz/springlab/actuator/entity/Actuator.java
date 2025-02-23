package org.hyeonqz.springlab.actuator.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity(name="actuator")
public class Actuator {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long no;

	private LocalDateTime created;

	public Actuator (Long no, LocalDateTime created) {
		this.no = no;
		this.created = created;
	}

}

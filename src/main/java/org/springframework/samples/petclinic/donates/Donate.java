package org.springframework.samples.petclinic.donates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.samples.petclinic.model.BaseEntity;
import java.time.LocalDate;

@Entity
@Table(name = "donates")
public class Donate extends BaseEntity {

	@Column(name = "donator_name")
	@NotBlank
	private String donatorName;

	@Column(name = "donate_date")
	@NotNull
	private LocalDate date;

	@Column(name = "amount")
	@NotBlank
	private String amount;

	@Column(name = "message")
	@NotBlank
	private String message;

	public String getDonatorName() {
		return donatorName;
	}

	public void setDonatorName(String donatorName) {
		this.donatorName = donatorName;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

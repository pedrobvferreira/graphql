package com.example.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SampleRequest {

	@NotBlank
	private String firstName;
	private String lastName;
}

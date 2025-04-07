package com.example.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SampleRequest {

	@NotNull
	private String firstName;
	private String lastName;
}

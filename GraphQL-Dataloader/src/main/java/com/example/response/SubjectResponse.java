package com.example.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectResponse {
	private Long id;
	private String subjectName;
	private Double marksObtained;

    public SubjectResponse(Long id, String subjectName, Double marksObtained) {
		this.id = id;
		this.subjectName = subjectName;
		this.marksObtained = marksObtained;
    }
}

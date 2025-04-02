package com.example.response;

import com.example.entity.Subject;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Getter;
import lombok.Setter;

@GraphQLType
@Getter
@Setter
public class SubjectResponse {

	private Long id;
	private String subjectName;
	private Double marksObtained;
	
	public SubjectResponse (Subject subject) {
		this.id = subject.getId();
		this.subjectName = subject.getSubjectName();
		this.marksObtained = subject.getMarksObtained();
	}
}

package com.example.dto;

import com.example.entity.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {
    private Long id;
    private String subjectName;
    private Double marksObtained;

    public SubjectDTO(Subject address) {
        this.id = address.getId();
        this.subjectName = address.getSubjectName();
        this.marksObtained = address.getMarksObtained();
    }
}

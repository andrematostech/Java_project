package org.estg.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembersDTO {

    private String id;

    @JsonAlias("name")
    private String fullName;

    private String email;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String trainingGoal;
    private String experienceLevel;
    private String status;
    private String address;
    private String city;
    private String zipCode;
}

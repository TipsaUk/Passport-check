package ru.elistratov.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class CheckDTO {

    private boolean result;
    private LocalDate listVersionDate;
    private String error;
    private List<CheckPassportDto> passports;

}

package ru.elistratov.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CheckServiceDto {

    private boolean available;
    private LocalDate listVersionDate;

}

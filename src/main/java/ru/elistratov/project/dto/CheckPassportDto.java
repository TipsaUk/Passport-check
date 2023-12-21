package ru.elistratov.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckPassportDto {

    private boolean invalidPassport;
    private String series;
    private String number;

}

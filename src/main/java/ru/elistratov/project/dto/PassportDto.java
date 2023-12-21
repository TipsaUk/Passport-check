package ru.elistratov.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PassportDto {

    private String series;
    private String number;

}

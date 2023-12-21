package ru.elistratov.project.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DataReadCmd {

    private final String filePath;

    private final int beginLine;

    private final int endLine;

}

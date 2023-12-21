package ru.elistratov.project.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.elistratov.project.dto.CheckDTO;
import ru.elistratov.project.dto.CheckServiceDto;
import ru.elistratov.project.dto.PassportDto;
import ru.elistratov.project.services.PassportLoader;
import ru.elistratov.project.services.PassportService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PassportCheckImpl implements PassportCheck {

    private final PassportLoader passportLoader;
    private final PassportService passportService;

    @Override
    public ResponseEntity<CheckServiceDto> checkService() {
        return ResponseEntity.ok(passportService.checkService());
    }

    @Override
    public ResponseEntity<CheckDTO> checkPassport(PassportDto passportDto) {
        return ResponseEntity.ok(passportService.checkPassport(passportDto));
    }

    @Override
    public ResponseEntity<CheckDTO> checkPassports(List<PassportDto> passports) {
        return ResponseEntity.ok(passportService.checkPassports(passports));
    }

    @Override
    public ResponseEntity<String> loadNewVersionPassports() {
        passportLoader.updateInvalidPassports();
        return ResponseEntity.ok("Started");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleResourceNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

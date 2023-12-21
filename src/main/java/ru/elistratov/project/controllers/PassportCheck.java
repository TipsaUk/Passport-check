package ru.elistratov.project.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.elistratov.project.dto.CheckDTO;
import ru.elistratov.project.dto.CheckServiceDto;
import ru.elistratov.project.dto.PassportDto;

import java.util.List;

@RequestMapping("/api")
public interface PassportCheck {

    @GetMapping("/service")
    ResponseEntity<CheckServiceDto> checkService();

    @GetMapping("/check")
    ResponseEntity<CheckDTO> checkPassport(PassportDto passportDto);

    @PostMapping ("/checkList")
    ResponseEntity<CheckDTO> checkPassports(@RequestBody List<PassportDto> passports);

    @GetMapping("/load_new_version")
    ResponseEntity<String> loadNewVersionPassports();
}

package ru.elistratov.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invalid_passport")
public class InvalidPassport {

    @Id
    @Column(name = "number", columnDefinition = "varchar(10)")
    private String number;

}

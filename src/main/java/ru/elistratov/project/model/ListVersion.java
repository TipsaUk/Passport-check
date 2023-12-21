package ru.elistratov.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "list_version")
public class ListVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "status_Code", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusCode statusCode;

    @Column(name = "error")
    private String error;

    @Column(name = "amount_passports")
    private long amountPassports;

}

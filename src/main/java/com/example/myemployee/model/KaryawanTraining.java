package com.example.myemployee.model;

import lombok.Data;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "karyawan_training")
@Where(clause = "deleted_date is null")
public class KaryawanTraining extends AbstractDate implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tangal_training")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyMMdd")
    private Date tanggalTraining;

    @ManyToOne(targetEntity = Karyawan.class, cascade = CascadeType.ALL)
    public Karyawan karyawan;

    @ManyToOne(targetEntity = Training.class, cascade = CascadeType.ALL)
    public Training training;
}


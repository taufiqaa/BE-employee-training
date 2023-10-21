package com.example.myemployee.model;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "rekening")
@Where(clause = "deleted_date is null")
public class Rekening extends AbstractDate implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jenis", length = 10)
    private String jenis;

    @Column(name = "nama", length = 45)
    private String nama;

    @Column(name = "rekening", length = 10)
    private String rekening;

    @ManyToOne(targetEntity = Karyawan.class)
    private Karyawan karyawan;

}

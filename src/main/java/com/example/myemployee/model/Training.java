package com.example.myemployee.model;

import lombok.Data;
import org.hibernate.annotations.Where;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "training")
@Where(clause = "deleted_date is null")
public class Training extends AbstractDate implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pengajar", length = 45)
    private String pengajar;

    @Column(name = "tema", length = 90)
    private String tema;


//    @JsonIgnore
//    @OneToOne(mappedBy = "training")
//    private KaryawanTraining karyawanTraining;

    @JsonIgnore
    @OneToMany(mappedBy = "training")
    List<KaryawanTraining> karyawanTrainings;


}

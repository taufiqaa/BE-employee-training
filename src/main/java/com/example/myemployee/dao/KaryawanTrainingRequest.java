package com.example.myemployee.dao;

import com.example.myemployee.model.Karyawan;
import com.example.myemployee.model.Training;
import lombok.Data;

import java.util.Date;

@Data
public class KaryawanTrainingRequest {

    public Long id;

    public Long idTraining;
    public Long idKaryawan;
    public Date tanggalTraining;
}

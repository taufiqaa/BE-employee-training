package com.example.myemployee.service.impl;

import com.example.myemployee.model.Karyawan;
import com.example.myemployee.model.KaryawanTraining;
import com.example.myemployee.model.Training;
import com.example.myemployee.repository.KaryawanRepository;
import com.example.myemployee.repository.KaryawanTrainingRepository;
import com.example.myemployee.repository.TrainingRepository;
import com.example.myemployee.service.KaryawanTrainingService;
import com.example.myemployee.utils.TemplateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class KaryawanTrainingImpl implements KaryawanTrainingService {

    public static final Logger log = LoggerFactory.getLogger(KaryawanImpl.class);

    @Autowired
    public TemplateResponse templateResponse;

    @Autowired
    public TrainingRepository trainingRepository;

    @Autowired
    public KaryawanRepository karyawanRepository;

    @Autowired
    public KaryawanTrainingRepository karyawanTrainingRepository;


    public Map insert(KaryawanTraining obj) {
        Map map =new HashMap<>();
        try {

            Karyawan karyawanNew = karyawanRepository.getByID(obj.karyawan.getId());
            if ( templateResponse.checkNull(obj.karyawan.getId()) ) {
                return templateResponse.templateError("Id Karyawan Tidak boleh null");
            }
            Training trainingNew = trainingRepository.getbyID(obj.training.getId());
            if ( templateResponse.checkNull(obj.training.getId()) ) {
                return templateResponse.templateError("Id Training Tidak boleh null");
            }

            if ( templateResponse.checkNull(trainingNew) ) {
                return templateResponse.templateError("Id Training Tidak ada di database");
            }

// Buat objek KaryawanTraining
            KaryawanTraining karyawanTraining = new KaryawanTraining();
            karyawanTraining.setTraining(trainingNew);
            karyawanTraining.setKaryawan(karyawanNew);
            karyawanTraining.setTanggalTraining(obj.getTanggalTraining());

            // Simpan objek KaryawanTraining ke basis data
            karyawanTrainingRepository.save(karyawanTraining);
            //disimpan ke db: objek transaksi
            KaryawanTraining saveKryTraining = karyawanTrainingRepository.save(karyawanTraining);
            return templateResponse.templateSukses(karyawanTraining);

        } catch ( Exception e ) {
            return templateResponse.templateError(e);
        }
    }


    public Map update(KaryawanTraining obj) {
        Map map =new HashMap<>();
        try {
            if ( templateResponse.checkNull(obj.karyawan.getId()) ) {
                return templateResponse.templateError("Id Karyawan Tidak boleh null");
            }

            if ( templateResponse.checkNull(obj.training.getId()) ) {
                return templateResponse.templateError("Id Training Tidak boleh null");
            }
            if ( templateResponse.checkNull(obj.getId()) ) {
                return templateResponse.templateError("Id Karyawan Training Tidak boleh null");
            }

            Karyawan checkKaryawan = karyawanRepository.getByID(obj.karyawan.getId());
            if ( templateResponse.checkNull(checkKaryawan) ) {
                return templateResponse.templateError("Id Karyawan Tidak ada di database");
            }

            Training checkTraining = trainingRepository.getbyID(obj.training.getId());
            if ( templateResponse.checkNull(checkTraining) ) {
                return templateResponse.templateError("Id Training Tidak ada di database");
            }

            KaryawanTraining checkKryTraining = karyawanTrainingRepository.getbyID(obj.getId());
            if ( templateResponse.checkNull(checkKryTraining) ) {
                return templateResponse.templateError("Id Karyawan Training Tidak ada di database");
            }
            //update disini
            checkKryTraining.setTraining(checkTraining);
            checkKryTraining.setKaryawan(checkKaryawan);
            checkKryTraining.setTanggalTraining(checkKryTraining.getTanggalTraining());
            checkKryTraining.setUpdated_date(new Date());
            KaryawanTraining saveUpdate = karyawanTrainingRepository.save(checkKryTraining);
            return templateResponse.templateSukses(saveUpdate);
        } catch ( Exception e ) {
            return templateResponse.templateError(e);
        }

    }
    @Override
    public Map delete(Long obj) {
        Map map =new HashMap<>();
        try {
            if ( templateResponse.checkNull(obj) ) {
                return templateResponse.templateError("Id Karyawan Training is required");
            }
            //            1. chek id karyawan
            KaryawanTraining checkKaryawanTraining = karyawanTrainingRepository.getbyID(obj);
            if ( templateResponse.checkNull(checkKaryawanTraining) ) {
                return templateResponse.templateError("Id Karyawan Training Not found");
            }

//            2. update , tanggal deleted saja
            checkKaryawanTraining.setDeleted_date(new Date());//
            karyawanTrainingRepository.save(checkKaryawanTraining);

            return templateResponse.templateSukses("sukses deleted");

        } catch ( Exception e ) {
            return templateResponse.templateError(e);
        }
    }
}


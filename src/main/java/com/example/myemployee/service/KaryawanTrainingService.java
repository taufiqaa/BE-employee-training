package com.example.myemployee.service;


import com.example.myemployee.dao.KaryawanTrainingRequest;
import com.example.myemployee.model.KaryawanTraining;

import java.util.Map;

public interface KaryawanTrainingService {

    public Map insert(KaryawanTraining obj);

    public Map update(KaryawanTraining obj);

    public Map delete(Long obj);
}

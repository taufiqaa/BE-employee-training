package com.example.myemployee.service;

import com.example.myemployee.model.Training;

import java.util.Map;

public interface TrainingService {

    public Map insert(Training obj);

    public Map update(Training obj);

    public Map delete(Long idTraining);
}

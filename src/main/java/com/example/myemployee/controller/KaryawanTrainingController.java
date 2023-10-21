package com.example.myemployee.controller;

import com.example.myemployee.dao.KaryawanTrainingRequest;
import com.example.myemployee.model.Karyawan;
import com.example.myemployee.model.KaryawanTraining;
import com.example.myemployee.repository.KaryawanTrainingRepository;
import com.example.myemployee.service.KaryawanTrainingService;
import com.example.myemployee.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/idstar/karyawan-training")
public class KaryawanTrainingController {

    @Autowired
    public KaryawanTrainingService karyawanTrainingService;

    @Autowired
    public KaryawanTrainingRepository karyawanTrainingRepository;

    @Autowired
    public TemplateResponse templateResponse;

    @PostMapping("/save")
    public ResponseEntity<Map> save(@RequestBody KaryawanTraining objModel) {
        Map obj = karyawanTrainingService.insert(objModel);
        return new ResponseEntity<Map>(obj, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Map> update(@RequestBody KaryawanTraining objModel) {
        Map map = karyawanTrainingService.update(objModel);
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map> delete(@RequestBody Map<String, Long> requestBody) {
        Long id = requestBody.get("id");
        if (id == null) {
            // Handle the case where "id" is not provided in the request body
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Id Karyawan Training is required");
            errorResponse.put("status", "400"); // or use an appropriate HTTP status code
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } else {
            // Perform the delete operation using the "id"
            Map map = karyawanTrainingService.delete(id);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map> listByNama(
            @RequestParam() Integer page,
            @RequestParam() Integer size,
            @RequestParam(required = false) String namaKaryawan,// ga mandatory : default mandatory
            @RequestParam(required = false) String temaTraining) {
        Map map = new HashMap();
        Page<KaryawanTraining> list = null;
        Pageable show_data = PageRequest.of(page, size, Sort.by("id").descending());//batasin roq

        list = karyawanTrainingRepository.getAllData(show_data);
        return new ResponseEntity<Map>(templateResponse.templateSukses(list), new HttpHeaders(), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map> getId(@PathVariable(value = "id") Long id) {
        KaryawanTraining obj1 = karyawanTrainingRepository.getbyID(id);
        return new ResponseEntity<Map>(templateResponse.templateSukses(obj1), HttpStatus.OK);
    }
}


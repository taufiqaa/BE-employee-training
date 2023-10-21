package com.example.myemployee.controller;

import com.example.myemployee.model.Karyawan;
import com.example.myemployee.model.Rekening;
import com.example.myemployee.model.Training;
import com.example.myemployee.repository.RekeningRepository;
import com.example.myemployee.service.RekeningService;
import com.example.myemployee.utils.TemplateResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/idstar/rekening")
public class RekeningController {

    @Autowired
    RekeningService rekeningService;

    @Autowired
    RekeningRepository rekeningRepository;

    @Autowired
    public TemplateResponse templateResponse;


    @PostMapping("/save")
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map> save(@RequestBody Rekening objModel) {
        Map map = new HashMap();
        Map obj = rekeningService.insert(objModel);
        return new ResponseEntity<Map>(obj, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Map> update(@RequestBody Rekening objModel) {
        Map obj = rekeningService.update(objModel);
        return new ResponseEntity<Map>(obj, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map> delete(@RequestBody Map<String, Long> requestBody) {
        Long id = requestBody.get("id");
        if (id == null) {
            // Handle the case where "id" is not provided in the request body
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Id Rekening is required");
            errorResponse.put("status", "400"); // or use an appropriate HTTP status code
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } else {
            // Perform the delete operation using the "id"
            Map map = rekeningService.delete(id);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }



    @GetMapping("/list")
    public ResponseEntity<Map> listByTema(
            @RequestParam() Integer page,
            @RequestParam() Integer size,
            @RequestParam(required = false) Long id) {
        Map map = new HashMap();
        Page<Rekening> list = null;
        Pageable show_data = PageRequest.of(page, size, Sort.by("id").descending());//batasin roq

        if ( id != null) {
            list = (Page<Rekening>) rekeningRepository.getbyID(id);
        } else {
            list = rekeningRepository.getAllData(show_data);
        }
        return new ResponseEntity<Map>(templateResponse.templateSukses(list), new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map> getId(@PathVariable(value = "id") Long id) {
        Rekening obj1 = rekeningRepository.getbyID(id);
        return new ResponseEntity<Map>(templateResponse.templateSukses(obj1), HttpStatus.OK);
    }


}

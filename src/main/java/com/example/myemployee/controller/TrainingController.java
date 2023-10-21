package com.example.myemployee.controller;

import com.example.myemployee.model.Training;
import com.example.myemployee.repository.TrainingRepository;
import com.example.myemployee.service.TrainingService;
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
@RequestMapping("/v1/idstar/training")
public class TrainingController {

    @Autowired
    public TrainingService trainingService;

    @Autowired
    public TrainingRepository trainingRepository;

    @Autowired
    public TemplateResponse templateResponse;

    @PostMapping("/save")
    public ResponseEntity<Map> save(@RequestBody Training objModel) {
      Map map =  trainingService.insert(objModel);
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Map> update(@RequestBody Training objModel) {
        Map obj = trainingService.update(objModel);
        return new ResponseEntity<Map>(obj, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map> delete(@RequestBody Map<String, Long> requestBody) {
        Long id = requestBody.get("id");
        if (id == null) {
            // Handle the case where "id" is not provided in the request body
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Id Training is required");
            errorResponse.put("status", "400"); // or use an appropriate HTTP status code
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } else {
            // Perform the delete operation using the "id"
            Map map = trainingService.delete(id);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }


    @GetMapping("/list")
    public ResponseEntity<Map> listByTema(
            @RequestParam() Integer page,
            @RequestParam() Integer size,
            @RequestParam(required = false) String tema) {
        Map map = new HashMap();
        Page<Training> list = null;
        Pageable show_data = PageRequest.of(page, size, Sort.by("id").descending());//batasin roq

        if ( tema != null && !tema.isEmpty() ) {
            list = trainingRepository.findByTema("%" + tema + "%", show_data);
        } else {
            list = trainingRepository.getAllData(show_data);
        }
        return new ResponseEntity<Map>(templateResponse.templateSukses(list), new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map> getId(@PathVariable(value = "id") Long id) {
        Training obj1 = trainingRepository.getbyID(id);
        return new ResponseEntity<Map>(templateResponse.templateSukses(obj1), HttpStatus.OK);
    }


}

package com.example.myemployee.repository;

import com.example.myemployee.model.KaryawanTraining;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KaryawanTrainingRepository extends PagingAndSortingRepository <KaryawanTraining, Long>{

    @Query("select c from KaryawanTraining c WHERE c.id = :id")
    public KaryawanTraining getbyID(@Param("id") Long id);

    @Query("select c from KaryawanTraining c")
    public Page<KaryawanTraining> getAllData(Pageable pageable);


}

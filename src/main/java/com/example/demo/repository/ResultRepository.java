package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {

	  List<Result> findByStudentEmailOrderByIdDesc(String studentEmail);

	
}
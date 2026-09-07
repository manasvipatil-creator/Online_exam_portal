package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Exam;

public interface ExamRepository extends JpaRepository<Exam,Integer>{

	
	 List<Exam> findByexamNameContainingIgnoreCase(String examName);
}

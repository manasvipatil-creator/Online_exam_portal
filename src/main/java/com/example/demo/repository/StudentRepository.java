package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Student;

public interface StudentRepository extends JpaRepository<Student,Integer>{

	
	Student findByEmailAndPassword(String email, String password);
}


















//JpaRepository import kela
//✅ Student entity connect keli
//✅ Primary key type Integer thevla
//✅ Repository ready zhala
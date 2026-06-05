package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;

@Service
public class StudentService {
	
	 @Autowired                                 //mhanje Spring swatah StudentRepository cha object tayar karun studentRepository madhye takel.
	    private StudentRepository studentRepository;
	 
	 
	 public boolean registerStudent(Student student) {
		 
		 try {
			 studentRepository.save(student);
			 return true;
		 }catch(Exception e) {
			 e.printStackTrace();
			 return false;
		 }
		 }
	 
	
	 public Student loginStudent(String email, String password) {                  //student login sathi email and password correct asel tr ch login honar
		 
		return studentRepository.findByEmailAndPassword(email, password);
	 }
	 
	 public List<Student> getAllStudents() {

		    return studentRepository.findAll();
		}
	 
	 public Student getStudentById(int id) {        //for view student details in admin manage student

		    return studentRepository
		            .findById(id)
		            .orElse(null);
		}
	 
	 public void deleteStudent(int id) {              //for delete the student in admin-->manage student

		    studentRepository.deleteById(id);
		}
}

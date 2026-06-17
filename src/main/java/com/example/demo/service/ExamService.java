package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.service.ExamService;
import com.example.demo.service.QuestionService;
import com.example.demo.entity.Exam;
import com.example.demo.repository.ExamRepository;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;
    
   

    @Autowired
    private QuestionService questionService;

    public boolean addExam(Exam exam) {

        try {
            examRepository.save(exam);
            return true;

        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Exam> getAllExams() {

        return examRepository.findAll();
    }
    
    public Exam getExamById(int id) {
        return examRepository.findById(id).orElse(null);
    }
}
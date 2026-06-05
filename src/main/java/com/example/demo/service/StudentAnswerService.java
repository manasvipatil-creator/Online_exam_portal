package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.StudentAnswer;
import com.example.demo.repository.StudentAnswerRepository;

@Service
public class StudentAnswerService {

    @Autowired
    private StudentAnswerRepository repository;

    public void saveAnswer(StudentAnswer answer) {

        StudentAnswer existing =
                repository.findByStudentIdAndQuestionId(
                        answer.getStudentId(),
                        answer.getQuestionId());

        if(existing != null) {

            existing.setSelectedAnswer(
                    answer.getSelectedAnswer());

            repository.save(existing);

        } else {

            repository.save(answer);
        }
    }

    public List<StudentAnswer> getAnswers(
            int studentId){

        return repository.findByStudentId(studentId);
    }
    
    public StudentAnswer getAnswer(int studentId, int questionId){

        return repository.findByStudentIdAndQuestionId(
                studentId,
                questionId);
    }
}
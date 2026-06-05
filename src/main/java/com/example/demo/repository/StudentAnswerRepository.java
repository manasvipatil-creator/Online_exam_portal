package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.StudentAnswer;

public interface StudentAnswerRepository
        extends JpaRepository<StudentAnswer, Integer> {

    List<StudentAnswer> findByStudentId(int studentId);

    StudentAnswer findByStudentIdAndQuestionId(
            int studentId,
            int questionId
    );
}
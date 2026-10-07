package com.example.demo.repository;

import com.example.demo.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamAttemptRepository
        extends JpaRepository<ExamAttempt, Integer> {

    Optional<ExamAttempt> findByStudentIdAndExamId(
            int studentId,
            int examId
    );
}
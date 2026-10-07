package com.example.demo.service;

import com.example.demo.entity.ExamAttempt;
import com.example.demo.repository.ExamAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;

    public ExamAttemptService(
            ExamAttemptRepository examAttemptRepository) {
        this.examAttemptRepository = examAttemptRepository;
    }

    // Find existing attempt
    public Optional<ExamAttempt> getAttempt(
            int studentId,
            int examId) {

        return examAttemptRepository
                .findByStudentIdAndExamId(studentId, examId);
    }

    // Create new attempt
    public ExamAttempt createAttempt(
            int studentId,
            int examId,
            long startTime,
            long endTime) {

        ExamAttempt attempt = new ExamAttempt();

        attempt.setStudentId(studentId);
        attempt.setExamId(examId);
        attempt.setStatus("IN_PROGRESS");
        attempt.setStartTime(startTime);
        attempt.setEndTime(endTime);

        return examAttemptRepository.save(attempt);
    }

    // Mark exam as completed
    public void completeAttempt(ExamAttempt attempt) {

        attempt.setStatus("COMPLETED");

        examAttemptRepository.save(attempt);
    }
    
    public void allowRetake(int studentId, int examId) {

        Optional<ExamAttempt> attempt =
                examAttemptRepository.findByStudentIdAndExamId(
                        studentId,
                        examId
                );

        if (attempt.isPresent()) {

            ExamAttempt examAttempt = attempt.get();

            examAttempt.setStatus("RETAKE_ALLOWED");

            examAttemptRepository.save(examAttempt);
        }
    }
    
    
    public void startRetake(
            ExamAttempt attempt,
            long startTime,
            long endTime) {

        attempt.setStatus("IN_PROGRESS");
        attempt.setStartTime(startTime);
        attempt.setEndTime(endTime);

        examAttemptRepository.save(attempt);
    }
    
    
    
}
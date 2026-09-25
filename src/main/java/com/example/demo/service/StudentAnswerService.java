package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entity.StudentAnswer;
import com.example.demo.repository.StudentAnswerRepository;

@Service
public class StudentAnswerService {

    @Autowired
    private StudentAnswerRepository repository;

    public void saveAnswer(StudentAnswer answer) {

        StudentAnswer existing =
                repository.findByStudentIdAndQuestionIdAndExamId(
                        answer.getStudentId(),
                        answer.getQuestionId(),
                        answer.getExamId());

        if (existing != null) {

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
    
    public List<StudentAnswer> getAnswersByStudentAndExam(
            int studentId,
            int examId) {

        return repository.findByStudentIdAndExamId(
                studentId,
                examId);
    }
    
    public void markForReview(
            int studentId,
            int questionId,
            int examId,
            boolean marked) {

        StudentAnswer answer =
                repository.findByStudentIdAndQuestionIdAndExamId(
                        studentId,
                        questionId,
                        examId);

        if (answer != null) {

            answer.setMarkedForReview(marked);

            repository.save(answer);

        } else {

            StudentAnswer newAnswer = new StudentAnswer();

            newAnswer.setStudentId(studentId);
            newAnswer.setQuestionId(questionId);
            newAnswer.setExamId(examId);
            newAnswer.setMarkedForReview(marked);

            repository.save(newAnswer);
        }
    }
    
    public StudentAnswer getAnswer(
            int studentId,
            int questionId,
            int examId) {

        return repository.findByStudentIdAndQuestionIdAndExamId(
                studentId,
                questionId,
                examId);
    }
    
    public long getAttemptedExamCount(int studentId) {

        return repository.countByStudentId(studentId);
    }
    
    
    @Transactional
    public void clearExamAnswers(int studentId, int examId) {

        repository.deleteByStudentIdAndExamId(studentId, examId);
    }
}
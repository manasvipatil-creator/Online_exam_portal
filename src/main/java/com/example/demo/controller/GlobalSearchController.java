package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Exam;
import com.example.demo.entity.Question;
import com.example.demo.entity.Result;
import com.example.demo.entity.Student;
import com.example.demo.service.ExamService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.ResultService;
import com.example.demo.service.StudentService;

@RestController
public class GlobalSearchController {

    @Autowired
    private ExamService examService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ResultService resultService;


    @GetMapping("/admin/global-search")
    public Map<String, Object> globalSearch(
            @RequestParam(required = false) String keyword) {

        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> results =
                new ArrayList<>();


        if (keyword == null || keyword.isBlank()) {

            response.put("results", results);

            return response;
        }


        String searchKeyword =
                keyword.trim().toLowerCase();


        // =========================
        // SEARCH EXAMS
        // =========================

        List<Exam> exams =
                examService.getAllExams();

        for (Exam exam : exams) {

            if (exam.getExamName() != null &&
                exam.getExamName()
                    .toLowerCase()
                    .contains(searchKeyword)) {

                Map<String, Object> item =
                        new HashMap<>();

                item.put("type", "Exam");
                item.put("title", exam.getExamName());
                item.put("subtitle", exam.getSubject());
                item.put("url", "/admin/manage-exams");

                results.add(item);
            }
        }


        // =========================
        // SEARCH STUDENTS
        // =========================

        List<Student> students =
                studentService.getAllStudents();

        for (Student student : students) {

            boolean nameMatch =
                    student.getFullName() != null &&
                    student.getFullName()
                            .toLowerCase()
                            .contains(searchKeyword);

            boolean emailMatch =
                    student.getEmail() != null &&
                    student.getEmail()
                            .toLowerCase()
                            .contains(searchKeyword);


            if (nameMatch || emailMatch) {

                Map<String, Object> item =
                        new HashMap<>();

                item.put("type", "Student");
                item.put("title", student.getFullName());
                item.put("subtitle", student.getEmail());

                item.put(
                    "url",
                    "/admin/students/details/"
                    + student.getId()
                );

                results.add(item);
            }
        }


        // =========================
        // SEARCH QUESTIONS
        // =========================

        List<Question> questions =
                questionService.getAllQuestions();

        for (Question question : questions) {

            if (question.getQuestionText() != null &&
                question.getQuestionText()
                        .toLowerCase()
                        .contains(searchKeyword)) {

                Map<String, Object> item =
                        new HashMap<>();

                item.put("type", "Question");

                item.put(
                    "title",
                    question.getQuestionText()
                );

                item.put(
                    "subtitle",
                    "Question ID: " + question.getId()
                );

                item.put(
                    "url",
                    "/admin/manage-questions"
                );

                results.add(item);
            }
        }


        // =========================
        // SEARCH RESULTS
        // =========================

        List<Result> examResults =
                resultService.getAllResults();

        for (Result result : examResults) {

            boolean studentMatch =
                    result.getStudentName() != null &&
                    result.getStudentName()
                            .toLowerCase()
                            .contains(searchKeyword);

            boolean emailMatch =
                    result.getStudentEmail() != null &&
                    result.getStudentEmail()
                            .toLowerCase()
                            .contains(searchKeyword);

            boolean examMatch =
                    result.getExamName() != null &&
                    result.getExamName()
                            .toLowerCase()
                            .contains(searchKeyword);


            if (studentMatch ||
                emailMatch ||
                examMatch) {

                Map<String, Object> item =
                        new HashMap<>();

                item.put("type", "Result");
                item.put("title", result.getStudentName());

                item.put(
                    "subtitle",
                    result.getExamName()
                    + " - "
                    + result.getPercent()
                );

                item.put(
                    "url",
                    "/admin/manage-results"
                );

                results.add(item);
            }
        }


        response.put("results", results);

        return response;
    }
}
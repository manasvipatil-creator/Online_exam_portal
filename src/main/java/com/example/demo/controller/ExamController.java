package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Exam;
import com.example.demo.service.ExamService;

@Controller
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/admin/exams")
    public String createExamPage(Model model) {

        model.addAttribute("exam", new Exam());

        return "admin/manage-exams";    }

    @PostMapping("/admin/add-exam")
    public String addExam(@ModelAttribute Exam exam) {

        boolean result = examService.addExam(exam);

        if(result) {
            return "redirect:/admin/dashboard";
        }

        return "admin/manage-exams";
    }
}
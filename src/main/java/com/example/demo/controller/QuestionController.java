package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.demo.service.ExamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.entity.Question;
import com.example.demo.service.QuestionService;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private ExamService examService;

    @GetMapping("/admin/manage-questions")
    public String manageQuestions(Model model) {

        model.addAttribute(
                "questionsList",
                questionService.getAllQuestions());

        model.addAttribute(
                "exams",
                examService.getAllExams());

        return "admin/manage-questions";
    }
    
    @PostMapping("/admin/questions/add")
    public String addQuestion(@ModelAttribute Question question) {

        System.out.println("ADD QUESTION METHOD CALLED");

        questionService.addQuestion(question);

        return "redirect:/admin/manage-questions";
    }
}
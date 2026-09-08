package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.demo.service.ExamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.entity.Exam;
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
    	
    	model.addAttribute("question", new Question());

        // 1. सर्व exams मिळवा
        List<Exam> exams = examService.getAllExams();

        // 2. प्रत्येक exam चे questions वेगळे करा
        Map<Integer, List<Question>> questionsByExam = new HashMap<>();

        for (Exam exam : exams) {

            List<Question> examQuestions =
                    questionService.getQuestionsByExamId(exam.getId());

            questionsByExam.put(
                    exam.getId(),
                    examQuestions
            );
        }

        // 3. Thymeleaf ला data पाठवा
        model.addAttribute("exams", exams);
        model.addAttribute("questionsByExam", questionsByExam);

        // 4. Console debugging
        System.out.println("EXAMS = " + exams.size());
        System.out.println("QUESTIONS BY EXAM = " + questionsByExam);

        return "admin/manage-questions";
    }
    
    @PostMapping("/admin/questions/add")
    public String addQuestion(@ModelAttribute Question question) {

        System.out.println("ADD QUESTION METHOD CALLED");

        questionService.addQuestion(question);

        return "redirect:/admin/manage-questions";
    }
    
    @PostMapping("/admin/questions/update")
    public String updateQuestion(@ModelAttribute Question question) {

        System.out.println("UPDATE QUESTION METHOD CALLED");

        questionService.updateQuestion(question);

        return "redirect:/admin/manage-questions";
    }
    
    @GetMapping("/admin/questions/edit/{id}")
    public String editQuestion(
            @PathVariable int id,
            Model model) {

        Question question = questionService.getQuestionById(id);

        List<Exam> exams = examService.getAllExams();

        Map<Integer, List<Question>> questionsByExam = new HashMap<>();

        for (Exam exam : exams) {
            questionsByExam.put(
                    exam.getId(),
                    questionService.getQuestionsByExamId(exam.getId())
            );
        }

        model.addAttribute("question", question);
        model.addAttribute("exams", exams);
        model.addAttribute("questionsByExam", questionsByExam);

        return "admin/manage-questions";
    }
    
    @GetMapping("/admin/questions/delete/{id}")
    public String deleteQuestion(@PathVariable int id) {

        questionService.deleteQuestion(id);

        return "redirect:/admin/manage-questions";
    }
}
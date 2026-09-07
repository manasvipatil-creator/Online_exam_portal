package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Exam;
import com.example.demo.service.ExamService;

@Controller
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/admin/manage-exams")
    public String manageExams(
            @RequestParam(required = false) String keyword,
            Model model) {
    	
    	System.out.println("SEARCH KEYWORD = " + keyword);


        model.addAttribute("exam", new Exam());

        List<Exam> exams;

        if (keyword == null || keyword.isBlank()) {
            exams = examService.getAllExams();
        } else {
            exams = examService.searchExam(keyword);
        }

        System.out.println("EXAMS FOUND = " + exams.size());
       

        model.addAttribute("scheduledExamsList", exams);

        return "admin/manage-exams";
    }
    
    @PostMapping("/admin/add-exam")
    public String addExam(@ModelAttribute Exam exam) {

        boolean result = examService.addExam(exam);

        if(result) {
        	return "redirect:/admin/manage-exams";
        }

        return "admin/manage-exams";
    }



@GetMapping("/test")
@ResponseBody
public String test() {
    return "ExamController is working";
}

@GetMapping("/admin/manage-exams-test")
@ResponseBody
public String manageExamTest() {
    return "Manage Exams URL is working";
}

@GetMapping("/admin/manage-exams-page")
public String manageExamPage(Model model) {

    model.addAttribute("exam", new Exam());

    List<Exam> exams = examService.getAllExams();

    System.out.println("EXAMS FOUND = " + exams.size());

    model.addAttribute("scheduledExamsList", exams);

    return "admin/manage-exams";
}

@GetMapping("/admin/exams/delete/{id}")                      //for delete functionality
public String deleteExam(@PathVariable int id) {

    examService.deleteExam(id);

    return "redirect:/admin/manage-exams";
}

@GetMapping("/admin/exams/edit/{id}")                         // for edit functionality
public String editExam(@PathVariable int id, Model model) {

    Exam exam = examService.getExamById(id);

    model.addAttribute("exam", exam);

    List<Exam> exams = examService.getAllExams();
    model.addAttribute("scheduledExamsList", exams);

    return "admin/manage-exams";
}

@PostMapping("/admin/exams/update")                         //for update functionality
public String updateExam(@ModelAttribute Exam exam) {

    examService.updateExam(exam);

    return "redirect:/admin/manage-exams";
}


}
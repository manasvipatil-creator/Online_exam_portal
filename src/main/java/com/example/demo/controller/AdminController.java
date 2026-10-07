package com.example.demo.controller;
import com.example.demo.service.ExamService;
import com.example.demo.service.QuestionService;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Exam;
import com.example.demo.entity.ExamAttempt;
import com.example.demo.entity.Student;
import com.example.demo.service.ExamAttemptService;
import org.springframework.ui.Model;
import com.example.demo.service.AdminService;
import com.example.demo.service.ResultService;
import com.example.demo.service.StudentService;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private ResultService resultService;
    
    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private ExamAttemptService examAttemptService;
    
    
    
    @GetMapping("/admin/manage-students")
    public String manageStudents(Model model) {

        model.addAttribute(
                "students",
                studentService.getAllStudents());

        model.addAttribute(
                "totalStudents",
                studentService.getStudentCount());

        model.addAttribute(
                "activeStudents",
                studentService.getActiveStudentCount());

        model.addAttribute(
                "suspendedStudents",
                studentService.getSuspendedStudentCount());

        return "admin/manage-students";
    }
    
    
    

    @GetMapping("/admin/login")
    public String loginPage() {

    	 return "admin/admin-login";
    }
    
    

    @PostMapping("/admin/login")
    public String loginAdmin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        Admin admin =
                adminService.loginAdmin(
                        email,
                        password);

        if(admin != null) {

            session.setAttribute(
                    "admin",
                    admin);

            return "redirect:/admin/dashboard";
        }

        return "redirect:/admin/login?error";
    }
    
    

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session,
                            Model model) {

        Admin admin =
                (Admin) session.getAttribute("admin");

        if(admin == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("admin", admin);

        model.addAttribute(
                "totalStudents",
                studentService.getAllStudents().size());

        model.addAttribute(
                "totalExams",
                examService.getAllExams().size());

        model.addAttribute(
                "totalQuestions",
                questionService.getAllQuestions().size());

        model.addAttribute(
                "totalResults",
                resultService.getAllResults().size());

        model.addAttribute(
                "recentResults",
                resultService.getAllResults());

        return "admin/admin-dashboard";
    }
    
    
    
    @GetMapping("/admin/students/details/{id}")
    public String viewStudent(
            @PathVariable int id,
            Model model) {

        Student student =
                studentService.getStudentById(id);

        model.addAttribute("student", student);

        // Get all exams
        List<Exam> exams =
                examService.getAllExams();

        // Exams already attempted by this student
        List<Exam> attemptedExams =
                new ArrayList<>();

        for (Exam exam : exams) {

            Optional<ExamAttempt> attempt =
                    examAttemptService.getAttempt(
                            student.getId(),
                            exam.getId()
                    );

            if (attempt.isPresent()
                    && "COMPLETED".equals(
                            attempt.get().getStatus())) {

                attemptedExams.add(exam);
            }
        }

        model.addAttribute(
                "attemptedExams",
                attemptedExams
        );

        return "admin/student-details";
    }
    
    @GetMapping("/admin/students/delete/{id}")
    public String deleteStudent(
            @PathVariable int id) {

        studentService.deleteStudent(id);

        return "redirect:/admin/manage-students";
    }
    
    @GetMapping("/admin/students/status/{id}")
    public String changeStudentStatus(@PathVariable int id) {

        studentService.changeStudentStatus(id);

        return "redirect:/admin/manage-students";
    }
    
    
    
    @GetMapping("/admin/manage-results")
    public String manageResults(Model model) {

        model.addAttribute("resultsLedger", resultService.getAllResults());

        model.addAttribute(
            "totalResults",
            resultService.getTotalResults()
        );

        model.addAttribute(
            "averagePercentage",
            resultService.getAveragePercentage()
        );
        
        model.addAttribute(
        	    "passingRatio",
        	    resultService.getPassingRatio()
        	);

        model.addAttribute(
        	    "highestScore",
        	    resultService.getHighestScore()
        	);
        
        model.addAttribute(
        	    "examNames",
        	    resultService.getAllExamNames()
        	);
        
        return "admin/manage-results";
    }
    
    @GetMapping("/admin/allow-retake/{studentId}/{examId}")
    public String allowRetake(
            @PathVariable int studentId,
            @PathVariable int examId) {

        examAttemptService.allowRetake(
                studentId,
                examId
        );

        return "redirect:/admin/manage-students";
    }


 }
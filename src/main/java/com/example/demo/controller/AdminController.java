package com.example.demo.controller;

import com.example.demo.entity.Admin;
import org.springframework.ui.Model;
import com.example.demo.service.AdminService;
import com.example.demo.service.ResultService;
import com.example.demo.service.StudentService;

import jakarta.servlet.http.HttpSession;

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
    
    @GetMapping("/admin/manage-students")
    public String manageStudents(Model model) {

        model.addAttribute(
                "studentsList",
                studentService.getAllStudents());

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
    public String dashboard(HttpSession session) {

        System.out.println("ADMIN SESSION = "
                + session.getAttribute("admin"));

        return "admin/admin-dashboard";
    }
    
    @GetMapping("/admin/students/details/{id}")
    public String viewStudent(
            @PathVariable int id,
            Model model) {

        model.addAttribute(
                "student",
                studentService.getStudentById(id));

        return "admin/student-details";
    }
    
    @GetMapping("/admin/students/delete/{id}")
    public String deleteStudent(
            @PathVariable int id) {

        studentService.deleteStudent(id);

        return "redirect:/admin/manage-students";
    }
    
    @GetMapping("/admin/manage-results")
    public String manageResults(Model model) {

        model.addAttribute(
                "resultsLedger",
                resultService.getAllResults());

        return "admin/manage-results";
    }
}
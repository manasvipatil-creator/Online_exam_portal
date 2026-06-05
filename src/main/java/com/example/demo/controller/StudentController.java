package com.example.demo.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Question;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentAnswer;
import com.example.demo.service.ExamService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.StudentAnswerService;
import com.example.demo.service.StudentService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class StudentController {
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private ExamService examService;
	
	@Autowired 
	private QuestionService questionService;
	
	@Autowired
	private StudentAnswerService studentAnswerService;
	
	//register page open karnyasathi
	@GetMapping("/student/register")
	public String registerPage() {
		return "student/register";
		
	}
	
	
	@GetMapping("/student/login")
	public String loginPage() {
		return"student/login";
	}
	
	@PostMapping("/student/register")           //
	public String registerStudent(@ModelAttribute Student student ) {
		boolean result = studentService.registerStudent(student);
		return "student/register";
	}
	
	@PostMapping("/student/login")
	public String loginStudent(
	        @RequestParam String email,
	        @RequestParam String password,
	        HttpSession session) {

	    Student student =
	            studentService.loginStudent(email,password);

	    System.out.println(student);

	    if(student != null) {

	        session.setAttribute("student", student);

	        System.out.println("SESSION SAVED");

	        return "redirect:/student/dashboard";
	    }

	    return "student/login";
	}
	
	
	@GetMapping("/student/dashboard")
	public String dashboard(HttpSession session, Model model) {

	    System.out.println("STUDENT SESSION = "
	            + session.getAttribute("student"));

	    Student student = (Student) session.getAttribute("student");

	    if(student == null) {
	        return "student/login";
	    }

	    model.addAttribute("student", student);

	    return "student/dashboard";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {

	    session.invalidate();

	    return "redirect:/login?logout";
	}
	
	@GetMapping("/student/exams")
	public String availableExams(Model model) {
		
		model.addAttribute("examList", examService.getAllExams());
		return "student/exam-list";
	}
	
	
	@GetMapping("/student/exam/start/{id}/{index}")
	public String startExam(@PathVariable int id,
	                        @PathVariable int index,
	                        Model model,
	                        HttpSession session) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if(student == null){
	        return "redirect:/student/login";
	    }

	    List<Question> questions =
	            questionService.getAllQuestions();

	    Question currentQuestion =
	            questions.get(index);

	    StudentAnswer savedAnswer =
	            studentAnswerService.getAnswer(
	                    student.getId(),
	                    currentQuestion.getId());

	    String selectedAnswer = "";

	    if(savedAnswer != null){
	        selectedAnswer =
	                savedAnswer.getSelectedAnswer();
	    }

	    model.addAttribute("selectedAnswer",
	            selectedAnswer);

	    model.addAttribute("examId", id);
	    model.addAttribute("questionList", questions);
	    model.addAttribute("currentIndex", index);

	    return "student/start-exam";
	}
	
	@GetMapping("/student/result")
	public String resultPage(Model model) {

	    model.addAttribute("percent", 90);
	    model.addAttribute("status", "PASSED");
	    model.addAttribute("examTitle", "Core Java Fundamentals");

	    model.addAttribute("totalQuestions", 2);
	    model.addAttribute("correctAnswers", 2);
	    model.addAttribute("wrongAnswers", 0);

	    model.addAttribute("score", "20 / 20");

	    model.addAttribute("remarks",
	            "Excellent Performance! Keep it up.");

	    return "student/result";
	}
	
	
	@GetMapping("/student/exam/submit")
	public String testResult(Model model) {

	    model.addAttribute("percent", 90);
	    model.addAttribute("status", "PASSED");
	    model.addAttribute("examTitle", "Core Java Fundamentals");
	    model.addAttribute("totalQuestions", 2);
	    model.addAttribute("correctAnswers", 2);
	    model.addAttribute("wrongAnswers", 0);
	    model.addAttribute("score", "20 / 20");
	    model.addAttribute("remarks", "Excellent Performance!");

	    return "student/result";
	}

	@PostMapping("/student/exam/submit")
	public String submitExamPost(Model model) {

	    System.out.println("POST SUBMIT");

	    model.addAttribute("percent", 90);
	    model.addAttribute("status", "PASSED");
	    model.addAttribute("examTitle", "Core Java Fundamentals");
	    model.addAttribute("totalQuestions", 2);
	    model.addAttribute("correctAnswers", 2);
	    model.addAttribute("wrongAnswers", 0);
	    model.addAttribute("score", "20 / 20");
	    model.addAttribute("remarks", "Excellent Performance!");

	    return "student/result";
	}
	
	@PostMapping("/student/exam/save")
	public String saveAnswer(
	        @RequestParam int examId,
	        @RequestParam int questionId,
	        @RequestParam(required = false) String selectedAnswer,
	        @RequestParam int nextIndex,
	        HttpSession session) {

	    Student student =
	            (Student) session.getAttribute("student");

	    System.out.println("STUDENT = " + student);

	    if(student == null){
	        return "redirect:/student/login";
	    }

	    if(selectedAnswer != null) {

	        StudentAnswer answer = new StudentAnswer();

	        answer.setStudentId(student.getId());
	        answer.setQuestionId(questionId);
	        answer.setSelectedAnswer(selectedAnswer);

	        studentAnswerService.saveAnswer(answer);
	    }

	    return "redirect:/student/exam/start/"
	            + examId + "/" + nextIndex;
	}
	
	
	@PostMapping("/student/exam/previous")
	public String previousQuestion(
	        @RequestParam int examId,
	        @RequestParam int questionId,
	        @RequestParam(required = false) String selectedAnswer,
	        @RequestParam int prevIndex,
	        HttpSession session) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if(student != null && selectedAnswer != null){

	        StudentAnswer answer = new StudentAnswer();

	        answer.setStudentId(student.getId());
	        answer.setQuestionId(questionId);
	        answer.setSelectedAnswer(selectedAnswer);

	        studentAnswerService.saveAnswer(answer);
	    }

	    return "redirect:/student/exam/start/"
	            + examId + "/" + prevIndex;
	}
	
	@GetMapping("/student/profile")
	public String profile(HttpSession session,
	                      Model model) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if(student == null) {
	        return "redirect:/student/login";
	    }

	    model.addAttribute("student", student);

	    return "student/student-profile";
	}
}
	


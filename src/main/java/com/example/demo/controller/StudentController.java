package com.example.demo.controller;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.entity.Exam;
import com.example.demo.entity.Question;
import com.example.demo.entity.Result;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentAnswer;
import com.example.demo.service.ExamService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.ResultService;
import com.example.demo.service.StudentAnswerService;
import com.example.demo.service.StudentService;

import jakarta.servlet.http.HttpSession;



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
	
	@Autowired
	private ResultService resultService;
	
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
		student.setRegisteredDate(java.time.LocalDate.now());
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

	    Student student = (Student) session.getAttribute("student");

	    if(student == null) {
	        return "redirect:/student/login";
	    }

	    model.addAttribute("student", student);

	    long totalExams = examService.getAllExams().size();
	    model.addAttribute("totalExamsCount", totalExams);

	    long attemptedExams =
	            studentAnswerService.getAttemptedExamCount(student.getId());

	    model.addAttribute("attemptedExamsCount", attemptedExams);

	    model.addAttribute("pendingExamsCount",
	            totalExams - attemptedExams);

	    List<Result> recentResults =
	            resultService.getStudentResults(
	                    student.getEmail());

	    model.addAttribute("recentResults", recentResults);

	    if(!recentResults.isEmpty()) {
	        model.addAttribute("latestScorePercent",
	                recentResults.get(0).getPercent());
	    } else {
	        model.addAttribute("latestScorePercent", "0%");
	    }

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

	    // Debug
	    System.out.println("Exam Start Session ID: " + session.getId());

	    Student student = (Student) session.getAttribute("student");

	    System.out.println("Student from Session: " + student);

	    // Check student login
	    if(student == null) {
	        System.out.println("Student not found in session. Redirecting to login...");
	        return "redirect:/student/login";
	    }

	    // Get all questions of exam
	    List<Question> questions = questionService.getQuestionsByExamId(id);

	    // Check questions available or index valid
	    if(questions.isEmpty() || index < 0 || index >= questions.size()) {
	        System.out.println("No questions found or invalid index");
	        return "redirect:/student/exams";
	    }

	    // Get current question
	    Question currentQuestion = questions.get(index);

	    
	    Exam exam = examService.getExamById(id);
	    
	    System.out.println("================================");
	    System.out.println("EXAM ID = " + id);
	    System.out.println("EXAM NAME = " + exam.getExamName());
	    System.out.println("EXAM DURATION = " + exam.getDuration());
	    System.out.println("================================");
	    
	    Long examEndTime =
	            (Long) session.getAttribute("examEndTime_" + id);

	    if (examEndTime == null) {

	        examEndTime =
	                System.currentTimeMillis()
	                + (exam.getDuration() * 60L * 1000L);

	        session.setAttribute(
	                "examEndTime_" + id,
	                examEndTime);
	    }

	    long remainingSeconds =
	            Math.max(
	                    0,
	                    (examEndTime - System.currentTimeMillis()) / 1000
	            );

	    model.addAttribute(
	            "remainingSeconds",
	            remainingSeconds);
	    
	    // Get previously selected answer
	    StudentAnswer savedAnswer =
	            studentAnswerService.getAnswer(
	                student.getId(),
	                currentQuestion.getId(),
	                id
	            );

	    String selectedAnswer = "";

	    if(savedAnswer != null) {
	        selectedAnswer = savedAnswer.getSelectedAnswer();
	    }

	    // Send data to HTML
	    model.addAttribute("examId", id);
	    model.addAttribute("examDurationMinutes", exam.getDuration());
	    model.addAttribute("questionList", questions);
	    model.addAttribute("currentQuestion", currentQuestion);
	    model.addAttribute("currentIndex", index);
	    model.addAttribute("selectedAnswer", selectedAnswer);
	    
	    int progressPercent =
	            ((index + 1) * 100) / questions.size();

	    model.addAttribute("progressPercent", progressPercent);
	    
	    Set<Integer> reviewedQuestions = new HashSet<>();
	    Set<Integer> answeredQuestions = new HashSet<>();

	    for (Question q : questions) {

	        StudentAnswer answer =
	                studentAnswerService.getAnswer(
	                        student.getId(),
	                        q.getId(),
	                        id);

	        if (answer != null) {

	            // Marked for Review
	            if (answer.isMarkedForReview()) {
	                reviewedQuestions.add(q.getId());
	            }

	            // Answered
	            if (answer.getSelectedAnswer() != null
	                    && !answer.getSelectedAnswer().isBlank()) {
	                answeredQuestions.add(q.getId());
	            }
	        }
	    }

	    model.addAttribute("reviewedQuestions", reviewedQuestions);
	    model.addAttribute("answeredQuestions", answeredQuestions);

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
	public String submitRedirect() {

	    return "redirect:/student/dashboard";
	}
	
	@PostMapping("/student/exam/submit")
	public String submitExamPost(
			 @RequestParam int examId,
	        HttpSession session,
	        Model model) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if(student == null) {
	        return "redirect:/student/login";
	    }


	    List<StudentAnswer> answers =
	            studentAnswerService.getAnswersByStudentAndExam(
	                    student.getId(),
	                    examId);
	    
	    List<Question> allQuestions =
	            questionService.getQuestionsByExamId(examId);


	    int totalMarks = 0;
	    int obtainedMarks = 0;
	    int correctAnswers = 0;


	    // Complete exam चे total marks
	    for(Question question : allQuestions) {

	        totalMarks += question.getMarks();
	    }


	    // Student ने दिलेल्या answers check कर
	    for(StudentAnswer answer : answers) {

	        Question question =
	                questionService.getQuestionById(
	                        answer.getQuestionId());

	        if(question != null &&
	           question.getCorrectAnswer() != null &&
	           question.getCorrectAnswer()
	                   .equalsIgnoreCase(
	                           answer.getSelectedAnswer())) {

	            obtainedMarks += question.getMarks();

	            correctAnswers++;
	        }
	    }

	    int totalQuestions = allQuestions.size();
	    int wrongAnswers =
	            totalQuestions - correctAnswers;


	    double percentage =
	            ((double) obtainedMarks / totalMarks) * 100;


	    String status =
	            percentage >= 40 ? "PASSED" : "FAILED";


	    // Save Result in Database

	    Result result = new Result();

	    result.setStudentName(
	            student.getFullName());

	    result.setStudentEmail(
	            student.getEmail());

	    result.setExamName("Online Exam");

	    result.setDate(
	            java.time.LocalDate.now().toString());

	    result.setScoreString(
	            obtainedMarks + " / " + totalMarks);

	    result.setPercent(
	            String.format("%.0f%%", percentage));

	    result.setPassed(
	            percentage >= 40);


	    resultService.saveResult(result);



	    // Send Data To Result Page

	    model.addAttribute(
	            "percent",
	            (int) percentage);

	    model.addAttribute(
	            "status",
	            status);

	    Exam exam =
	            examService.getExamById(examId);

	    model.addAttribute(
	            "examTitle",
	            exam.getExamName());
	    
	    model.addAttribute(
	            "date",
	            java.time.LocalDate.now());

	    model.addAttribute(
	            "totalQuestions",
	            totalQuestions);

	    model.addAttribute(
	            "correctAnswers",
	            correctAnswers);

	    model.addAttribute(
	            "wrongAnswers",
	            wrongAnswers);

	    model.addAttribute(
	            "score",
	            obtainedMarks + " / " + totalMarks);

	    model.addAttribute(
	            "remarks",
	            percentage >= 80 ?
	            "Excellent Performance!" :
	            percentage >= 40 ?
	            "Good Job!" :
	            "Need More Practice");


	    return "student/result";
	}
	
	@GetMapping("/student/exam/begin/{id}")
	public String beginExam(
	        @PathVariable int id,
	        HttpSession session) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if(student == null) {
	        return "redirect:/student/login";
	    }

	    // Clear previous answers
	    studentAnswerService.clearExamAnswers(
	            student.getId(), id);

	    // Get latest exam from database
	    Exam exam =
	            examService.getExamById(id);

	    // Start NEW timer
	    long endTime =
	            System.currentTimeMillis()
	            + (exam.getDuration() * 60L * 1000L);

	    // Save timer end time in session
	    session.setAttribute(
	            "examEndTime_" + id,
	            endTime);

	    return "redirect:/student/exam/start/"
	            + id + "/0";
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
	        answer.setExamId(examId);
	        answer.setSelectedAnswer(selectedAnswer);

	        studentAnswerService.saveAnswer(answer);
	    }

	    return "redirect:/student/exam/start/"
	            + examId + "/" + nextIndex;
	}
	
	
	@PostMapping("/student/exam/mark-review")
	public String markForReview(
	        @RequestParam int examId,
	        @RequestParam int questionId,
	        @RequestParam int currentIndex,
	        @RequestParam(required = false) String selectedAnswer,
	        HttpSession session) {

	    Student student = (Student) session.getAttribute("student");

	    if (student == null) {
	        return "redirect:/student/login";
	    }

	    // First save selected answer
	    if (selectedAnswer != null && !selectedAnswer.isBlank()) {

	        StudentAnswer answer = new StudentAnswer();

	        answer.setStudentId(student.getId());
	        answer.setQuestionId(questionId);
	        answer.setExamId(examId);
	        answer.setSelectedAnswer(selectedAnswer);

	        studentAnswerService.saveAnswer(answer);
	    }

	    // Check current review status
	    StudentAnswer existingAnswer =
	            studentAnswerService.getAnswer(
	                    student.getId(),
	                    questionId,
	                    examId
	            );

	    boolean currentlyMarked = false;

	    if (existingAnswer != null) {
	        currentlyMarked = existingAnswer.isMarkedForReview();
	    }

	    // Toggle: true -> false, false -> true
	    studentAnswerService.markForReview(
	            student.getId(),
	            questionId,
	            examId,
	            !currentlyMarked
	    );

	    return "redirect:/student/exam/start/"
	            + examId + "/"
	            + currentIndex;
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

	    if(student != null && selectedAnswer != null) {

	        StudentAnswer answer = new StudentAnswer();

	        answer.setStudentId(student.getId());
	        answer.setQuestionId(questionId);
	        answer.setExamId(examId);
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
	


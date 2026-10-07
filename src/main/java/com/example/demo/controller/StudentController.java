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
import com.example.demo.entity.ExamAttempt;
import com.example.demo.entity.Question;
import com.example.demo.entity.Result;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentAnswer;
import com.example.demo.service.ExamService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.ResultService;
import com.example.demo.service.StudentAnswerService;
import com.example.demo.service.StudentService;
import com.example.demo.service.ExamAttemptService;
import java.util.Optional;


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
	
	@Autowired
	private ExamAttemptService examAttemptService;
	
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
	            resultService.getAttemptedExamCount(
	                    student.getEmail());

	    model.addAttribute(
	            "attemptedExamsCount",
	            attemptedExams);

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
	public String availableExams(
	        @RequestParam(required = false) String alreadyAttempted,
	        HttpSession session,
	        Model model) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if (student == null) {
	        return "redirect:/student/login";
	    }

	    List<Exam> exams =
	            examService.getAllExams();

	    // Store IDs of exams already completed by this student
	    Set<Integer> completedExamIds = new HashSet<>();

	    for (Exam exam : exams) {

	        Optional<ExamAttempt> attempt =
	                examAttemptService.getAttempt(
	                        student.getId(),
	                        exam.getId()
	                );

	        if (attempt.isPresent()
	                && "COMPLETED".equals(
	                        attempt.get().getStatus())) {

	            completedExamIds.add(exam.getId());
	        }
	    }

	    model.addAttribute(
	            "examList",
	            exams);

	    model.addAttribute(
	            "completedExamIds",
	            completedExamIds);

	    if ("true".equals(alreadyAttempted)) {
	        model.addAttribute(
	                "alreadyAttempted",
	                true);
	    }

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
	
	@GetMapping("/student/result/{id}")
	public String resultDetails(
	        @PathVariable int id,
	        HttpSession session,
	        Model model) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if (student == null) {
	        return "redirect:/student/login";
	    }

	    Result result =
	            resultService.getResultById(id);

	    if (result == null) {
	        return "redirect:/student/results";
	    }

	    model.addAttribute("result", result);

	    return "student/result-details";
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

	    // Get logged-in student
	    Student student =
	            (Student) session.getAttribute("student");

	    if (student == null) {
	        return "redirect:/student/login";
	    }

	    // Get student's answers for this exam
	    List<StudentAnswer> answers =
	            studentAnswerService.getAnswersByStudentAndExam(
	                    student.getId(),
	                    examId);

	    // Get all questions of this exam
	    List<Question> allQuestions =
	            questionService.getQuestionsByExamId(examId);

	    // Get exam details
	    Exam exam =
	            examService.getExamById(examId);

	    // Marks calculation
	    int totalMarks = 0;
	    int obtainedMarks = 0;
	    int correctAnswers = 0;

	    // Calculate total marks
	    for (Question question : allQuestions) {

	        totalMarks += question.getMarks();
	    }

	    // Check student's answers
	    for (StudentAnswer answer : answers) {

	        Question question =
	                questionService.getQuestionById(
	                        answer.getQuestionId());

	        if (question != null
	                && question.getCorrectAnswer() != null
	                && answer.getSelectedAnswer() != null
	                && question.getCorrectAnswer()
	                        .equalsIgnoreCase(
	                                answer.getSelectedAnswer())) {

	            obtainedMarks += question.getMarks();

	            correctAnswers++;
	        }
	    }

	    // Total questions
	    int totalQuestions = allQuestions.size();

	    int attemptedAnswers = answers.size();

	    int wrongAnswers =
	            attemptedAnswers - correctAnswers;

	    int unansweredAnswers =
	            totalQuestions - attemptedAnswers;
	    
	    // Percentage
	    double percentage = 0;

	    if (totalMarks > 0) {

	        percentage =
	                ((double) obtainedMarks / totalMarks) * 100;
	    }

	    // Fixed passing criteria = 40%
	    int passingMarks =
	            (int) Math.ceil(totalMarks * 0.40);

	    // Pass / Fail status
	    String status =
	            obtainedMarks >= passingMarks
	            ? "PASSED"
	            : "FAILED";


	    // ==============================
	    // Save Result in Database
	    // ==============================

	    Result result =
	            new Result();

	    result.setStudentName(
	            student.getFullName());

	    result.setStudentEmail(
	            student.getEmail());

	    result.setExamName(
	            exam.getExamName());

	    result.setDate(
	            java.time.LocalDate.now().toString());

	    result.setScoreString(
	            obtainedMarks + " / " + totalMarks);

	    result.setPercent(
	            String.format(
	                    "%.0f%%",
	                    percentage));

	    result.setPassed(
	            obtainedMarks >= passingMarks);

	    resultService.saveResult(result);

	    
	 // Mark exam attempt as COMPLETED
	    Optional<ExamAttempt> existingAttempt =
	            examAttemptService.getAttempt(
	                    student.getId(),
	                    examId
	            );

	    if (existingAttempt.isPresent()) {
	        examAttemptService.completeAttempt(
	                existingAttempt.get()
	        );
	    }

	    // ==============================
	    // Send Data To Result Page
	    // ==============================

	    model.addAttribute(
	            "percent",
	            (int) percentage);

	    model.addAttribute(
	            "status",
	            status);

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
	            "attemptedAnswers",
	            attemptedAnswers);

	    model.addAttribute(
	            "unansweredAnswers",
	            unansweredAnswers);

	    model.addAttribute(
	            "score",
	            obtainedMarks + " / " + totalMarks);

	    model.addAttribute(
	            "remarks",
	            percentage >= 80
	            ? "Excellent Performance!"
	            : percentage >= 40
	            ? "Good Job!"
	            : "Need More Practice");


	    return "student/result";
	}
	
	@GetMapping("/student/exam/begin/{id}")
	public String beginExam(
	        @PathVariable int id,
	        HttpSession session) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if (student == null) {
	        return "redirect:/student/login";
	    }

	    // Get exam
	    Exam exam = examService.getExamById(id);

	    if (exam == null) {
	        return "redirect:/student/exams";
	    }

	    // Check existing attempt
	    Optional<ExamAttempt> existingAttempt =
	            examAttemptService.getAttempt(
	                    student.getId(),
	                    id
	            );

	    // Already completed
	    if (existingAttempt.isPresent()
	            && "COMPLETED".equals(
	                    existingAttempt.get().getStatus())) {

	    	return "redirect:/student/exams?alreadyAttempted=true";
	    }

	    // Existing exam is still in progress
	    if (existingAttempt.isPresent()
	            && "IN_PROGRESS".equals(
	                    existingAttempt.get().getStatus())) {

	        // Use old timer
	        session.setAttribute(
	                "examEndTime_" + id,
	                existingAttempt.get().getEndTime()
	        );

	        // Resume exam
	        return "redirect:/student/exam/start/"
	                + id + "/0";
	    }
	    
	 // =========================
	 // RETAKE ALLOWED
	 // =========================
	 if (existingAttempt.isPresent()
	         && "RETAKE_ALLOWED".equals(
	                 existingAttempt.get().getStatus())) {

	     long startTime =
	             System.currentTimeMillis();

	     long endTime =
	             startTime
	             + (exam.getDuration() * 60L * 1000L);

	     // Start retake
	     examAttemptService.startRetake(
	             existingAttempt.get(),
	             startTime,
	             endTime
	     );

	     // Save new timer
	     session.setAttribute(
	             "examEndTime_" + id,
	             endTime
	     );

	     // Clear previous attempt answers
	     studentAnswerService.clearExamAnswers(
	             student.getId(),
	             id
	     );

	     // Start exam again
	     return "redirect:/student/exam/start/"
	             + id + "/0";
	 }
	    
	    

	    // =========================
	    // NEW EXAM ATTEMPT
	    // =========================

	    long startTime =
	            System.currentTimeMillis();

	    long endTime =
	            startTime
	            + (exam.getDuration() * 60L * 1000L);

	    // Create new attempt
	    examAttemptService.createAttempt(
	            student.getId(),
	            id,
	            startTime,
	            endTime
	    );

	    // Save timer
	    session.setAttribute(
	            "examEndTime_" + id,
	            endTime
	    );

	    // Clear old answers ONLY for new attempt
	    studentAnswerService.clearExamAnswers(
	            student.getId(),
	            id
	    );

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
	
	
	
	
	// RESULT HISTORY
	@GetMapping("/student/results")
	public String resultHistory(
	        HttpSession session,
	        Model model) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if(student == null) {
	        return "redirect:/student/login";
	    }

	    List<Result> results =
	            resultService.getStudentResults(
	                    student.getEmail());

	    model.addAttribute("results", results);

	    return "student/result-history";
	}
	
	@PostMapping("/student/profile/update")
	public String updateProfile(
	        @RequestParam String fullName,
	        @RequestParam String mobileNumber,
	        @RequestParam String department,
	        HttpSession session,
	        Model model) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if (student == null) {
	        return "redirect:/student/login";
	    }

	    boolean updated =
	            studentService.updateStudentProfile(
	                    student.getId(),
	                    fullName,
	                    mobileNumber,
	                    department);

	    if (updated) {

	        student.setFullName(fullName);
	        student.setMobileNumber(mobileNumber);
	        student.setDepartment(department);

	        session.setAttribute("student", student);

	        model.addAttribute(
	                "infoSuccess",
	                true);
	    }

	    model.addAttribute("student", student);

	    return "student/student-profile";
	}
	
	@GetMapping("/student/profile")
	public String profile(HttpSession session, Model model) {

	    Student student = (Student) session.getAttribute("student");

	    if(student == null) {
	        return "redirect:/student/login";
	    }

	    // Student information
	    model.addAttribute("student", student);

	    // Student exam history
	    List<Result> results =
	            resultService.getStudentResults(student.getEmail());

	    model.addAttribute("results", results);

	    return "student/student-profile";
	}
	
	@PostMapping("/student/profile/password")
	public String changePassword(
	        @RequestParam String currentPassword,
	        @RequestParam String newPassword,
	        @RequestParam String confirmNewPassword,
	        HttpSession session,
	        Model model) {

	    Student student =
	            (Student) session.getAttribute("student");

	    if (student == null) {
	        return "redirect:/student/login";
	    }

	    String result = studentService.changeStudentPassword(
	            student.getId(),
	            currentPassword,
	            newPassword,
	            confirmNewPassword
	    );

	    if ("SUCCESS".equals(result)) {
	        model.addAttribute("pwdSuccess",
	                "Password changed successfully!");
	    } else {
	        model.addAttribute("pwdError", result);
	    }

	    model.addAttribute("student", student);
	    
	    List<Result> results =
	            resultService.getStudentResults(student.getEmail());

	    model.addAttribute("results", results);

	    return "student/student-profile";
	}
}
	


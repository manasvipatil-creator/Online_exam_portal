package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Result {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String studentName;
    private String studentEmail;
    private String examName;
    private String date;
    private String scoreString;
    private String percent;
    private boolean passed;

    
    
    public Result() {
    	
    }
	
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getStudentEmail() {
		return studentEmail;
	}
	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getScoreString() {
		return scoreString;
	}
	public void setScoreString(String scoreString) {
		this.scoreString = scoreString;
	}
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public boolean isPassed() {
		return passed;
	}
	public void setPassed(boolean passed) {
		this.passed = passed;
	}
	

}

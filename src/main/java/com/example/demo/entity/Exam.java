package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Exam {
	
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String examName;
	private String subject;
	private int duration;
	private int totalMarks;
	private int passingScore;
	private String description;

	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(int totalMarks) {
		this.totalMarks = totalMarks;
	}
	
	public int getPassingScore() {
	    return passingScore;
	}

	public void setPassingScore(int passingScore) {
	    this.passingScore = passingScore;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}
}

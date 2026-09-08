package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.demo.entity.Question;
import com.example.demo.repository.QuestionRepository;

@Service
public class QuestionService {
	
	
	
	@Autowired
	public QuestionRepository questionRepository;
	
	public boolean addQuestion(Question question) {
		
		try {
			questionRepository.save(question);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Question>getAllQuestions(){
		return questionRepository.findAll();
	}
	
	public Question getQuestionById(int id) {

	    return questionRepository.findById(id).orElse(null);
	}
	
	public List<Question> getQuestionsByExamId(int examId) {
	    return questionRepository.findByExamId(examId);
	}
	
	public long getQuestionCountByExamId(int examId) {
	    return questionRepository.countByExamId(examId);
	}
	public void updateQuestion(Question question) {
	    questionRepository.save(question);
	}
	
	public void deleteQuestion(int id) {
	    questionRepository.deleteById(id);
	}

}

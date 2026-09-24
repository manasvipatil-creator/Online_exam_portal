package com.example.demo.service;

import java.util.List;
import com.example.demo.repository.ResultRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Result;

@Service
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }
    public List<Result> getStudentResults(String email) {
        return resultRepository.findByStudentEmailOrderByIdDesc(email);
    }
    
    public List<String> getAllExamNames() {

        List<Result> results = resultRepository.findAll();

        return results.stream()
                .map(Result::getExamName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .toList();
    }
    
    public void saveResult(Result result) {

        resultRepository.save(result);
    }
    
    public long getTotalResults() {
        return resultRepository.count();
    }
    
    public double getAveragePercentage() {

        List<Result> results = resultRepository.findAll();

        if (results.isEmpty()) {
            return 0;
        }

        double total = 0;

        for (Result result : results) {

            String percent = result.getPercent();

            if (percent != null && !percent.isBlank()) {

                percent = percent.replace("%", "").trim();

                total += Double.parseDouble(percent);
            }
        }

        return total / results.size();
    }
    
    public double getPassingRatio() {

        List<Result> results = resultRepository.findAll();

        if (results.isEmpty()) {
            return 0;
        }

        long passedCount = 0;

        for (Result result : results) {

            if (result.isPassed()) {
                passedCount++;
            }
        }

        return (passedCount * 100.0) / results.size();
    }
    
    
    public double getHighestScore() {

        List<Result> results = resultRepository.findAll();

        if (results.isEmpty()) {
            return 0;
        }

        double highestScore = 0;

        for (Result result : results) {

            String score = result.getScoreString();

            if (score != null && !score.isBlank()) {

                try {
                    String numericScore = score.split("/")[0].trim();

                    double currentScore =
                            Double.parseDouble(numericScore);

                    if (currentScore > highestScore) {
                        highestScore = currentScore;
                    }

                } catch (Exception e) {
                    System.out.println(
                        "Invalid score: " + score
                    );
                }
            }
        }

        return highestScore;
    }
    

}

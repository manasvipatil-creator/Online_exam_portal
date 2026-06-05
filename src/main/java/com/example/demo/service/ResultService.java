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


}

package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Admin;
import com.example.demo.repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository repository;

    public Admin loginAdmin(
            String email,
            String password) {

        return repository.findByEmailAndPassword(
                email,
                password);
    }
}
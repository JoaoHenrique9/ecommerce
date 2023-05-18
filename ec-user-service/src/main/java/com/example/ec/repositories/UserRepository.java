package com.example.ec.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ec.models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, UUID> {

    UserModel findByEmail(String email);
}
package com.example.ec.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ec.models.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {

}

package com.ibm.quarkus.academy.ram.repository;

import com.ibm.quarkus.academy.ram.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
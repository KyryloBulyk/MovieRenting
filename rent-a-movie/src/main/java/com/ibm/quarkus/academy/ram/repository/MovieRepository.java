package com.ibm.quarkus.academy.ram.repository;

import com.ibm.quarkus.academy.ram.model.Movie;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class MovieRepository implements PanacheRepositoryBase<Movie, Integer> {

    @Inject
    EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
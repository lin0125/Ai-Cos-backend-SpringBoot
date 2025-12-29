package com.example.aicosbackendspringboot.JWT.repository;

import com.example.aicosbackendspringboot.JWT.entities.FieldEntity;
import com.example.aicosbackendspringboot.JWT.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface FieldRepository extends JpaRepository<FieldEntity, String> {

    Optional<FieldEntity> getFieldEntityByBuildFieldEmailAndFieldName(UserEntity buildFieldEmail, String fieldName);
}

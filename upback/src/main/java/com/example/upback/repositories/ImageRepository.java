package com.example.upback.repositories;

import com.example.upback.models.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageModel, UUID> {
}

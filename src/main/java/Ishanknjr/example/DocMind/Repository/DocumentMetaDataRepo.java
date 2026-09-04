package Ishanknjr.example.DocMind.Repository;

import Ishanknjr.example.DocMind.DTO.DocumentMetaDataDto;
import Ishanknjr.example.DocMind.Entity.DocumentMetaData;
import Ishanknjr.example.DocMind.Enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentMetaDataRepo extends JpaRepository<DocumentMetaData, UUID> {

    List<DocumentMetaData> findByStatus(DocumentStatus status);
    List<DocumentMetaData> findAllByOrderByCreatedAtDesc();
}


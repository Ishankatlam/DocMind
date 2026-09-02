package Ishanknjr.example.DocMind.Repository;

import Ishanknjr.example.DocMind.DTO.DocumentMetaDataDto;
import Ishanknjr.example.DocMind.Entity.DocumentMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentMetaDataRepo extends JpaRepository<DocumentMetaData, UUID> {

}


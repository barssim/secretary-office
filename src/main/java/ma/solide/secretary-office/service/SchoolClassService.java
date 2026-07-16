package ma.solide.attestationservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ma.solide.attestationservice.dto.SchoolClassResponse;
import ma.solide.attestationservice.model.SchoolClass;
import ma.solide.attestationservice.repository.SchoolClassRepository;

@Service
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;

    public SchoolClassService(SchoolClassRepository schoolClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
    }

    public List<SchoolClassResponse> getClasses() {
        return schoolClassRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SchoolClassResponse toResponse(SchoolClass schoolClass) {
        return SchoolClassResponse.builder()
                .id(schoolClass.getId())
                .name(schoolClass.getName())
                .students(schoolClass.getStudents())
                .build();
    }
}



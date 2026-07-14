package ma.solide.attestationservice.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import ma.solide.attestationservice.dto.AttestationResponse;
import ma.solide.attestationservice.model.Attestation;
import ma.solide.attestationservice.repository.AttestationRepository;

@Service
public class AttestationService {

    private final AttestationRepository attestationRepository;

    public AttestationService(AttestationRepository attestationRepository) {
        this.attestationRepository = attestationRepository;
    }

    public List<AttestationResponse> getAttestations(Integer userId, String search) {
        List<Attestation> attestations;
        boolean hasSearch = StringUtils.hasText(search);

        if (userId != null && hasSearch) {
            attestations = attestationRepository.findByUserIdAndTitleContainingIgnoreCaseOrderByDateDesc(userId, search.trim());
        } else if (userId != null) {
            attestations = attestationRepository.findByUserIdOrderByDateDesc(userId);
        } else if (hasSearch) {
            attestations = attestationRepository.findByTitleContainingIgnoreCaseOrderByDateDesc(search.trim());
        } else {
            attestations = attestationRepository.findAllByOrderByDateDesc();
        }

        return attestations.stream().map(this::toResponse).toList();
    }

    public AttestationResponse getAttestation(Integer id) {
        return toResponse(findEntity(id));
    }

    public Attestation findEntity(Integer id) {
        return attestationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Attestation introuvable pour l'id " + id));
    }

    private AttestationResponse toResponse(Attestation attestation) {
        return AttestationResponse.builder()
                .id(attestation.getId())
                .userId(attestation.getUserId())
                .studentName(attestation.getStudentName())
                .className(attestation.getClassName())
                .title(attestation.getTitle())
                .type(attestation.getType())
                .date(attestation.getDate())
                .status(attestation.getStatus())
                .documentUrl(resolveDocumentUrl(attestation.getId(), "download"))
                .viewUrl(resolveDocumentUrl(attestation.getId(), "view"))
                .issuedBy(attestation.getIssuedBy())
                .validFrom(attestation.getValidFrom())
                .validUntil(attestation.getValidUntil())
                .reference(attestation.getReference())
                .build();
    }

    private String resolveDocumentUrl(Integer id, String action) {
        return "/api/attestations/" + id + "/" + action;
    }
}

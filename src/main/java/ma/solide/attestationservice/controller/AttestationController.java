package ma.solide.attestationservice.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ma.solide.attestationservice.dto.AttestationResponse;
import ma.solide.attestationservice.model.Attestation;
import ma.solide.attestationservice.service.AttestationPdfService;
import ma.solide.attestationservice.service.AttestationService;

@RestController
@RequestMapping({"/api/attestations", "/api/attestationsproduction"})
public class AttestationController {

    private final AttestationService attestationService;
    private final AttestationPdfService attestationPdfService;

    public AttestationController(AttestationService attestationService, AttestationPdfService attestationPdfService) {
        this.attestationService = attestationService;
        this.attestationPdfService = attestationPdfService;
    }

    @GetMapping
    public ResponseEntity<List<AttestationResponse>> getAttestations(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(attestationService.getAttestations(userId, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttestationResponse> getAttestation(@PathVariable Integer id) {
        return ResponseEntity.ok(attestationService.getAttestation(id));
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<InputStreamResource> viewAttestation(@PathVariable Integer id) {
        return buildPdfResponse(id, true);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadAttestation(@PathVariable Integer id) {
        return buildPdfResponse(id, false);
    }

    private ResponseEntity<InputStreamResource> buildPdfResponse(Integer id, boolean inline) {
        Attestation attestation = attestationService.findEntity(id);
        ByteArrayInputStream pdf = attestationPdfService.generatePdf(attestation);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                (inline ? "inline" : "attachment") + "; filename=attestation-" + attestation.getId() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}


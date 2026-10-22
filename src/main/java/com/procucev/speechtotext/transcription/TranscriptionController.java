package com.procucev.speechtotext.transcription;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transcriptions")
@CrossOrigin
public class TranscriptionController {
    private final TranscriptionService service;

    public TranscriptionController(TranscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public List<TranscriptionResponse> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TranscriptionResponse byId(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TranscriptionResponse create(@Valid @RequestBody TranscriptionRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public TranscriptionResponse update(@PathVariable UUID id,
                                        @Valid @RequestBody TranscriptionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

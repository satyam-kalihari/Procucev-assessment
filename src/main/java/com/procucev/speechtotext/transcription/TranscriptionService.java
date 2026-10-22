package com.procucev.speechtotext.transcription;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TranscriptionService {
    private final TranscriptionRepository repository;

    public TranscriptionService(TranscriptionRepository repository) {
        this.repository = repository;
    }

    public List<TranscriptionResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"))
                .stream().map(TranscriptionResponse::from).toList();
    }

    public TranscriptionResponse findById(UUID id) {
        return TranscriptionResponse.from(get(id));
    }

    public TranscriptionResponse create(TranscriptionRequest request) {
        String language = request.language() == null || request.language().isBlank()
                ? "en-IN" : request.language();
        return TranscriptionResponse.from(repository.save(
                new Transcription(request.title().trim(), request.content().trim(), language)));
    }

    public TranscriptionResponse update(UUID id, TranscriptionRequest request) {
        Transcription transcription = get(id);
        String language = request.language() == null || request.language().isBlank()
                ? transcription.getLanguage() : request.language();
        transcription.update(request.title().trim(), request.content().trim(), language);
        return TranscriptionResponse.from(repository.save(transcription));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw notFound(id);
        }
        repository.deleteById(id);
    }

    private Transcription get(UUID id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(UUID id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Transcription not found: " + id);
    }
}

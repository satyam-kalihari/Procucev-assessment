package com.procucev.speechtotext.transcription;

import java.time.Instant;
import java.util.UUID;

public record TranscriptionResponse(
        UUID id,
        String title,
        String content,
        String language,
        Instant createdAt,
        Instant updatedAt
) {
    public static TranscriptionResponse from(Transcription transcription) {
        return new TranscriptionResponse(
                transcription.getId(),
                transcription.getTitle(),
                transcription.getContent(),
                transcription.getLanguage(),
                transcription.getCreatedAt(),
                transcription.getUpdatedAt()
        );
    }
}

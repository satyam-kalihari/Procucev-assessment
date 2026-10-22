package com.procucev.speechtotext.transcription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TranscriptionRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank String content,
        @Size(max = 20) String language
) {
}

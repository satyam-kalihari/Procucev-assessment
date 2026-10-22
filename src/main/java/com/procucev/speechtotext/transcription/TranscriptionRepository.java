package com.procucev.speechtotext.transcription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TranscriptionRepository extends JpaRepository<Transcription, UUID> {
}

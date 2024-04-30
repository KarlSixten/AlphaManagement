package org.example.alphamanagement.service;

import org.example.alphamanagement.repository.AlphaRepository;
import org.springframework.stereotype.Service;

@Service
public class AlphaService {
    private final AlphaRepository alphaRepository;

    public AlphaService(AlphaRepository alphaRepository) {
        this.alphaRepository = alphaRepository;
    }
}

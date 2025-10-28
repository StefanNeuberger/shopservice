package service;

import lombok.RequiredArgsConstructor;
import repository.IdGeneratorRepository;

@RequiredArgsConstructor
public class IdGeneratorService {

    private final IdGeneratorRepository idGeneratorRepository;

    public String generateId() {
        return idGeneratorRepository.generateId();
    }
}

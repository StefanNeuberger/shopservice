package repository;

public class StringIdGeneratorRepo implements IdGeneratorRepository {

    @Override
    public String generateId() {
        return java.util.UUID.randomUUID().toString();
    }
    
}

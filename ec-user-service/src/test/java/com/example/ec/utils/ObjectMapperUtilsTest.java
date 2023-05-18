package com.example.ec.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.Setter;

public class ObjectMapperUtilsTest {

    @Test
    public void shouldMapSingleObject() {
        // Crie um objeto de origem
        SourceEntity source = new SourceEntity();
        source.setId(1);
        source.setName("John Doe");

        // Mapeie o objeto de origem para o objeto de destino usando ObjectMapperUtils
        DestinationEntity destination = ObjectMapperUtils.map(source, DestinationEntity.class);

        // Verifique se os valores foram mapeados corretamente
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
    }

    @Test
    public void shouldMapListOfObjects() {
        // Crie uma lista de objetos de origem
        List<SourceEntity> sourceList = new ArrayList<>();
        SourceEntity source1 = new SourceEntity();
        source1.setId(1);
        source1.setName("John Doe");
        SourceEntity source2 = new SourceEntity();
        source2.setId(2);
        source2.setName("Jane Smith");
        sourceList.add(source1);
        sourceList.add(source2);

        // Mapeie a lista de objetos de origem para a lista de objetos de destino usando
        // ObjectMapperUtils
        List<DestinationEntity> destinationList = ObjectMapperUtils.mapAll(sourceList, DestinationEntity.class);

        // Verifique se os valores foram mapeados corretamente para cada objeto da lista
        assertEquals(source1.getId(), destinationList.get(0).getId());
        assertEquals(source1.getName(), destinationList.get(0).getName());
        assertEquals(source2.getId(), destinationList.get(1).getId());
        assertEquals(source2.getName(), destinationList.get(1).getName());
    }

    // Classe de exemplo para teste
    @Getter
    @Setter
    private static class SourceEntity {
        private int id;
        private String name;

    }

    // Classe de exemplo para teste
    @Getter
    @Setter
    private static class DestinationEntity {
        private int id;
        private String name;

    }

}

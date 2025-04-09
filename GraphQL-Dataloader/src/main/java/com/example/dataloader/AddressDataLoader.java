package com.example.dataloader;

import com.example.dto.AddressDTO;
import com.example.response.AddressResponse;
import org.dataloader.MappedBatchLoader;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Component
public class AddressDataLoader implements MappedBatchLoader<Long, AddressResponse> {

    @Override
    public CompletionStage<Map<Long, AddressResponse>> load(Set<Long> studentIds) {
        // Simula fetch em batch (poderia vir de repositório, etc.)
        Map<Long, AddressResponse> addressMap = Map.of(
                1L, new AddressResponse(new AddressDTO(1L, "Rua das Flores", "Lisboa")),
                2L, new AddressResponse(new AddressDTO(2L, "Avenida Central", "Porto"))
        );

        // Apenas retorna os que foram solicitados
        Map<Long, AddressResponse> result = addressMap.entrySet().stream()
                .filter(e -> studentIds.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return CompletableFuture.completedFuture(result);
    }
}
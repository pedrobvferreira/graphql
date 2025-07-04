package com.example.dataloader;

import com.example.dto.AddressDTO;
import com.example.response.AddressResponse;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.MappedBatchLoader;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AddressDataLoader implements MappedBatchLoader<Long, AddressResponse> {

    /**
     * 4) BatchLoader executa o acesso real:
     * O metodo é chamado uma única vez com todos os IDs juntos, resolvendo o problema N+1.
     **/
    @Override
    public CompletionStage<Map<Long, AddressResponse>> load(Set<Long> studentIds) {
        // Poderia ser uma query real: SELECT * FROM address WHERE student_id IN (...)
        // Simula fetch em batch (poderia vir de repositório, etc.)
        log.info(Thread.currentThread().getName());
        Map<Long, AddressResponse> addressMap = Map.of(
                1L, new AddressResponse(new AddressDTO(1L, "Rua das Flores", "Lisboa")),
                2L, new AddressResponse(new AddressDTO(2L, "Avenida Central", "Porto"))
        );

        //5) Resultado é mapeado e entregue
        // Apenas retorna os que foram solicitados
        Map<Long, AddressResponse> result = addressMap.entrySet().stream()
                .filter(e -> studentIds.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return CompletableFuture.completedFuture(result);
    }
}
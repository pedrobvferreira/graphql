package com.example.comp;

import com.example.dataloader.AddressDataLoader;
import com.example.dataloader.SubjectDataLoader;
import lombok.AllArgsConstructor;
import org.dataloader.DataLoaderRegistry;
import org.dataloader.DataLoaderFactory;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class StudentDataLoaderInterceptor implements WebGraphQlInterceptor {

    private final AddressDataLoader addressDataLoader;
    private final SubjectDataLoader subjectDataLoader;

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        // Cria DataLoaderRegistry por requisição
        DataLoaderRegistry registry = new DataLoaderRegistry();
        registry.register("addressDataLoader", DataLoaderFactory.newMappedDataLoader(addressDataLoader));
        registry.register("subjectDataLoader", DataLoaderFactory.newMappedDataLoader(subjectDataLoader));

        request.configureExecutionInput((executionInput, builder) ->
                builder.dataLoaderRegistry(registry).build()
        );

        return chain.next(request);
    }
}

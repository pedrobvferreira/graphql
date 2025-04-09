package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@SpringBootApplication
public class GraphQLApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphQLApplication.class, args);
	}

	@Controller
	static class SimpleTestController {
		@QueryMapping
		public String hello() {
			return "GraphQL está funcionando!";
		}
	}

}

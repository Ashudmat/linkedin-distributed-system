package com.codingshuttle.linkedin.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"jwt.secretKey=abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijkl",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false"
})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}

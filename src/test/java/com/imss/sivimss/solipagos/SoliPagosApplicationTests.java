package com.imss.sivimss.solipagos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.imss.sivimss.solipagos.SoliPagosApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SoliPagosApplicationTests {

	@Test
	void contextLoads() {
		String result="test";
		SoliPagosApplication.main(new String[]{});
		assertNotNull(result);
	}

}

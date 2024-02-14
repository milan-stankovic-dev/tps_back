package rs.ac.bg.fon.tps_backend;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class TpsBackendApplicationTests {
	final Calculator underTest = new Calculator();
	@Test
	void contextLoads() {
		// given
		int numberOne = 20;
		int numberTwo = 30;

		// when
		int result = underTest.add(numberOne, numberTwo);

		// then
		Assertions.assertThat(result).isEqualTo(50);
	}

	class Calculator {
		int add(int a, int b){
			return a+b;
		}
	}

}

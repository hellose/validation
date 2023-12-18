package spring.validation.web.validation;

import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import spring.validation.domain.item.Item;

/*
 * Java Bean Validation test
 */
public class BeanValidationTest {

	@Test
	void beanValidation() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		Validator validator = validatorFactory.getValidator();

		Item item = new Item();
		item.setItemName(" ");
		item.setPrice(999);
		item.setQuantity(10000);

		Set<ConstraintViolation<Item>> violations = validator.validate(item);
		for (ConstraintViolation<Item> violation : violations) {
			System.out.println("violation: " + violation);
			System.out.println("violation.getMessage(): " + violation.getMessage());
		}
	}

}

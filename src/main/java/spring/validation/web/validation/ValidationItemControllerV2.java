package spring.validation.web.validation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spring.validation.domain.item.Item;
import spring.validation.domain.item.ItemRepository;

@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV2 {

	private final ItemRepository itemRepository;

	@GetMapping
	public String items(Model model) {
		List<Item> items = itemRepository.findAll();
		model.addAttribute("items", items);
		return "validation/v2/items";
	}

	@GetMapping("/{itemId}")
	public String item(@PathVariable(name = "itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "validation/v2/item";
	}

	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("item", new Item());
		return "validation/v2/addForm";
	}

	// BindingResult 파라메터 선언 유무

	// 선언 X: 컨트롤러가 호출되지 않고 400 응답 코드에 해당하는 에러 메세지
	// 선언 O: 컨트롤러가 호출되며 에러 정보는 BindingResult에 담긴다.
	@PostMapping("/add")
	public String addItem(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		// BindingResult는 Model에 자동으로 포함돼서 넘어간다.

		if (!StringUtils.hasText(item.getItemName())) {
			bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
		}
		if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
			bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
		}
		if (item.getQuantity() == null || item.getQuantity() > 9999) {
			bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
		}

		// 특정 필드가 아닌 복합 룰 검증
		if (item.getPrice() != null && item.getQuantity() != null) {
			int resultPrice = item.getPrice() * item.getQuantity();
			if (resultPrice < 10000) {
				bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
			}
		}

		if (bindingResult.hasErrors()) {
			log.debug("bindingResult: {}", bindingResult);
			return "validation/v2/addForm";
		}

		Item savedItem = itemRepository.save(item);
		redirectAttributes.addAttribute("itemId", savedItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/validation/v2/items/{itemId}";
	}

	@GetMapping("/{itemId}/edit")
	public String editForm(@PathVariable(name = "itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "validation/v2/editForm";
	}

	@PostMapping("/{itemId}/edit")
	public String edit(@PathVariable(name = "itemId") Long itemId, @ModelAttribute Item item) {
		itemRepository.update(itemId, item);
		return "redirect:/validation/v2/items/{itemId}";
	}

}

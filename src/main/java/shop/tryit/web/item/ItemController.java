package shop.tryit.web.item;

import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.tryit.domain.item.Category;
import shop.tryit.domain.item.Image;
import shop.tryit.domain.item.ImageService;
import shop.tryit.domain.item.Item;
import shop.tryit.domain.item.ItemService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ImageService imageService;

    @GetMapping("/new")
    public String newItemForm(Model model) {
        Category[] categories = Category.values();

        model.addAttribute("item", ItemFormDto.builder().build());
        model.addAttribute("categories", categories);

        return "/items/register";
    }

    @PostMapping("/new")
    public String newItem(@Valid @ModelAttribute("item") ItemFormDto form, BindingResult bindingResult, Model model) throws IOException {
        log.info("======== 상품 등록 컨트롤러 실행 ========");

        // 검증 실패시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            Category[] categories = Category.values();
            model.addAttribute("categories", categories);
            return "/items/register";
        }

        Item item = imageService.addImage(ItemAdapter.toEntity(form), form);
        itemService.register(item);

        log.info("상품 등록 : {}, {}원", item.getName(), item.getPrice());
        log.info("======== 상품 등록 컨트롤러 종료 ========");

        return "redirect:/items";
    }

    @GetMapping
    public String list(Model model) throws IOException {
        List<Item> items = itemService.findItems();

        List<Image> mainImages = imageService.findMainImages();

        List<ItemListDto> itemListDto = ItemAdapter.toDto(items, mainImages);

        model.addAttribute("items", itemListDto);

        return "/items/list";
    }

    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable long id, Model model) {
        Category[] categories = Category.values();
        model.addAttribute("categories", categories);

        Item item = itemService.findOne(id);
        ItemDto itemDto = ItemAdapter.toDto(item, imageService.getMainImage(id), imageService.getDetailImage(id));
        model.addAttribute("item", itemDto);

        return "/items/update";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable long id,
                         @Valid @ModelAttribute("item") ItemFormDto form,
                         BindingResult bindingResult,
                         Model model) throws IOException {
        log.info("======== 상품 수정 컨트롤러 실행 ========");

        // 검증 실패시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            Category[] categories = Category.values();
            model.addAttribute("categories", categories);
            return "/items/update";
        }

        Item newItem = ItemAdapter.toEntity(form);

        itemService.update(id, newItem);
        imageService.updateMainImage(id, form);
        imageService.updateDetailImage(id, form);

        log.info("======== 상품 수정 컨트롤러 종료 ========");

        return "redirect:/items";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable long id, Model model) {
        Item item = itemService.findOne(id);

        ItemDto itemDto = ItemAdapter.toDto(item, imageService.getMainImage(id), imageService.getDetailImage(id));
        model.addAttribute("item", itemDto);

        return "/items/detail";
    }

}

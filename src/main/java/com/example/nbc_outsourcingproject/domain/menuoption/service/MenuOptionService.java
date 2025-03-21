package com.example.nbc_outsourcingproject.domain.menuoption.service;

import com.example.nbc_outsourcingproject.domain.menu.entity.Menu;
import com.example.nbc_outsourcingproject.domain.menu.repository.MenuRepository;
import com.example.nbc_outsourcingproject.domain.menuoption.dto.MenuOptionResponse;
import com.example.nbc_outsourcingproject.domain.menuoption.entity.MenuOption;
import com.example.nbc_outsourcingproject.domain.menuoption.repository.MenuOptionRepository;
import com.example.nbc_outsourcingproject.global.cache.MyStoreCache;
import com.example.nbc_outsourcingproject.global.exception.menu.InvalidStoreMenuException;
import com.example.nbc_outsourcingproject.global.exception.menu.MenuAlreadyDeletedException;
import com.example.nbc_outsourcingproject.global.exception.menu.MenuNotFoundException;
import com.example.nbc_outsourcingproject.global.exception.menu.MenuOptionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuOptionService {

    private final MenuRepository menuRepository;
    private final MenuOptionRepository optionRepository;
    private final MyStoreCache myStoreCache;

    //TODO: 구현 후 시간이 남으면 component service 생성하여 다른 entity 가져오기
    @Transactional
    public void createOption(Long userId, Long menuId, String text, Integer price) {
        Menu menu = validateMenuOfStore(userId, menuId);
        MenuOption menuOption = new MenuOption(menu, text, price);
        optionRepository.save(menuOption);
    }

    @Transactional(readOnly = true)
    public List<MenuOptionResponse> getOptions(Long userId, Long menuId, Long optionId) {
        validateMenuOfStore(userId, menuId);
        if (optionId == null) {
            List<MenuOption> menuOptionList = optionRepository.findByMenuId(menuId);
            return menuOptionList.stream().map(MenuOptionResponse::from).toList();
        }
        return Collections.singletonList(getOption(menuId, optionId));
    }


    @Transactional
    public MenuOptionResponse updateOption(Long userId, Long menuId, Long optionId, String text, Integer price) {
        Menu menu = validateMenuOfStore(userId, menuId);

        MenuOption option = optionRepository.findById(optionId).orElseThrow(MenuOptionNotFoundException::new);
        MenuOption updateOption = modifiedOption(option, text, price);

        return MenuOptionResponse.of(menu.getName(), updateOption.getText(), updateOption.getPrice());
    }

    @Transactional
    public void deleteOption(Long userId, Long menuId, Long optionId) {
        validateMenuOfStore(userId, menuId);
        if (!optionRepository.existsById(optionId)) {
            throw new MenuOptionNotFoundException();
        }
        optionRepository.deleteById(optionId);
    }


    private MenuOptionResponse getOption(Long menuId, Long optionId) {
        // 가져오려는 옵션이 존재하지 않을 경우
        if (!optionRepository.existsById(optionId)) {
            throw new MenuOptionNotFoundException();
        }

        // 가져오려는 옵션이 menu에 포함된 옵션이 아닐 경우
        MenuOption option = optionRepository.findByIdAndMenu_Id(optionId, menuId).orElseThrow(InvalidStoreMenuException::new);

        return MenuOptionResponse.from(option);
    }

    private MenuOption modifiedOption(MenuOption option, String text, Integer price) {
        String updateText = option.getText();
        Integer updatePrice = option.getPrice();

        if (!text.isBlank()) {
            updateText = text;
        }
        if (price != 0) {
            updatePrice = price;
        }

        return option.updateOption(updateText, updatePrice);
    }

    private Menu validateMenuOfStore(Long userId, Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);
        Long storeId = menuRepository.findByMenuIdForStoreId(menuId);
        myStoreCache.validateStoreOwner(userId, storeId);

        if (menu.getIsDeleted()) {
            throw new MenuAlreadyDeletedException();
        }

        return menu;
    }

}

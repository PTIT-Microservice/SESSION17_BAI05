package com.re.ss17.service;

import com.re.ss17.config.CacheConfig;
import com.re.ss17.dto.MenuDTO;
import com.re.ss17.model.Menu;
import com.re.ss17.repository.MenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Cacheable(cacheNames = CacheConfig.MENU_CACHE, key = "#id")
    public MenuDTO getMenu(Long id) {
        log.info(">>> [DB FALLBACK] SELECT menu id={} tu database", id);
        Menu menu = menuRepository.findById(id).orElse(null);
        return menu == null ? null : new MenuDTO(menu.getId(), menu.getDishName(), menu.getPrice());
    }

    @CacheEvict(cacheNames = CacheConfig.MENU_CACHE, key = "#menu.id")
    public MenuDTO updateMenu(MenuDTO menu) {
        Menu saved = menuRepository.save(new Menu(menu.id(), menu.dishName(), menu.price()));
        log.info(">>> [DB UPDATED] menu id={} gia moi={}", saved.getId(), saved.getPrice());
        return new MenuDTO(saved.getId(), saved.getDishName(), saved.getPrice());
    }
}

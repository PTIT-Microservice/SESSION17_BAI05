package com.re.ss17.controller;

import com.re.ss17.dto.MenuDTO;
import com.re.ss17.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> getMenu(@PathVariable Long id) {
        return ResponseEntity.ofNullable(menuService.getMenu(id));
    }

    @PutMapping
    public MenuDTO updateMenu(@RequestBody MenuDTO menu) {
        return menuService.updateMenu(menu);
    }
}

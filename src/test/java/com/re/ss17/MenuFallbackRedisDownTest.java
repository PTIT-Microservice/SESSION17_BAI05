package com.re.ss17;

import static org.assertj.core.api.Assertions.assertThat;

import com.re.ss17.dto.MenuDTO;
import com.re.ss17.repository.MenuRepository;
import com.re.ss17.service.MenuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.redis.port=6390",
        "spring.data.redis.timeout=100ms",
        "spring.data.redis.connect-timeout=100ms"
})
@DisplayName("BT05 - Redis down: GET van 200 va doc tu DB, PUT van ghi duoc")
class MenuFallbackRedisDownTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void docVanTraDuLieuKhiKhongCoRedis() {
        assertThat(menuService.getMenu(1L)).isNotNull();
        assertThat(menuService.getMenu(1L).id()).isEqualTo(1L);
        assertThat(menuService.getMenu(999L)).isNull();
    }

    @Test
    void capNhatVanThanhCongKhiEvictThatBai() {
        MenuDTO current = menuService.getMenu(1L);
        long newPrice = current.price() + 1000L;

        assertThat(menuService.updateMenu(new MenuDTO(1L, current.dishName(), newPrice)).price())
                .isEqualTo(newPrice);
        assertThat(menuRepository.findById(1L).orElseThrow().getPrice()).isEqualTo(newPrice);
        assertThat(menuService.getMenu(1L).price()).isEqualTo(newPrice);
    }
}

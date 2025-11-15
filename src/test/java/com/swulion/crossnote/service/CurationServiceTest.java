package com.swulion.crossnote.service;

import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import com.swulion.crossnote.entity.Curation.CurationType;
import com.swulion.crossnote.repository.CategoryRepository;
import com.swulion.crossnote.repository.Curation.CurationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Testcontainers
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class}) // Security 제거
class CurationServiceTest {

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0.40")
            .withDatabaseName("crossnote_test")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private CurationService curationService;

    @Autowired
    private CurationRepository curationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        // DB 초기화
        curationRepository.deleteAll();
        categoryRepository.deleteAll();

        category1 = new Category();
        category1.setCategoryName("Category 1");
        categoryRepository.save(category1);

        category2 = new Category();
        category2.setCategoryName("Category 2");
        categoryRepository.save(category2);

        Curation curation1 = Curation.builder()
                .title("큐레이션 1")
                .description("설명 1")
                .sourceUrl("https://example1.com")
                .curationType(CurationType.INSIGHT)
                .curationLevel(CurationLevel.LEVEL_1)
                .likeCount(0L)
                .scrapCount(0L)
                .terminologyDensity(0.0)
                .category(category1)
                .build();

        Curation curation2 = Curation.builder()
                .title("큐레이션 2")
                .description("설명 2")
                .sourceUrl("https://example2.com")
                .curationType(CurationType.CROSSNOTE)
                .curationLevel(CurationLevel.LEVEL_2)
                .likeCount(0L)
                .scrapCount(0L)
                .terminologyDensity(0.0)
                .category(category2)
                .build();

        curationRepository.saveAll(List.of(curation1, curation2));
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("큐레이션 조회 테스트")
    void findCuration_Success() {
        List<Curation> curations = curationRepository.findAll();
        assertThat(curations).hasSize(2);
        assertThat(curations.get(0).getTitle()).isEqualTo("큐레이션 1");
        assertThat(curations.get(1).getTitle()).isEqualTo("큐레이션 2");
    }

    @Test
    @DisplayName("좋아요 카운트 증가 테스트")
    void incrementLikeCount_Success() {
        Curation curation = curationRepository.findAll().get(0);
        long before = curation.getLikeCount();

        curation.incrementLikeCount(); // 엔티티 메서드 호출
        curationRepository.save(curation);
        entityManager.flush();
        entityManager.clear();

        Curation refreshed = curationRepository.findById(curation.getId()).orElseThrow();
        assertThat(refreshed.getLikeCount()).isEqualTo(before + 1);
    }
}

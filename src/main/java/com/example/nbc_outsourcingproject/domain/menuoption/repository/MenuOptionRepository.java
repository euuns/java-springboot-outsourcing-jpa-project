package com.example.nbc_outsourcingproject.domain.menuoption.repository;

import com.example.nbc_outsourcingproject.domain.menuoption.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {

    @Query("SELECT o FROM MenuOption o JOIN FETCH o.menu WHERE o.menu.id = :menuId")
    List<MenuOption> findByMenuId(Long menuId);

    Optional<MenuOption> findByIdAndMenu_Id(Long optionId, Long menuId);

    Optional<List<MenuOption>> findByIdInAndMenu_Id(List<Long> optionIds, Long menuId);

    @Query("SELECT o FROM MenuOption o JOIN FETCH o.menu WHERE o.id = :optionId AND o.menu.id = :menuId ")
    Boolean existsByIdAndMenuId(Long optionId, Long menuId);

    @Query("SELECT COUNT(o) = :size FROM MenuOption o WHERE o.id IN :options AND o.menu.id = :menuId")
    boolean existsAllByIdAndMenu_Id(@Param("options") List<Long> options, @Param("size") long size, @Param("menuId")Long menuId);

    List<MenuOption> findByIdIn(List<Long> optionIds);
}

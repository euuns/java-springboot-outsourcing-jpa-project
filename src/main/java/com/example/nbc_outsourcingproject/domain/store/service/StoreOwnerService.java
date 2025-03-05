package com.example.nbc_outsourcingproject.domain.store.service;

import com.example.nbc_outsourcingproject.domain.common.dto.AuthUser;
import com.example.nbc_outsourcingproject.domain.common.exception.InvalidRequestException;
import com.example.nbc_outsourcingproject.domain.common.exception.UnauthorizedException;
import com.example.nbc_outsourcingproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.nbc_outsourcingproject.domain.store.dto.response.StoreResponse;
import com.example.nbc_outsourcingproject.domain.store.entity.Store;
import com.example.nbc_outsourcingproject.config.cache.MyStoreCache;
import com.example.nbc_outsourcingproject.domain.store.dto.request.StoreSaveRequest;
import com.example.nbc_outsourcingproject.domain.store.dto.response.StoreResponse;
import com.example.nbc_outsourcingproject.domain.store.dto.response.StoreSaveResponse;
import com.example.nbc_outsourcingproject.domain.store.entity.Store;
import com.example.nbc_outsourcingproject.domain.store.exception.MaxStoreCreationException;
import com.example.nbc_outsourcingproject.domain.store.repository.StoreRepository;
import com.example.nbc_outsourcingproject.domain.user.entity.User;
import com.example.nbc_outsourcingproject.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class StoreOwnerService {

    private final MyStoreCache myStoreCache;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    private final Map<Long, List<Long>> storeCache = new ConcurrentHashMap<>();

    // 가게 생성
    @Transactional
    public StoreSaveResponse saveStore(AuthUser user, @Valid StoreSaveRequest storeSaveRequest) {

        User storeOwner = userRepository.findById(user.getId()).orElseThrow(() -> new InvalidRequestException("존재하지 않는 사용자입니다."));

        validateOwner(user.getUserRole().toString());
        validateStoreCreationLimit(user);

        Store store = new Store(
                storeOwner,
                storeSaveRequest.getName(),
                storeSaveRequest.getAddress(),
                storeSaveRequest.getMinOrderAmount(),
                storeSaveRequest.getStoreInfo(),
                storeSaveRequest.getOpened(),
                storeSaveRequest.getClosed()
        );

        Store savedStore = storeRepository.save(store);

        return new StoreSaveResponse(
                savedStore.getId(),
                savedStore.getName(),
                savedStore.getStoreInfo(),
                savedStore.getMinOrderAmount(),
                savedStore.getClosed(),
                savedStore.getOpened()
        );
    }

    // 소유한 가게 조회
    @Transactional(readOnly = true)
    public List<StoreResponse> getStoresMine(AuthUser authUser) {
        List<Store> storesMine = storeRepository.findAllByUserId(authUser.getId());
        return storesMine.stream()
                .map(store -> new StoreResponse(
                        store.getName(),
                        store.getStoreInfo(),
                        store.getAddress(),
                        store.getMinOrderAmount(),
                        store.getClosed(),
                        store.getOpened()))
                .toList();
    }

    // 가게 정보 수정
    @Transactional
    public StoreResponse updateStore(AuthUser user, Long storeId, StoreUpdateRequest storeUpdateRequest) {
        validateOwner(user.getUserRole().toString());
        validateStoreOwnership(user.getId(), storeId);
        Store store = findStoreById(storeId);
        store.updateInfo(
                storeUpdateRequest.getName(),
                storeUpdateRequest.getAddress(),
                storeUpdateRequest.getMinOrderAmount(),
                storeUpdateRequest.getStoreInfo(),
                storeUpdateRequest.getOpened(),
                storeUpdateRequest.getClosed()
        );

        return new StoreResponse(
                store.getName(),
                store.getStoreInfo(),
                store.getAddress(),
                store.getMinOrderAmount(),
                store.getOpened(),
                store.getClosed()
        );
    }

    // 가게 폐업 처리
    @Transactional
    public void shutDownStore(AuthUser user, Long storeId) {
        Store store = findStoreById(storeId);
        validateStoreOwnership(user.getId(), storeId);
        store.shutDown();
    }

    // 사용자 OWNER 여부 검증
    private void validateOwner(String role) {
        if (!role.equals("OWNER")) {
            throw new UnauthorizedException("Owner가 아닙니다.");
        }

    // 생성된 가게 캐시 저장
    @Cacheable(key = "#userId", value = "myStores")
    public List<Long> saveStoreToCache(Long userId, Long storeId) {
        List<Long> cacheValue = storeCache.get(userId);
        cacheValue.add(storeId);
        storeCache.put(userId, cacheValue);

        return cacheValue;
    }

    // 캐시 수정 <가게 삭제>
    @CachePut(key = "#userId", value = "myStores")
    public List<Long> removeStoreToCache(Long userId, Long storeId) {
        myStoreCache.validateStoreOwner(userId, storeId);
        List<Long> cacheStore = myStoreCache.getCacheStore(userId);

        // int index로 remove메서드가 실행될 위험이 있어 wrapper 타입을 정확하게 명시
        cacheStore.remove(Long.valueOf(storeId));
        return cacheStore;
    }

    // 가게가 3개 초과시 생성 불가
    private void validateStoreCreationLimit(AuthUser user) {
        List<Store> storesMine = storeRepository.findAllByUserId(user.getId());
        if (storesMine.size() >= 3) {
            throw new InvalidRequestException("가게는 최대 3개까지 생성할 수 있습니다.");
        }
    }

    // 소유한 가게인지 확인
    private void validateStoreOwnership(Long userId, Long storeId) {
        Store store = findStoreById(storeId);
        if (!store.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("권한이 없습니다.");
        }
    }

    // 가게 조회 로직
    private Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(() -> new InvalidRequestException("해당 가게가 존재하지 않습니다."));
    }

}

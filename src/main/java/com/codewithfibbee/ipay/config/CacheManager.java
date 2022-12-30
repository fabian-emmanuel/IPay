package com.codewithfibbee.ipay.config;

import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.service.IPayProviderService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
@Value
@Slf4j
public class CacheManager {
    Cache<String, ListBanksResponse> bankResponseCache;
    Map<String, IPayProviderService> coreBankingProviders;
    Map<String, List<ListBanksResponse>> providerBanks = new ConcurrentHashMap<>();

    public String getBankName(final String code) {
        return Optional.ofNullable(bankResponseCache.get(code))
                .orElse(new ListBanksResponse(code, "", ""))
                .getName();
    }

    public List<ListBanksResponse> getProviderBanks(final String provider){
        return Optional.ofNullable(providerBanks.get(provider)).orElse(List.of());
    }
    @Scheduled(fixedRate = 604800000)// seven-days
    void updateSupportedInstitutionCache() {
        coreBankingProviders
                .entrySet()
                .parallelStream()
                .flatMap(p -> {
                    List<ListBanksResponse> banks = p.getValue().fetchBanks();
                    providerBanks.put(p.getKey(), banks);
                    return banks.parallelStream();
                })
                .forEach(nipBank -> bankResponseCache.put(nipBank.getCode(), nipBank));
    }
}

package com.mifica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3Config {

    // ICP-TOTAL: 0
    // Classe trivial: Apenas instancia Web3j com URL estática ou a partir da variável de ambiente POLYGON_RPC_URL.

    @Value("${POLYGON_RPC_URL:}")
    private String polygonRpcUrl;

    @Bean
    public Web3j web3j() {
        // Use POLYGON_RPC_URL from environment when available to avoid hardcoded endpoints
        // Default is kept as placeholder but deployment should set POLYGON_RPC_URL.
        return Web3j.build(new HttpService(polygonRpcUrl == null || polygonRpcUrl.isBlank()
                ? "https://mainnet.infura.io/v3/seu-token-infura"
                : polygonRpcUrl));
    }
}

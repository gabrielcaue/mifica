package com.mifica.blockchain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.TypeReference;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Serviço para interagir com o contrato Badge no Polygon.
 * Encapsula chamadas de leitura e escrita ao contrato inteligente.
 */
@Service
public class BadgeContractService {

    @Autowired
    private Web3j web3j;

    @Value("${blockchain.badge.contract-address:}")
    private String badgeContractAddress;

    @Value("${blockchain.polygon.chain-id:137}")
    private Long chainId;

    /**
     * Prepara uma chamada à função createBadge do contrato Badge.
     * 
     * @param toAddress endereço que receberá o badge (usar checksum format 0x...)
     * @param metadata JSON ou string com dados do badge
     * @return dados codificados (data) para incluir em uma transação
     */
    public String buildCreateBadgeData(String toAddress, String metadata) {
        // Validar endereço
        if (!toAddress.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Invalid Ethereum address: " + toAddress);
        }

        // Validar metadata
        if (metadata == null || metadata.isBlank()) {
            throw new IllegalArgumentException("Metadata cannot be empty");
        }

        // Função: createBadge(address to, string calldata metadata)
        Function function = new Function(
            "createBadge",
            Arrays.asList(
                new Address(toAddress),
                new Utf8String(metadata)
            ),
            Arrays.asList()  // Sem retorno (event-only)
        );

        return FunctionEncoder.encode(function);
    }

    /**
     * Valida se o contrato está configurado e acessível.
     * 
     * @throws IllegalStateException se contrato não está configurado
     */
    public void validateContractConfiguration() {
        if (badgeContractAddress == null || badgeContractAddress.isBlank() 
            || "0x0000000000000000000000000000000000000000".equals(badgeContractAddress.toLowerCase())) {
            throw new IllegalStateException("Badge contract address not configured. Set blockchain.badge.contract-address.");
        }

        if (chainId != 137L) {
            throw new IllegalStateException("Badge contract is deployed on Polygon mainnet (chainId=137). Current chainId: " + chainId);
        }
    }

    /**
     * Retorna o endereço do contrato configurado.
     */
    public String getContractAddress() {
        return badgeContractAddress;
    }

    /**
     * Simula uma chamada de leitura (get nextId do contrato).
     * Útil para verificar estado sem fazer uma transação.
     */
    public String callGetNextId() throws IOException {
        if (badgeContractAddress == null || badgeContractAddress.isBlank()) {
            throw new IllegalStateException("Badge contract address not configured");
        }

        // Função getter: nextId() -> uint256
        Function function = new Function(
            "nextId",
            Collections.emptyList(),
            Collections.singletonList(new TypeReference<Uint256>() {})
        );

        String data = FunctionEncoder.encode(function);
        
        // Chamar contrato (read-only, não usa gas)
        EthCall ethCall = web3j.ethCall(
            Transaction.createEthCallTransaction(null, badgeContractAddress, data),
            org.web3j.protocol.core.DefaultBlockParameterName.LATEST
        ).send();

        if (ethCall.hasError()) {
            throw new IOException("Contract call failed: " + ethCall.getError().getMessage());
        }

        List<?> decoded = FunctionReturnDecoder.decode(
            ethCall.getValue(),
            function.getOutputParameters()
        );

        if (decoded.isEmpty() || decoded.get(0) == null) {
            return "0";
        }

        return decoded.get(0).toString();
    }
}

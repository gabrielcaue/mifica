# Package: `com.mifica.filter`

Hospeda o filtro JWT da cadeia de segurança para autenticação stateless por token Bearer.
Intercepta request, valida assinatura/claims e popula `SecurityContext` por thread.
Opera na fronteira de segurança para enforcement de identidade antes da camada REST.

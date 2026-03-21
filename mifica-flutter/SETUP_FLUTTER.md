# Mifica Mobile - Aplicativo Flutter

## Estrutura do Projeto Flutter

Este é um guia de implementação para a versão mobile nativa do Mifica usando Flutter.

### Configuração Inicial

```bash
flutter create mifica_mobile
cd mifica_mobile
flutter pub add http dio provider flutter_secure_storage
```

### Arquitetura Recomendada

```
lib/
├── main.dart
├── config/
│   ├── api_client.dart          # Cliente HTTP para consumir API Java
│   ├── app_constants.dart
│   └── app_routes.dart
├── models/
│   ├── usuario.dart
│   ├── login_response.dart
│   └── admin_response.dart
├── providers/
│   ├── auth_provider.dart       # Gerenciamento de autenticação
│   ├── user_provider.dart
│   └── admin_provider.dart
├── screens/
│   ├── auth/
│   │   ├── login_screen.dart
│   │   ├── cadastro_screen.dart
│   │   └── cadastro_admin_screen.dart
│   ├── home/
│   │   ├── dashboard_screen.dart
│   │   ├── perfil_screen.dart
│   │   └── configuracoes_screen.dart
│   └── admin/
│       └── admin_panel_screen.dart
├── widgets/
│   ├── bottom_nav_bar.dart
│   ├── custom_button.dart
│   ├── custom_text_field.dart
│   └── responsive_layout.dart
└── utils/
    ├── validators.dart
    ├── formatters.dart
    └── theme.dart
```

### Próximos Passos

1. **Integração com API Java**
   - Usar `http` ou `dio` package
   - Consumir endpoints do backend em `application.properties`

2. **Autenticação**
   - JWT token armazenado com `flutter_secure_storage`
   - Refresh token automático
   - Logout seguro

3. **Responsividade**
   - Layout adaptativo com `MediaQuery`
   - Diferentes layouts para phone/tablet
   - Safe areas e notches

4. **Sidebars Móveis**
   - Drawer para menu principal
   - Bottom navigation para ações rápidas
   - Transições suaves

5. **Estado Global**
   - Provider para auth state
   - Caching de dados de usuário
   - Sincronização com backend

### Exemplo: Cliente API Básico

```dart
// lib/config/api_client.dart
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class ApiClient {
  final String baseUrl = 'http://seu-backend.com';
  final storage = const FlutterSecureStorage();

  Future<Map<String, dynamic>> post(String endpoint, Map<String, dynamic> data) async {
    final response = await http.post(
      Uri.parse('$baseUrl$endpoint'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ${await storage.read(key: 'token')}'
      },
      body: jsonEncode(data),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Erro: ${response.body}');
    }
  }
}
```

### Exemplo: Autenticação com Provider

```dart
// lib/providers/auth_provider.dart
import 'package:flutter/foundation.dart';
import '../config/api_client.dart';
import '../models/usuario.dart';

class AuthProvider extends ChangeNotifier {
  final ApiClient _apiClient = ApiClient();
  Usuario? _usuario;
  bool _isLoading = false;

  Usuario? get usuario => _usuario;
  bool get isLoading => _isLoading;

  Future<void> cadastro(String nome, String email, String senha) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _apiClient.post('/usuarios/cadastro', {
        'nome': nome,
        'email': email,
        'senha': senha,
      });
      // Handle response
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> login(String email, String senha) async {
    // Similar ao cadastro
  }
}
```

### Instruções para Desenvolvimento

1. Instalar Flutter SDK
2. Executar `flutter pub get`
3. Configurar emulador ou dispositivo físico
4. Rodar `flutter run`

### Recursos Úteis

- [Flutter Docs](https://flutter.dev/docs)
- [Provider Package](https://pub.dev/packages/provider)
- [Dio HTTP Client](https://pub.dev/packages/dio)
- [Flutter Secure Storage](https://pub.dev/packages/flutter_secure_storage)

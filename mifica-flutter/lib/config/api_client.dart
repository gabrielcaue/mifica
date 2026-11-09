// lib/config/api_client.dart
// Cliente HTTP para consumir a API Java do Backend Mifica

import 'dart:convert';
import 'dart:io';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class ApiClient {
  final FlutterSecureStorage storage = const FlutterSecureStorage();
  static const String _tokenKey = 'jwt_token';

  String get baseUrl {
    if (Platform.isAndroid) {
      return const String.fromEnvironment(
        'API_URL',
        defaultValue: 'http://10.0.2.2:8080',
      );
    }

    return const String.fromEnvironment(
      'API_URL',
      defaultValue: 'http://localhost:8080',
    );
  }

  Uri _uri(String endpoint) {
    final normalizedEndpoint = endpoint.startsWith('/') ? endpoint : '/$endpoint';
    final base = baseUrl.trim().replaceAll(RegExp(r'/+$'), '');
    return Uri.parse('$base/api$normalizedEndpoint');
  }

  Future<Map<String, dynamic>> post(
    String endpoint,
    Map<String, dynamic> data,
  ) async {
    try {
      final token = await storage.read(key: _tokenKey);
      final headers = {
        'Content-Type': 'application/json',
        if (token != null) 'Authorization': 'Bearer $token',
      };

      final response = await http.post(
        _uri(endpoint),
        headers: headers,
        body: jsonEncode(data),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final decoded = jsonDecode(response.body);
        return decoded is Map<String, dynamic>
            ? decoded
            : {'data': decoded};
      }

      if (response.statusCode == 401) {
        await clearToken();
        throw Exception('Não autenticado. Faça login novamente.');
      }

      throw Exception('Erro ${response.statusCode}: ${response.body}');
    } catch (e) {
      rethrow;
    }
  }

  Future<Map<String, dynamic>> get(String endpoint) async {
    try {
      final token = await storage.read(key: _tokenKey);
      final headers = {
        'Content-Type': 'application/json',
        if (token != null) 'Authorization': 'Bearer $token',
      };

      final response = await http.get(
        _uri(endpoint),
        headers: headers,
      );

      if (response.statusCode == 200) {
        final decoded = jsonDecode(response.body);
        return decoded is Map<String, dynamic>
            ? decoded
            : {'data': decoded};
      }

      if (response.statusCode == 401) {
        await clearToken();
        throw Exception('Não autenticado. Faça login novamente.');
      }

      throw Exception('Erro ${response.statusCode}: ${response.body}');
    } catch (e) {
      rethrow;
    }
  }

  Future<Map<String, dynamic>> login(String email, String senha) async {
    final response = await post('/usuarios/login', {
      'email': email.trim(),
      'senha': senha.trim(),
    });

    final token = response['token']?.toString();
    if (token != null && token.isNotEmpty) {
      await saveToken(token);
    }

    return response;
  }

  Future<Map<String, dynamic>> cadastro(String nome, String email, String senha) async {
    final response = await post('/usuarios/cadastro', {
      'nome': nome.trim(),
      'email': email.trim(),
      'senha': senha.trim(),
    });

    return response;
  }

  Future<void> saveToken(String token) async {
    await storage.write(key: _tokenKey, value: token);
  }

  Future<String?> getToken() async {
    return await storage.read(key: _tokenKey);
  }

  Future<void> clearToken() async {
    await storage.delete(key: _tokenKey);
  }
}

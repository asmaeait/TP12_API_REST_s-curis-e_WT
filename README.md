# TP12 - API REST Securisee avec JWT

## Description

Application Spring Boot qui securise une API REST avec JSON Web Token (JWT).
Contrairement aux sessions classiques, cette approche est stateless :
aucune donnee d'authentification n'est stockee sur le serveur.
Chaque requete contient un token signe qui prouve l'identite de l'utilisateur.

---

## Technologies utilisees

- Java 17
- Spring Boot 3.2
- Spring Security 6
- Spring Data JPA
- MySQL
- JWT (jjwt 0.11.5)
- Maven

---

## Structure du projet

```
src/main/java/ma/ens/security/
├── config/
│   ├── SecurityConfig.java          # Configuration securite stateless
│   └── DataInitializer.java         # Insertion des donnees au demarrage
├── jwt/
│   ├── JwtUtil.java                 # Generation et validation des tokens
│   └── JwtAuthorizationFilter.java  # Filtre JWT sur chaque requete
├── entities/
│   ├── User.java                    # Entite utilisateur
│   └── Role.java                    # Entite role
├── repositories/
│   ├── UserRepository.java          # Acces utilisateurs en base
│   └── RoleRepository.java          # Acces roles en base
├── services/
│   └── CustomUserDetailsService.java # Chargement utilisateurs pour Spring Security
├── web/
│   ├── AuthController.java          # Endpoint de login - genere le token
│   └── TestController.java          # Endpoints proteges pour les tests
└── JwtApiApplication.java           # Point d'entree
```

---

## Prerequis

- Java 17+
- MySQL (XAMPP ou autre)
- Maven
- Postman (pour tester l'API)

---

## Configuration base de donnees

Creer la base dans phpMyAdmin :

```sql
CREATE DATABASE security_jwt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

<img width="1221" height="239" alt="image" src="https://github.com/user-attachments/assets/d112e7dd-1a69-4684-8d1a-d861d3097fed" />


---

## Lancer le projet

```bash
mvn spring-boot:run
```

Les tables et donnees sont creees automatiquement au demarrage.

---

## Comptes disponibles

| Utilisateur | Mot de passe | Role |
|-------------|--------------|------|
| `admin_sys` | `Admin@2024` | ADMIN + USER |
| `etudiant` | `Etudiant@123` | USER |

---

## Endpoints disponibles

| Methode | URL | Acces | Description |
|---------|-----|-------|-------------|
| GET | `/api/auth/status` | Public | Verifie que l'API fonctionne |
| POST | `/api/auth/login` | Public | Obtenir un token JWT |
| GET | `/api/user/profile` | USER + ADMIN | Profil utilisateur |
| GET | `/api/admin/dashboard` | ADMIN uniquement | Tableau de bord admin |

---

## Comment tester avec Postman

### 1. Obtenir un token JWT

**POST** `http://localhost:8080/api/auth/login`

Body (raw JSON) :
```json
{
    "username": "admin_sys",
    "password": "Admin@2024"
}
```

Reponse :
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin_sys",
    "type": "Bearer",
    "expiresIn": 3600
}
```

### 2. Utiliser le token

Ajouter dans chaque requete protegee le header :
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 3. Comportements attendus

| Action | Resultat |
|--------|----------|
| Login avec bons identifiants | Token JWT retourne |
| Login avec mauvais identifiants | Erreur 401 |
| Requete sans token | Erreur 403 |
| Requete avec token valide | Acces autorise |
| Compte etudiant sur /admin | Erreur 403 |

---

## Fonctionnement du JWT

```
Client                          Serveur
  |                               |
  |-- POST /api/auth/login ------>|
  |   { username, password }      |
  |                               | Verifie les identifiants
  |                               | Genere le token JWT
  |<-- { token: "eyJ..." } -------|
  |                               |
  |-- GET /api/user/profile ----->|
  |   Authorization: Bearer eyJ...|
  |                               | Valide le token
  |                               | Charge l'utilisateur
  |<-- { profil utilisateur } ----|
```

---

## Les Resultats des tests:
### Test 1 — Vérifier que l'API fonctionne

<img width="391" height="328" alt="Test1" src="https://github.com/user-attachments/assets/82065481-8aa6-4829-9c31-18655f519af0" />


### Test 2 — Obtenir un token JWT (LOGIN)

<img width="722" height="388" alt="Test2" src="https://github.com/user-attachments/assets/d087cab3-b4a1-431c-a300-9fe2c7a5b4d5" />


### Test 3 — Accéder à l'espace utilisateur AVEC token

<img width="788" height="354" alt="Test3" src="https://github.com/user-attachments/assets/cd0d9016-6bc6-475e-8841-dc434bf53a3d" />


### Test 4 — Accéder SANS token (accès refusé)

<img width="797" height="307" alt="Test4" src="https://github.com/user-attachments/assets/73a6b8b0-f76a-4824-b7dc-ccea905a44c7" />


### Test 5 — Accéder à l'espace admin

<img width="777" height="362" alt="Test5" src="https://github.com/user-attachments/assets/6321b4b5-8d28-4657-bef8-cf43d44c127b" />


### Test 6 — Tester avec compte etudiant

<img width="783" height="395" alt="Test6" src="https://github.com/user-attachments/assets/d00cdf33-1f0e-4ab0-be9d-5fa55d583a3c" />


### Test 7 — Mauvais identifiants

<img width="791" height="365" alt="Test7" src="https://github.com/user-attachments/assets/e781da99-2572-4459-8a4a-3af99805f06d" />

---

## Points cles

- `JwtUtil` : genere et valide les tokens avec HMAC-SHA256
- `JwtAuthorizationFilter` : intercepte chaque requete et verifie le token
- `SessionCreationPolicy.STATELESS` : aucune session cote serveur
- `BCryptPasswordEncoder` : mots de passe securises en base
- Token valide pendant **1 heure**

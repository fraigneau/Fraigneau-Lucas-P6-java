Voici un README complet du projet en français qui résume toutes les caractéristiques et fonctionnalités de l'application Pay My Buddy.

# Pay My Buddy

Une application de transfert d'argent peer-to-peer développée avec Spring Boot qui permet aux utilisateurs d'envoyer facilement de l'argent à leurs amis.

## Stack Technologique

- **Java 21** - Langage de programmation
- **Spring Boot 3.4.3** - Framework d'application
- **Spring Security** - Authentification et autorisation
- **Spring Data JPA** - Persistance des données
- **Thymeleaf** - Moteur de templates Java côté serveur
- **MySQL** - Base de données
- **Gradle** - Gestion des dépendances et des builds
- **JaCoCo** - Couverture de code Java
- **JUnit** - Framework de test
- **Lombok** - Réduction du code boilerplate
- **MapStruct** - Framework de mapping de beans

## Fonctionnalités

- Inscription et authentification des utilisateurs
- Gestion du profil
- Ajout et gestion des amis
- Envoi d'argent aux amis connectés
- Dépôt de fonds sur le compte
- Consultation de l'historique des transactions

## Structure du Projet

L'application suit une architecture en couches standard :

- **Controller** : Gère les requêtes HTTP et les réponses
- **Service** : Contient la logique métier
- **Repository** : Interface avec la base de données
- **Model** : Entités du domaine
- **DTO** : Objets de transfert de données
- **Mapper** : Interfaces MapStruct pour la conversion d'objets

## Documentation

All project documentation is available in the `docs` directory of the GitHub repository and is deployed online:

- [**JaCoCo Reports**](https://fraigneau.github.io/Fraigneau-Lucas-P6-java/jacoco/): Code coverage analysis for the project
- [**Gradle Reports Unit test**](https://fraigneau.github.io/Fraigneau-Lucas-P6-java/test/): Build and test reports
- [**Gradle Reports integration test**](https://fraigneau.github.io/Fraigneau-Lucas-P6-java/integrationTest/): Build and test reports
- [**JavaDoc**](https://fraigneau.github.io/Fraigneau-Lucas-P6-java/javadoc/): API documentation for the codebase

You can access the documentation online through the GitHub Pages site for this repository.

### Prérequis

- Java 21
- MySQL
- Gradle

### Configuration

1. Cloner le dépôt
2. Configurer la connexion à la base de données MySQL dans `application.properties`
3. Construire le projet avec Gradle

```bash
./gradlew build
```

4. Exécuter l'application

```bash
./gradlew bootRun
```

L'application devrait maintenant fonctionner à l'adresse `http://localhost:8080`

## Tests

Le projet comprend des tests unitaires complets couvrant les contrôleurs, les services et la configuration de sécurité.

Exécuter les tests avec :

```bash
./gradlew test
```

Pour générer des rapports de couverture de code :

```bash
./gradlew jacocoTestReport
```

Les rapports peuvent être trouvés dans le répertoire `build/reports/jacoco/`.

## Schéma de Base de Données

L'application utilise plusieurs entités interconnectées :

- **User** : Représente les utilisateurs de l'application
- **Transaction** : Suit les transferts d'argent entre utilisateurs
- **UserConnection** : Gère les relations d'amitié entre utilisateurs

## Sécurité

L'application implémente Spring Security pour l'authentification et l'autorisation. Les mots de passe sont encodés à l'aide de BCrypt.

## Pages de l'Application

L'application comprend plusieurs pages HTML/Thymeleaf :

- **login.html** : Page de connexion
- **signup.html** : Page d'inscription
- **profil.html** : Gestion du profil utilisateur
- **friends.html** : Gestion des amis/contacts
- **transaction.html** : Transferts d'argent et historique des transactions

## Gestion des Exceptions

L'application inclut un gestionnaire d'exceptions global qui intercepte les exceptions spécifiques comme :

- **ResourceNotFoundException** : Ressource introuvable
- **InsufficientBalanceException** : Solde insuffisant
- **SelfSendingAmountException** : Tentative d'envoi d'argent à soi-même
- **ContactAlreadyExistException** : Contact déjà existant
- **DuplicateResourceException** : Ressource en double
- **UserNotFondExeption** : Utilisateur non trouvé

## Tests d'Intégration

Des tests d'intégration sont configurés pour vérifier le bon fonctionnement de l'application dans son ensemble. Ils peuvent être exécutés avec :

```bash
./gradlew integrationTest
```

## Structure des Répertoires

- **src/main/java** : Code source principal
- **src/main/resources** : Fichiers de ressources (templates, CSS, properties)
- **src/test/java** : Tests unitaires et d'intégration
- **src/test/resources** : Ressources pour les tests

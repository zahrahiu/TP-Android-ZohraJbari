# 🌸 Flora Boutique – Application Mobile de Vente de Fleurs

##  Description
**Flora Boutique** est une application mobile développée dans le cadre d’un projet académique. Elle permet aux utilisateurs de consulter et d’acheter des fleurs en ligne à travers une interface moderne, intuitive et personnalisable.

##  Fonctionnalités principales
- Affichage des produits floraux sous forme de cartes
- Recherche avec filtre (type, couleur, occasion, prix)
- Interface multilingue (Français, Arabe, Anglais)
- Choix du thème (clair, sombre, Pastel)
- Système de gestion des utilisateurs par l’administrateur
- Barre de navigation inférieure avec onglets (Accueil, Catégories, Favoris, Panier)

## Technologies utilisées
- **Kotlin** & **Jetpack Compose** 
- **Android Studio** – Environnement de développement
- **API-JSON** – Fichier local utilisé pour simuler les données des produits
- **Git** & **GitHub** – Suivi de version et hébergement du projet

##  Dépendances principales (`build.gradle`)

```kotlin

val nav_version = "2.8.9"
    implementation("com.google.dagger:hilt-android:2.56.1")
    ksp("com.google.dagger:hilt-compiler:2.56.1")

// 🌐 Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// 🖼️ UI & Material
implementation("androidx.compose.ui:ui:1.4.3")
implementation("androidx.compose.material3:material3:1.2.1")
implementation("androidx.compose.material3:material3:1.1.0")        // legacy screens
implementation("androidx.compose.material:material-icons-extended:1.6.1")
implementation("com.google.accompanist:accompanist-flowlayout:0.31.5-beta")

// 🚦 Navigation
def nav_version = "2.7.7"
implementation("androidx.navigation:navigation-compose:$nav_version")

// 💾 DataStore
implementation("androidx.datastore:datastore-preferences:1.0.0")
implementation("androidx.datastore:datastore-core:1.0.0")

// 🧩 Dependency Injection
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

```



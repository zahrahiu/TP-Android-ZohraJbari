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
- **Hilt**
##  Dépendances principales (`build.gradle`)

```kotlin

val nav_version = "2.8.9"
    implementation("com.google.dagger:hilt-android:2.56.1")
    ksp("com.google.dagger:hilt-compiler:2.56.1")

//  Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

//  UI & Material
implementation("androidx.compose.ui:ui:1.4.3")
implementation("androidx.compose.material3:material3:1.2.1")
implementation("androidx.compose.material3:material3:1.1.0")        // legacy screens
implementation("androidx.compose.material:material-icons-extended:1.6.1")
implementation("com.google.accompanist:accompanist-flowlayout:0.31.5-beta")

//  Navigation
def nav_version = "2.7.7"
implementation("androidx.navigation:navigation-compose:$nav_version")

//  DataStore
implementation("androidx.datastore:datastore-preferences:1.0.0")
implementation("androidx.datastore:datastore-core:1.0.0")

//  Dependency Injection
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

```

## Lancement
1- Clone le projet :
```
git clone https://github.com/zahrahiu/TP-Android-ZohraJbari.git
```
2- Ouvre-le dans Android Studio.  

3-Lance l’application sur un émulateur ou appareil Android.    
4-Sur un appareil réel :  
   a-Activez le mode développeur  
   b-Activez le débogage USB  
   c-Connectez votre téléphone avec un câble USB    
5-Sélectionnez l’appareil ou l’émulateur dans la barre d’outils Android Studio  
6-Cliquez sur le bouton Run pour lancer l’application



## Réalisation de l’application
### Logo de l'application
![image](https://github.com/user-attachments/assets/a891aa1c-5276-4eb3-8c4e-5c17636a89fd)
### Page Login
Dans cette étape, j’ai conçu l’interface de connexion de l’application Flora en utilisant Jetpack Compose (Kotlin). L’objectif était de proposer une expérience utilisateur simple, élégante et cohérente avec le thème floral de l’application.   

![image](https://github.com/user-attachments/assets/cb4b95ba-a254-4a1f-951f-c188787021c6)    

Si l’utilisateur saisit une adresse e mail ou un mot de passe incorrect ,un message d’erreur rouge s’affiche immédiatement sous les champs de saisie    

![image](https://github.com/user-attachments/assets/9b65325e-5249-4714-a327-3eb09b68c9e4)    

### page d’inscription  

Si l’utilisateur n’a pas encore de compte, il sélectionne le lien « S’inscrire » depuis la page de connexion et arrive sur l’écran d’inscription.  

![image](https://github.com/user-attachments/assets/8c2ce213-dfb8-4ab6-a351-b372a829e324)  

Tous les champs doivent obligatoirement être remplis ; si l’utilisateur laisse l’un d’eux vide, un message d’erreur rouge apparaît juste en dessous du bouton « S’inscrire » pour signaler l’omission et bloquer l’envoi tant que le formulaire n’est pas complet.    

![image](https://github.com/user-attachments/assets/4d8aae97-f75a-4b1e-8b97-8830deb0ad93)  

Si le contenu du champ « Confirmer le mot de passe » ne correspond pas au mot de passe initial => un message d’erreur rouge — « Les mots de passe ne correspondent pas » — s’affiche juste sous le bouton S’inscrire .      

![image](https://github.com/user-attachments/assets/e904c234-a544-4d2d-b10b-21ced78082bc)

### Page d’accueil 

La page d’accueil de l’application Flora Boutique affiche les éléments essentiels d’une boutique de fleurs en ligne : logo, barre de recherche avec filtres, bannières de catégories populaires, liste de produits avec prix, notes et favoris, ainsi qu’un menu de navigation en bas (Accueil, Catégories, Favoris, Panier).    

![image](https://github.com/user-attachments/assets/b56f78f8-e32e-4b57-8cd1-e37fb53978bd)  

L’utilisateur saisit le nom d’une fleur dans la barre de recherche. L’application filtre les produits correspondants et les affiche. Si aucun résultat n’est trouvé, un message “Aucun produit trouvé” s’affiche.  

![image](https://github.com/user-attachments/assets/a549a323-f566-4f4b-8794-083a3631c07c)

L’utilisateur peut affiner sa recherche en cliquant sur le bouton « Filtres ». Il peut filtrer par type, couleur, occasion, ou plage de prix. Un bouton « Réinitialiser » permet de tout effacer, et « Appliquer » lance la recherche selon les critères choisis.  

![image](https://github.com/user-attachments/assets/ad882aa3-5b72-4c98-ad79-ad90b2741b8e)


Sous la barre de recherche, trois bannières mettent en avant des catégories populaires comme les fleurs avec cadeaux, les fleurs multicolores et les paniers décoratifs. Elles aident à orienter l’utilisateur avec des visuels attractifs et des messages incitatifs.  

![image](https://github.com/user-attachments/assets/74e4f1ff-d3ea-496f-a2e4-47eac9b4a459)  

Le menu des paramètres permet à l’utilisateur de personnaliser son expérience dans l’application.  

![image](https://github.com/user-attachments/assets/7cbc77cd-33e3-4b14-ae34-fd19b6fb0b23)  

Lorsqu’une langue est sélectionnée, tous les textes de l’application (menus, boutons, messages) sont automatiquement traduits dans la langue choisie.    

![image](https://github.com/user-attachments/assets/2d642b53-ded9-4404-8d9a-827936f46471)   

Après avoir choisi la langue arabe  

![image](https://github.com/user-attachments/assets/c1c2ac8a-7386-4866-8df9-d69edbcf7762)  

L’application propose 3 thèmes différents. L’utilisateur peut choisir celui qu’il préfère selon son goût  


![image](https://github.com/user-attachments/assets/018c7796-0b3e-456d-9ff1-3ecb37400cda)  

Voilà, parmi les thèmes existants dans cette application.  

![image](https://github.com/user-attachments/assets/49041567-a8ed-4746-8243-bfe74157b459)![image](https://github.com/user-attachments/assets/e6a1bda2-3515-434b-899d-e9a53f7a0f57)

Lorsqu’on clique sur « Se déconnecter », un message de confirmation apparaît ; si l’utilisateur confirme, il est redirigé vers la page de connexion, sinon il reste sur la même page  

![image](https://github.com/user-attachments/assets/5474950b-446f-4622-b0dd-a38a875791f6)  

### Page categorie 

En cliquant sur « Catégories », l’application ouvre la page listant toutes les catégories ; si l’on sélectionne l’une d’elles, seuls les produits de cette catégorie s’affichent.  

![image](https://github.com/user-attachments/assets/ca544424-a454-462c-af9f-a9e60eb997a9)![image](https://github.com/user-attachments/assets/2e911f2e-cd49-48b9-b51f-fa613268a4a0)  


### Page de détails de produits


La page de détails présente toutes les informations clés d’un produit : promotion, note en étoiles, stock disponible, description, type, couleur, et un bouton pour l’ajouter au panier. En bas, une section propose des produits similaires pour enrichir l’expérience d’achat.  

![image](https://github.com/user-attachments/assets/a56f4468-596a-4fc8-8ec3-2731bd71ae8b)![image](https://github.com/user-attachments/assets/6b24a7c0-d099-45b3-821d-54f643a6d696)  


Lorsqu’un utilisateur clique sur « Ajouter au panier », une pop-up s’ouvre pour proposer des ajouts facultatifs comme du chocolat, un vase ou une peluche. Il peut ajuster les quantités avec « + »/« – ». Cela améliore l’expérience et favorise l’upselling.  


![image](https://github.com/user-attachments/assets/5caf1781-8752-49fe-a6f0-42145f79d3b1)  


### page de panier d’achat

Le panier affiche le produit choisi avec ses ajouts, les quantités modifiables, le total mis à jour automatiquement, et un bouton pour finaliser la commande.  

![image](https://github.com/user-attachments/assets/1381a0bf-6bee-4d80-84a3-84371a40e9b8)  


### Page passer commande 

La page « Passer Commande » guide l’utilisateur en trois étapes : choix du mode de livraison, saisie des coordonnées, et sélection du paiement (carte ou PayPal). Chaque étape est obligatoire, et le total s’ajuste automatiquement. La commande n’est validée que si tous les champs sont correctement remplis.  


![image](https://github.com/user-attachments/assets/219f2a5a-1a8e-4d1d-bb58-3be959cc9b5b)![image](https://github.com/user-attachments/assets/9cf350ab-4db3-4cd0-ad80-76ec423a3847)![image](https://github.com/user-attachments/assets/f676a083-4200-45cc-9ccb-193a49d6c3f8)  


Une fois le paiement effectué, l’utilisateur reçoit un message de confirmation indiquant que la commande a bien été enregistrée  


![image](https://github.com/user-attachments/assets/167dafe1-1109-4b5f-9bac-854012b8224b)  

Une notification s’affiche immédiatement pour informer l’utilisateur que sa commande a été envoyée et qu’il doit attendre la validation de l’administrateur.  

![image](https://github.com/user-attachments/assets/899a1d42-6d8c-48a1-80df-caffce37407c)![image](https://github.com/user-attachments/assets/3594894f-baf9-4e6a-9fd6-39ba51395f8e)  


### Page de favoris 

La page Favoris regroupe tous les produits que l’utilisateur a aimés en cliquant sur l’icône  (like)  

![image](https://github.com/user-attachments/assets/dd531e75-c283-476f-95bf-a0b59abc4da2)  


### Espace admin 


L’administrateur dispose d’une page dédiée où il peut gérer les commandes des utilisateurs.  


![image](https://github.com/user-attachments/assets/64beb8af-7515-44f3-a1dc-b197287d1dbe)  

L’administrateur peut effectuer toutes les actions qu’un utilisateur normal  mais il dispose en plus d’un espace d’administration sécurisé pour gérer et valider les commandes.  

![image](https://github.com/user-attachments/assets/841f7d4f-5247-404f-af80-25d649d82e0e)  

#### 1 Gerer users


Dans cette interface, chaque nouvel utilisateur qui s'inscrit est automatiquement ajouté à la liste "En attente". L'administrateur a la possibilité de gérer ces utilisateurs en choisissant de les accepter ou de les refuser.  

![image](https://github.com/user-attachments/assets/81df40d3-0005-4895-a982-cc20a158b865)  


Dans cette section, on trouve la liste des utilisateurs dont les inscriptions ont été validées par l’administrateur  


![image](https://github.com/user-attachments/assets/c67de263-726d-4f7a-8cfa-f7b1856c8c4a)  


Lorsqu’un utilisateur dont l’inscription a été refusée par l’administrateur tente de se connecter, un message d’erreur s’affiche  

![image](https://github.com/user-attachments/assets/1b6a9944-0715-4d56-8f7a-cc92db66c18e)   


#### 2 Gerer commandes 

Dans l’onglet Commandes, l’administrateur peut consulter les commandes passées par les utilisateurs acceptés.   


![image](https://github.com/user-attachments/assets/cd3e871a-930b-4d58-9a00-cc35fb4ca46f)  


Si l’admin accepte la commande, l’utilisateur reçoit une notification l’informant que sa commande a été validée.   

![image](https://github.com/user-attachments/assets/c5b56573-44db-41ee-9e9e-a60afdce90a0)  

Lorsqu’un utilisateur clique sur "Voir commande" depuis sa notification, il accède à une page de récapitulatif de commande  

![image](https://github.com/user-attachments/assets/f0b69f8b-33e0-4739-9f76-a78df805524e)  


Il peut ensuite accéder à un espace de suivi de commande pour consulter l’état de traitement (en cours, livrée, etc.).    

![image](https://github.com/user-attachments/assets/57471606-40b1-497a-ac08-df2f1d059ddb)  

Une fois la commande livrée avec succès, l'utilisateur reçoit automatiquement un message de confirmation lui indiquant que sa commande a bien été livrée.  


![image](https://github.com/user-attachments/assets/ea4509e1-d9f2-460d-9b26-c16b86d986ec)  













# Welcome to Compteur de Taxi! 🚖

Compteur de Taxi is an Android application designed to help taxi drivers manage their fares efficiently. The app provides features such as driver profiles, fare calculations, and user-friendly interfaces to enhance the driving experience. 🌍✨

## Features 🌟
- **Driver Profile Management**: Allows drivers to create and manage their profiles. 📝
- **Fare Calculation**: Automatically calculates fares based on distance and time. 🕒️
- **User-Friendly Interface**: Designed with modern UI principles for ease of use. 🖥️

## Fonctionnalités de l'Application 🚖

### 1. Écran Principal 🖥️
- Affichage d'une carte Google Maps avec la position actuelle du chauffeur.
- Affichage du compteur de taxi qui inclut :
  - La distance parcourue en kilomètres.
  - Le temps écoulé en minutes.
  - Le tarif total de la course calculé en temps réel.
- Un bouton "Démarrer la course" pour initier le suivi du trajet et démarrer le calcul du tarif.

### 2. Calcul du Tarif 💰
- Un tarif de base (par exemple, 2.5 DH).
- Un tarif par kilomètre (par exemple, 1.5 DH par kilomètre).
- Un tarif par minute (par exemple, 0.5 DH par minute).
- Le tarif total est mis à jour chaque fois que la position change, en fonction de la distance parcourue et du temps écoulé.

### 3. Notifications 🔔
- À la fin de la course, une notification est envoyée pour informer le chauffeur du tarif total de la course.
- La notification inclut des informations sur la distance et le temps total de la course.

### 4. Permissions 🔑
- L'application demande et vérifie la permission d'accès à la localisation.
- Utilisation de la bibliothèque EasyPermissions pour simplifier la gestion des permissions à l'exécution.

### 5. Suivi de la Position en Temps Réel 📍
- Affichage de la position du chauffeur sur la carte en temps réel en utilisant le service FusedLocationProviderClient.
- Mise à jour du compteur de taxi en fonction de la position du chauffeur.
- Calcul de la distance parcourue et du temps écoulé depuis le début de la course.

### 6. Mise à Jour du Tarif 📊
- Le tarif est calculé en fonction de la distance parcourue et du temps écoulé, tenant compte du tarif par kilomètre et par minute.
- La mise à jour du tarif est dynamique et se fait à chaque changement de position.

### Couleurs Principales 🎨
- Jaune Taxi : #FFD700
- Noir : #000000
- Gris Clair : #D3D3D3
- Blanc : #FFFFFF
- Rouge : #FF0000
- Bleu : #007BFF
- Vert : #28A745
- Bleu Clair : #E0F7FF
- Gris Sombre : #343A40

### Icône de l'Application 🖼️
- ![Icône de l'application](https://www.flaticon.com/fr/icone-gratuite/taxi_619006?related_id=619127&origin=search)

## Technologies Used 🛠️
- Kotlin
- Android SDK
- Android Studio

## Getting Started 🚀
To get started, clone this repository and follow the installation instructions below:

1. Clone the repository:
   ```bash
   git clone https://github.com/zenitsudono/Compteur-de-Taxi.git
   ```
2. Open the project in Android Studio.
3. Build and run the application on an Android device or emulator.

## Contributing 🤝
Feel free to submit issues or pull requests to improve the application.

## License 📄
This project is licensed under the MIT License.

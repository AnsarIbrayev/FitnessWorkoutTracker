# FitnessWorkoutTracker

## Description
FitnessWorkoutTracker is an Android application developed using Kotlin and Jetpack Compose.
The app allows users to search for exercises using a public REST API and view detailed
information about each exercise.

## Features
- Exercise search with debounce
- Pagination (Load more)
- Exercise details screen
- Bottom navigation (Search, Add Workout, Profile)
- REST API integration (wger.de)

## API
- https://wger.de/api/v2/exercise/
- https://wger.de/api/v2/exerciseinfo/{id}/

## Technologies
- Kotlin
- Jetpack Compose
- Retrofit
- Coroutines
- Navigation Compose

## Navigation
The app uses bottom navigation with three main screens:
- Search
- Add Workout
- Profile

## Author
Ansar Ibrayev

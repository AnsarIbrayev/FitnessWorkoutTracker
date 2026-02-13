# Architecture

## Overview
The app is built with a simple layered architecture:

UI (Compose Screens)
→ ViewModel (state + UI logic)
→ Repository (data access abstraction)
→ Data Sources (Firebase / local cache)
→ Models (data classes)

## Data flow (example)
User action (tap/refresh)
→ ViewModel calls Repository
→ Repository loads data from Firebase
→ ViewModel updates UI state (Loading/Success/Error)
→ UI renders the state (list / empty / error)

## Key modules
- UI: Jetpack Compose screens and reusable components
- ViewModels: screen state, events, and validation
- Data: repository + Firebase services

package kz.aitu.fitnessworkouttracker.di

import kz.aitu.fitnessworkouttracker.data.api.ApiClient
import kz.aitu.fitnessworkouttracker.data.local.AppDatabase
import kz.aitu.fitnessworkouttracker.repository.ExerciseRepository
import kz.aitu.fitnessworkouttracker.repository.WorkoutRepository
import kz.aitu.fitnessworkouttracker.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // ✅ Room db + dao
    single { AppDatabase.create(get()) }
    single { get<AppDatabase>().exerciseDao() }

    // ✅ API
    single { ApiClient.api }

    // ✅ repos
    single { ExerciseRepository(api = get(), dao = get()) }
    single { WorkoutRepository() }

    // ✅ ViewModel (без параметров)
    viewModel { SearchViewModel(get()) }
}

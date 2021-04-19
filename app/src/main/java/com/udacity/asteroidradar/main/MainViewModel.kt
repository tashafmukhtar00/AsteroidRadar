package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.NasaAsteroidApiFilter
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import timber.log.Timber


enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidRepository(database)

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus> get() = _status

    /**
     * This is the overflow menu filter. Start by showing all saved
     */
    private val _filterSelected =
        MutableLiveData<NasaAsteroidApiFilter>(NasaAsteroidApiFilter.SHOW_SAVED)
    val filterSelected: LiveData<NasaAsteroidApiFilter>
        get() = _filterSelected


    val asteroids = Transformations.switchMap(_filterSelected) {
        when (it!!) {
            NasaAsteroidApiFilter.SHOW_WEEK -> asteroidsRepository.weeklyAsteroids
            NasaAsteroidApiFilter.SHOW_TODAY -> asteroidsRepository.todayAsteroids
            else -> asteroidsRepository.asteroids
        }
    }

    val pictureOfDay = asteroidsRepository.pictureOfDay

    /**
     * If this is non-null, immediately navigate to [DetailFragment] and call [doneNavigating]
     */
    private val _navigateToDetail = MutableLiveData<Asteroid>()

    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    /**
     * Call this immediately after navigating to [DetailFragment]
     * It will clear the navigation request, so if the user rotates their phone it won't navigate
     * twice.
     */
    fun doneNavigating() {
        _navigateToDetail.value = null
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        viewModelScope.launch {
            try {
                _status.value = AsteroidApiStatus.LOADING
                asteroidsRepository.refreshAsteroids()
            } catch (e: Exception) {
                Timber.e("Could not load asteroids $e")
                _status.value = AsteroidApiStatus.ERROR
            } finally {
                _status.value = AsteroidApiStatus.DONE
            }
        }
        viewModelScope.launch {
            asteroidsRepository.refreshPictureOfDay()
        }

    }

    // this will be observed in the fragment and cause doFilter to run, which in turn
    // modifies which source of asteroids from the repository are used
    fun updateFilter(filter: NasaAsteroidApiFilter) {
        Timber.d("Update _filterSelected with ${filter.toString()}")
        _filterSelected.value = filter
    }

    /**
     * Factory for constructing MainViewModel with parameter (application)
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
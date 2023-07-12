package ca.rheinmetall.atak.model

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointOfInterestRepository @Inject constructor(){
    private val _pointOfInterests: MutableMap<String, PointOfInterest> = HashMap()
    fun addPointOfInterests(newPointOfInterests: List<PointOfInterest>) {
        newPointOfInterests.forEach { _pointOfInterests[it.uuid] = it }
        pointOfInterests.postValue(_pointOfInterests.values.toList())
    }

    val pointOfInterests: MutableLiveData<List<PointOfInterest>> = MutableLiveData(ArrayList())
}
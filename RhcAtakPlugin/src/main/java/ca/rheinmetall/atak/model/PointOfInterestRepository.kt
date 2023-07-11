package ca.rheinmetall.atak.model

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointOfInterestRepository @Inject constructor(){
    val pointOfInterests: MutableLiveData<List<PointOfInterest>> = MutableLiveData(ArrayList())

    init {
        //demo
        pointOfInterests.value = listOf(PointOfInterest(45.72, -73.55, PointOfInterestIcon.BANK))
    }
}
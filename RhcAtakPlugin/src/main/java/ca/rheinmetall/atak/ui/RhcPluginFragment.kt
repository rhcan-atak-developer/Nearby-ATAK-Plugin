package ca.rheinmetall.atak.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.rheinmetall.atak.PointOfInterestRestClient
import ca.rheinmetall.atak.RetrofitEventListener
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.databinding.RhcPluginFragmentBinding
import ca.rheinmetall.atak.json.PointOfInterestResponse
import retrofit2.Call
import javax.inject.Inject

class RhcPluginFragment @Inject constructor(@param:PluginContext private val _pluginContext: Context) : Fragment() {
    private var _binding: RhcPluginFragmentBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RhcPluginFragmentBinding.inflate(LayoutInflater.from(_pluginContext))
        callUserListData()
        return _binding!!.root
    }

    internal fun callUserListData() {

        PointOfInterestRestClient.instance.getUserList( object : RetrofitEventListener {
            override  fun onSuccess(call: Call<*>, response: Any) {
                if (response is PointOfInterestResponse) {
                    Log.d("pbolduc", "-----" + response.pointOfInterestResponseData!!.results.size)
                    Log.d("pbolduc", response.toString());
                }
            }

            override fun onError(call: Call<*>, t: Throwable) {
                // snack bar that city can not find
                Log.e("pbolduc", "onError: $call", t )
            }
        })
    }
}
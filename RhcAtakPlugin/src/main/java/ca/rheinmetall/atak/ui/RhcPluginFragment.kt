package ca.rheinmetall.atak.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.rheinmetall.atak.PointOfInterestRestClient
import ca.rheinmetall.atak.RetrofitEventListener
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.dagger.ViewModelFactory
import ca.rheinmetall.atak.databinding.RhcPluginFragmentBinding
import ca.rheinmetall.atak.json.PointOfInterestResponse
import retrofit2.Call
import javax.inject.Inject

class RhcPluginFragment @Inject constructor(
    @PluginContext private val pluginContext: Context,
    private val viewModelFactory: ViewModelFactory
) : Fragment() {
    private lateinit var binding: RhcPluginFragmentBinding
    private lateinit var viewModel: PointOfInterestViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RhcPluginFragmentBinding.inflate(LayoutInflater.from(pluginContext))
        callUserListData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, viewModelFactory)[PointOfInterestViewModel::class.java]

    }

    internal fun callUserListData() {

        PointOfInterestRestClient.instance.getUserList(object : RetrofitEventListener {
            override fun onSuccess(call: Call<*>, response: Any) {
                if (response is PointOfInterestResponse) {
                    Log.d("pbolduc", "-----" + response.pointOfInterestResponseData!!.results.size)
                    Log.d("pbolduc", response.toString());
                }
            }

            override fun onError(call: Call<*>, t: Throwable) {
                // snack bar that city can not find
                Log.e("pbolduc", "onError: $call", t)
            }
        })
    }
}
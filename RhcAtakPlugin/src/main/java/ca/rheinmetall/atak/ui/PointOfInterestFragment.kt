package ca.rheinmetall.atak.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.rheinmetall.atak.R
import ca.rheinmetall.atak.RetrofitEventListener
import ca.rheinmetall.atak.Severity
import ca.rheinmetall.atak.TrafficIncidentRestClient
import ca.rheinmetall.atak.TrafficIncidentType
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.dagger.ViewModelFactory
import ca.rheinmetall.atak.databinding.RhcPluginFragmentBinding
import ca.rheinmetall.atak.json.route.TrafficIncidentResponse
import ca.rheinmetall.atak.model.PointOfInterestType
import retrofit2.Call
import javax.inject.Inject

class PointOfInterestFragment @Inject constructor(
    @PluginContext private val pluginContext: Context,
    private val viewModelFactory: ViewModelFactory,
    private val trafficIncidentRestClient: TrafficIncidentRestClient
) : Fragment() {
    private lateinit var binding: RhcPluginFragmentBinding
    private lateinit var viewModel: PointOfInterestViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RhcPluginFragmentBinding.inflate(LayoutInflater.from(pluginContext))
        callTrafficAPI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, viewModelFactory)[PointOfInterestViewModel::class.java]
        binding.selectCategoriesButton.setOnClickListener { onSelectCategoriesPressed() }
        viewModel.selectedCategories.observe(viewLifecycleOwner) { onSelectedCategoriesChanged(it)}

        binding.selectIncidentTypeBtn.setOnClickListener { onSelectIncidentType() }
        viewModel.selectedTrafficIncidentType.observe(viewLifecycleOwner) { onSelectedTrafficIncidentChange(it)}

        binding.selectSeverityTypeBtn.setOnClickListener { onSelectSeverity() }
        viewModel.selectedSeverity.observe(viewLifecycleOwner) { onSelectedSeverityChanged(it)}

        binding.searchButton.setOnClickListener { viewModel.searchPointOfInterests() }
    }

    private fun onSelectedSeverityChanged(it: Severity) {
        binding.selectedSeverityHint.text = it.name
    }

    private fun onSelectSeverity() {
        val severity = Severity.values()
        AlertDialog.Builder(requireContext())
            .setTitle(pluginContext.getString(R.string.select_severity))
            .setSingleChoiceItems(severity.map { it.name }.toTypedArray(), viewModel.selectedSeverity.value!!.severityCode -1) { _, i ->
                viewModel.selectSeverity(severity[i])
            }
            .setPositiveButton(pluginContext.getText(R.string.ok)) { _, i -> if(i != -1) viewModel.selectSeverity(severity[i])}
            .show()
    }

    private fun onSelectedTrafficIncidentChange(it: TrafficIncidentType) {
        binding.selectedIncidentHint.text = it.name
    }

    private fun onSelectIncidentType() {
        val type = TrafficIncidentType.values()
        AlertDialog.Builder(requireContext())
            .setTitle(pluginContext.getString(R.string.select_incident_type))
            .setSingleChoiceItems(type.map { it.name }.toTypedArray(), viewModel.selectedTrafficIncidentType.value!!.typeCode -1) { _, i ->
                viewModel.selectTrafficIncident(
                    type[i]
                )
            }
            .setPositiveButton(pluginContext.getText(R.string.ok)) { _, i -> if(i != -1) viewModel.selectTrafficIncident(type[i])}
            .show()
    }

    private fun onSelectedCategoriesChanged(selectedCategories: List<PointOfInterestType>) {
        val value = selectedCategories.joinToString(separator = ", ") { pluginContext.getString(it.stringRes) }
        if (value.isEmpty())
            binding.selectedCategoriesHint.text = pluginContext.getText(R.string.no_categories_selected)
        else
            binding.selectedCategoriesHint.text = value
    }

    private fun onSelectCategoriesPressed() {
        val categories = PointOfInterestType.values()
        val choicesWithCheckedState = categories.map { Pair(pluginContext.getString(it.stringRes), viewModel.selectedCategories.value?.contains(it) ?: false) }.toTypedArray()
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(pluginContext.getString(R.string.select_categories))
            .setMultiChoiceItems(
                choicesWithCheckedState.map { it.first }.toTypedArray(),
                choicesWithCheckedState.map { it.second }.toBooleanArray()
            ) { _, which, isChecked -> choicesWithCheckedState[which] = Pair(choicesWithCheckedState[which].first, isChecked) }
            .setPositiveButton(pluginContext.getText(R.string.ok)) { _, _ -> onPositiveButtonClicked(categories, choicesWithCheckedState) }
            .setNeutralButton(pluginContext.getText(R.string.unselect), null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            for (i in choicesWithCheckedState.indices)
                choicesWithCheckedState[i] = Pair(choicesWithCheckedState[i].first, false)

            dialog.listView.clearChoices()
            dialog.listView.children.map { it as Checkable }.forEach { it.isChecked = false }
        }
    }

    private fun onPositiveButtonClicked(categories: Array<PointOfInterestType>, choicesWithCheckedState: Array<Pair<String, Boolean>>) {
        viewModel.selectCategories(categories.mapIndexed { i, category -> Pair(choicesWithCheckedState[i].second, category) })
    }

    private fun callTrafficAPI() {
        trafficIncidentRestClient.retrofitEventListener( object : RetrofitEventListener {
            override  fun onSuccess(call: Call<*>, response: Any) {
                if (response is TrafficIncidentResponse) {
                    response.trafficIncidentResponseData.forEach{it.resources.forEach { Log.d("trafficIncident", it.description?:"") }}
                }
            }

            override fun onError(call: Call<*>, t: Throwable) {
                Log.e("trafficIncident", "onError: $call", t )
            }
        })
    }
}
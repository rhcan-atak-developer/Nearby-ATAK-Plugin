package ca.rheinmetall.atak.ui.incidents

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.rheinmetall.atak.R
import ca.rheinmetall.atak.Severity
import ca.rheinmetall.atak.TrafficIncidentType
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.dagger.ViewModelFactory
import ca.rheinmetall.atak.databinding.IncidentsFragmentBinding
import javax.inject.Inject

class IncidentsFragment @Inject constructor(
    @PluginContext private val pluginContext: Context,
    private val viewModelFactory: ViewModelFactory
) : Fragment() {
    private lateinit var binding: IncidentsFragmentBinding
    private lateinit var viewModel: IncidentsViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = IncidentsFragmentBinding.inflate(LayoutInflater.from(pluginContext))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, viewModelFactory)[IncidentsViewModel::class.java]

        binding.selectIncidentTypeBtn.setOnClickListener { onSelectIncidentType() }
        viewModel.selectedTrafficIncidentType.observe(viewLifecycleOwner) { onSelectedTrafficIncidentChange(it)}

        binding.selectSeverityTypeBtn.setOnClickListener { onSelectSeverity() }
        viewModel.selectedSeverity.observe(viewLifecycleOwner) { onSelectedSeverityChanged(it)}
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
}
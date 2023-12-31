package ca.rheinmetall.atak.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.rheinmetall.atak.R
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.dagger.ViewModelFactory
import ca.rheinmetall.atak.databinding.PointOfInterestFragmentBinding
import ca.rheinmetall.atak.model.PointOfInterestType
import javax.inject.Inject

class PointOfInterestFragment @Inject constructor(
    @PluginContext private val pluginContext: Context,
    private val viewModelFactory: ViewModelFactory
) : Fragment() {
    private lateinit var binding: PointOfInterestFragmentBinding
    private lateinit var viewModel: PointOfInterestViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PointOfInterestFragmentBinding.inflate(LayoutInflater.from(pluginContext))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, viewModelFactory)[PointOfInterestViewModel::class.java]
        binding.selectCategoriesButton.setOnClickListener { onSelectCategoriesPressed() }
        viewModel.selectedCategories.observe(viewLifecycleOwner) { onSelectedCategoriesChanged(it)}

        binding.searchButton.setOnClickListener { viewModel.searchPointOfInterests() }
        viewModel.selectedOption.observe(viewLifecycleOwner) { onSelectedOptionChanged(it)}
        binding.selfRadioButton.setOnClickListener() { viewModel.selectSelfOption() }
        binding.viewportRadioButton.setOnClickListener() { viewModel.selectViewportOption() }
        binding.reduceRadiusButton.setOnClickListener { viewModel.reduceRadius() }
        binding.increaseRadius.setOnClickListener { viewModel.increaseRadius() }
        viewModel.radius.observe(viewLifecycleOwner) { binding.radiusValue.text = "${it} km" }
    }

    private fun onSelectedOptionChanged(type: SearchType?) {
        binding.selfRadioButton.isChecked = type == SearchType.SELF
        binding.viewportRadioButton.isChecked = type == SearchType.VIEWPORT
        binding.radiusOptionViewGroup.isEnabled = type == SearchType.SELF
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
        val checkedArray = choicesWithCheckedState.map { it.second }.toBooleanArray()
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(pluginContext.getString(R.string.select_categories))
            .setMultiChoiceItems(
                choicesWithCheckedState.map { it.first }.toTypedArray(),
                checkedArray
            ) { _, which, isChecked -> choicesWithCheckedState[which] = Pair(choicesWithCheckedState[which].first, isChecked) }
            .setPositiveButton(pluginContext.getText(R.string.ok)) { _, _ -> onPositiveButtonClicked(categories, choicesWithCheckedState) }
            .setNeutralButton(pluginContext.getText(R.string.unselect), null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            for (i in choicesWithCheckedState.indices) {
                choicesWithCheckedState[i] = Pair(choicesWithCheckedState[i].first, false)
                checkedArray[i] = false
            }

            dialog.listView.clearChoices()
            dialog.listView.children.map { it as Checkable }.forEach { it.isChecked = false }
        }
    }

    private fun onPositiveButtonClicked(categories: Array<PointOfInterestType>, choicesWithCheckedState: Array<Pair<String, Boolean>>) {
        viewModel.selectCategories(categories.mapIndexed { i, category -> Pair(choicesWithCheckedState[i].second, category) })
    }
}

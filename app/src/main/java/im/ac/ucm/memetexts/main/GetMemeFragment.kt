package im.ac.ucm.memetexts.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import im.ac.ucm.memetexts.databinding.FragmentGetMemeBinding
import im.ac.ucm.memetexts.VolleyManager

class GetMemeFragment : Fragment(), OnItemSelectedListener {
    private var _binding: FragmentGetMemeBinding? = null
    private val binding get() = _binding!!

    private var caller: Caller? = null

    private lateinit var itemSelected: String
    private lateinit var noItemSelected: String

    /**Declares structure for methods in the caller*/
    interface Caller {
        fun getSpinnerOptions(): SpinnerAdapter
        fun getUrl(): String
        fun successResponse(response: String, tView: TextView, button: Button)
        fun failResponse(response: String, tView: TextView, button: Button, vm: VolleyManager)
    }

    /**Links to caller context when fragment is attached to an activity*/
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Caller){
            caller = context
        }
    }

    /**Inflates the layout as binding, sets listeners for the button and spinner and sets a default option.*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetMemeBinding.inflate(layoutInflater, container, false)

        binding.button.setOnClickListener { buttonClick() }

        binding.spinner.adapter = caller?.getSpinnerOptions()
        binding.spinner.onItemSelectedListener = this

        noItemSelected = binding.spinner.adapter.getItem(0).toString()

        return binding.root
    }

    /**Creates specific API URL dependent on whether there is a parameter passed or there is a selected spinner option.
     * @param extra specific parameter to add to the API URL*/
    private fun fullUrl(extra: String = ""): String{
        var url = caller?.getUrl()
        if(extra != ""){
            return url + extra
        }
        url += if(itemSelected != noItemSelected){
            itemSelected
        }
        else{
            ""
        }
        return url!!
    }

    /**Overload handles buttonClick when there is no given parameter.*/
    private fun buttonClick(){
        buttonClick("")
    }

    /**Calls the API using the given url, and with the success and failure responses from the caller activity.
     * @param extra any additional parameter to add onto the API URL*/
    fun buttonClick(extra: String){
        binding.button.isEnabled = false
        val url = fullUrl(extra)
        val vm = VolleyManager(caller as Context)
        vm.call(
            url,
            { response ->
                caller?.successResponse(response.toString(), binding.textView, binding.button)
            },
            { response ->
                caller?.failResponse(response.toString(), binding.textView, binding.button, vm)
            }
        )
    }

    /**Removes the link to the caller Activity once fragment is no longer needed.*/
    override fun onDetach() {
        super.onDetach()
        caller = null
    }

    /**Clears the binding once the fragment is no longer needed.*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**Sets the selected value for the spinner when one is selected by the user.*/
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        itemSelected = parent.getItemAtPosition(position).toString()
    }

    /**Sets a default value for the spinner if the user does not select one.*/
    override fun onNothingSelected(parent: AdapterView<*>) {
        itemSelected = parent.getItemAtPosition(0).toString()
    }

    /**Refreshes the spinner options whenever the user returns to the activity.*/
    override fun onResume() {
        super.onResume()
        binding.spinner.adapter = caller?.getSpinnerOptions()
    }

}
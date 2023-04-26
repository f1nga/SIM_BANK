package com.bluemeth.simbank.src.ui.drawer.contacts.view_contact_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentViewContactProfileBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.models.utils.NotificationType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.drawer.contacts.ContactsViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.BizumFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.TransferFormViewModel
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewContactProfileFragment : Fragment() {

    private lateinit var binding: FragmentViewContactProfileBinding

    private val viewContactProfileViewModel: ViewContactProfileViewModel by activityViewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val contactsViewModel: ContactsViewModel by viewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private val transferFormViewModel: TransferFormViewModel by activityViewModels()

    private lateinit var contact: User

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewContactProfileBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        contact = viewContactProfileViewModel.contact
        initListeners()
        initObservers()
        setTextViews()
    }

    private fun initObservers() {
        viewContactProfileViewModel.navigateToHome.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                toast("Se ha enviado la solicitud")
                missionDoned()
                goToHome()
            }
        }

        contactsViewModel.contactBlocked.observe(requireActivity()) {
            toast("Se ha bloqueado al contacto")
            goToAddContact()
        }
    }

    private fun initListeners() {
        binding.llDoBizum.setOnClickListener {
            bizumFormViewModel.setReUseBizumArguments(
                BizumFormModel(
                    "",
                    "",
                    ContactBizum(
                        name = contact.name,
                        phoneNumber = contact.phone
                    )
                )
            )

            goToBizumForm()
        }

        binding.llDoTransfer.setOnClickListener {
            transferFormViewModel.setContactTransfer(contact.name)
            goToTransferForm()
        }

        binding.llAddContact.setOnClickListener { showQuestionDialog() }

        binding.tvBlockContact.setOnClickListener { showBlockContactQuestionDialog() }
    }

    private fun sendNotification(user: User, newContact: User) {
        viewContactProfileViewModel.insertNotificationToDB(
            Notification(
                title = getString(R.string.noti_contact_request_title),
                description = getString(R.string.noti_contact_request_description) + " ${user.name}",
                type = NotificationType.ContactRequest,
                user_send_email = user.email,
                user_receive_email = newContact.email
            )
        )
    }

    private fun blockContact(blockedContact: User) {
        globalViewModel.getUserFromDB().observe(requireActivity()) {
            contactsViewModel.addUserBlockedContactToDB(it, blockedContact)
        }
    }

    private fun showQuestionDialog() {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = getString(R.string.dialog_send_noti_body),
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(
                    getString(R.string.dialog_send_noti_help),
                    Toast.LENGTH_LONG
                )
            },
            negativeAction = QuestionDialog.Action(getString(R.string.cancel)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action(getString(R.string.send)) {
                globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                    sendNotification(user, contact)
                    it.dismiss()
                }
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun showBlockContactQuestionDialog() {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = "¿Seguro que quieres bloquear a este contacto?",
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(
                    "No te aparecerá este contacto",
                    Toast.LENGTH_LONG
                )
            },
            negativeAction = QuestionDialog.Action(getString(R.string.cancel)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action("Bloquear") {
                blockContact(contact)
                it.dismiss()
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun missionDoned() {
        globalViewModel.setUserMissionToDB(Constants.ADD_CONTACT_MISSION)
    }

    private fun setTextViews() {
        with(binding) {
            Picasso.get().load(contact.image).into(ivCircle)
            if (contact.image == Constants.DEFAULT_PROFILE_IMAGE) {
                tvCircleName.text = Methods.splitNameAndSurname(contact.name)
            }
            tvTotalContacts.text = contact.contacts.size.toString()
            tvFullName.text = contact.name
            viewContactProfileViewModel.getTotalMovementsByUserFromDB(contact.email, contact.name)
                .observe(requireActivity()) {
                    tvTotalMovements.text = it.toString()
                }

            viewContactProfileViewModel.getCommonUserContactsFromDB(
                globalViewModel.getUserAuth().email!!,
                contact.email
            ).observe(requireActivity()) {
                tvComunContacts.text = "Teneis $it contactos en común"
            }

            if (arguments?.getBoolean("friend") == true) {
                llAddContact.isVisible = arguments?.getBoolean("friend") != true
            }

        }
    }

    private fun goToHome() {
        view?.findNavController()?.navigate(R.id.action_viewContactProfileFragment_to_homeFragment)
    }

    private fun goToBizumForm() {
        val bundle = bundleOf(Constants.FORM_TYPE to Constants.SEND_MONEY, Constants.REUSE to false)
        view?.findNavController()
            ?.navigate(R.id.action_viewContactProfileFragment_to_bizumFormFragment, bundle)
    }

    private fun goToTransferForm() {
        view?.findNavController()
            ?.navigate(R.id.action_viewContactProfileFragment_to_transferFormFragment)
    }

    private fun goToAddContact() {
        view?.findNavController()
            ?.navigate(R.id.action_viewContactProfileFragment_to_addContactFragment)
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_contact_profile)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_contactsFragment_to_notificationsFragment)
            }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!)
                .observe(requireActivity()) { isReaded ->
                    it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
                }
        }
    }
}
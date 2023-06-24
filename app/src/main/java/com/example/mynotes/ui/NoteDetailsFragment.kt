package com.example.mynotes.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.R
import com.example.mynotes.databinding.DeleteNoteLayoutBinding
import com.example.mynotes.databinding.FragmentNoteDetailsBinding
import com.example.mynotes.domain.model.Note
import com.example.mynotes.util.RealFilePath
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class NoteDetailsFragment : Fragment() {

    companion object {
        const val REQUEST_CODE_STORAGE_PERMISSION = 1
        const val REQUEST_CODE_SELECT_IMAGE = 2
        private const val TAG = "NoteDetailsFragment"
    }

    private lateinit var binding: FragmentNoteDetailsBinding
    private val noteViewModel by activityViewModels<NoteViewModel>()
    private lateinit var selectedNoteColor: String
    private lateinit var viewTitleIndicator: View

    private val currentDate = SimpleDateFormat.getInstance().format(Date())

    private val args: NoteDetailsFragmentArgs by navArgs()

    private lateinit var selectImageLauncher: ActivityResultLauncher<String>

    private lateinit var pickMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var selectedImagePath: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoteDetailsBinding.inflate(inflater, container, false)


        selectedNoteColor = "#333333"
        viewTitleIndicator = binding.viewTitleIndicator
        selectedImagePath = ""

        initMiscellaneous()
        setTitleIndicatorColor()

        selectImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    try {
                        Toast.makeText(
                            requireContext(),
                            "selectImageLauncher called",
                            Toast.LENGTH_SHORT
                        ).show()
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageNote.apply {
                            setImageBitmap(bitmap)
                            visibility = View.VISIBLE
                        }
                        binding.removeImageIV.visibility = View.VISIBLE

                        selectedImagePath = RealFilePath.getPath(requireContext(), uri).toString()

                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), ex.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


        pickMediaLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    try {
//                        Toast.makeText(
//                            requireContext(),
//                            "selectImageLauncher called",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageNote.apply {
                            setImageBitmap(bitmap)
                            visibility = View.VISIBLE
                        }
                        binding.removeImageIV.visibility = View.VISIBLE
                        selectedImagePath = RealFilePath.getPath(requireContext(), uri).toString()
                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), ex.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textDateTime.text =
            SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(Date())

        if (args.note != null) {
            viewCurrentNote()
            binding.included.layoutDeleteNote.apply {
                visibility = View.VISIBLE
                setOnClickListener {

                    val deleteNoteLayoutBinding = DeleteNoteLayoutBinding.inflate(layoutInflater)

                    val dialog = MaterialAlertDialogBuilder(requireContext())
                        .setView(deleteNoteLayoutBinding.root)
                        .setPositiveButton(
                            "DELETE NOTE"
                        ) { dialog, _ ->
                            noteViewModel.deleteNote(args.note!!)
                            dialog.dismiss()
                            findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrDeleteFragment())
                        }
                        .setNegativeButton(
                            "CANCEL"
                        ) { dialog, _ -> dialog?.cancel() }
                        .setCancelable(true)
                        .create()

                    dialog.show()
                }
            }
        } else {
            binding.included.layoutDeleteNote.visibility = View.GONE
        }



        binding.apply {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }

            imageSave.setOnClickListener {
                if (args.note == null) {
                    saveNote()
                } else {
                    editNote()
                }
                findNavController().navigateUp()
            }

            removeImageIV.setOnClickListener {
                imageNote.setImageBitmap(null)
                imageNote.visibility = View.GONE
                removeImageIV.visibility = View.GONE
                selectedImagePath = ""
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permission is granted, you can proceed with the required operation
                    // Call your method or perform the desired action here

                    selectImage()

                } else {

                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Permission rationale should be shown
                        showPermissionRationaleDialog()
                    }


                }
            }

    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("This permission is required to perform the operation.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Deny") { _, _ ->
                // Handle the case when the user denies the permission
            }
            .show()
    }


    private fun saveNote() {
        if (binding.inputNoteTitle.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Note Title can't be empty", Toast.LENGTH_SHORT).show()
            return
        } else if (binding.inputNote.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Note content can't be empty", Toast.LENGTH_SHORT)
                .show()
        }

        val newNote = Note(
            0, title = binding.inputNoteTitle.text.toString(),
            noteContent = binding.inputNote.text.toString(),
            dateTime = binding.textDateTime.text.toString(),
            color = selectedNoteColor,
            imagePath = selectedImagePath
        )
        noteViewModel.addNote(newNote)
    }

    private fun viewCurrentNote() {
        selectedNoteColor = args.note?.color.toString()
        binding.apply {
            inputNoteTitle.setText(args.note?.title)
            inputNote.setText(args.note?.noteContent)
            val gradientDrawable = viewTitleIndicator.background as GradientDrawable
            gradientDrawable.setColor(Color.parseColor(args.note?.color))

            if (!args.note?.imagePath.isNullOrEmpty()) {
                imageNote.setImageBitmap(BitmapFactory.decodeFile(args.note?.imagePath))
                imageNote.visibility = View.VISIBLE
                removeImageIV.visibility = View.VISIBLE
            }
        }
    }

    private fun editNote() {

        val editedNote = args.note?.let {
            Note(
                it.id,
                binding.inputNoteTitle.text.toString(),
                binding.inputNote.text.toString(),
                binding.textDateTime.text.toString(),
                selectedImagePath,
                selectedNoteColor
            )
        }

        if (editedNote != null) {
            noteViewModel.editNote(editedNote)
        }
    }

    private fun initMiscellaneous() {
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(binding.included.miscellaneousLayout)

        binding.included.miscellaneousTV.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

//        if (args.note?.color != null) {
//
//        }

        binding.included.apply {
            viewColor1.setOnClickListener {
                selectedNoteColor = "#333333"
                imageColor1.setImageResource(R.drawable.ic_done)
                imageColor2.setImageResource(0)
                imageColor3.setImageResource(0)
                imageColor4.setImageResource(0)
                imageColor5.setImageResource(0)
                setTitleIndicatorColor()
            }

            viewColor2.setOnClickListener {
                selectedNoteColor = "#FDBE3B"
                imageColor1.setImageResource(0)
                imageColor2.setImageResource(R.drawable.ic_done)
                imageColor3.setImageResource(0)
                imageColor4.setImageResource(0)
                imageColor5.setImageResource(0)
                setTitleIndicatorColor()
            }

            viewColor3.setOnClickListener {
                selectedNoteColor = "#FF4842"
                imageColor1.setImageResource(0)
                imageColor2.setImageResource(0)
                imageColor3.setImageResource(R.drawable.ic_done)
                imageColor4.setImageResource(0)
                imageColor5.setImageResource(0)
                setTitleIndicatorColor()
            }

            viewColor4.setOnClickListener {
                selectedNoteColor = "#3A52FC"
                imageColor1.setImageResource(0)
                imageColor2.setImageResource(0)
                imageColor3.setImageResource(0)
                imageColor4.setImageResource(R.drawable.ic_done)
                imageColor5.setImageResource(0)
                setTitleIndicatorColor()
            }

            viewColor5.setOnClickListener {
                selectedNoteColor = "#000000"
                imageColor1.setImageResource(0)
                imageColor2.setImageResource(0)
                imageColor3.setImageResource(0)
                imageColor4.setImageResource(0)
                imageColor5.setImageResource(R.drawable.ic_done)
                setTitleIndicatorColor()
            }

            if (!args.note?.color.isNullOrEmpty()) {
                when (args.note?.color) {
                    "#333333" -> {
                        viewColor1.performClick()
                    }

                    "#FDBE3B" -> {
                        viewColor2.performClick()
                    }

                    "#FF4842" -> {
                        viewColor3.performClick()
                    }

                    "#3A52FC" -> {
                        viewColor4.performClick()
                    }

                    "#000000" -> {
                        viewColor5.performClick()
                    }

                    else -> viewColor1.performClick()


                }
            }


            layoutAddImage.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
//                    requestPermissions(
//                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//                        REQUEST_CODE_STORAGE_PERMISSION
//                    )


                    // No need to show permission rationale, request the permission directly
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)


                } else {
                    selectImage()
                }

            }

        }


    }

    private fun selectImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            selectImageLauncher.launch("image/*")
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION)
            }
        }
    }


    private fun setTitleIndicatorColor() {
        val gradientDrawable = viewTitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor))
    }
}
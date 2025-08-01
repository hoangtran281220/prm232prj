package com.example.prm232rj.ui.screen.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm232rj.R;
import com.example.prm232rj.data.dto.TagDto;
import com.example.prm232rj.databinding.FragmentFilterComicDialogBinding;
import com.example.prm232rj.ui.viewmodel.TagViewModel;
import com.example.prm232rj.utils.FilterPrefManager;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterComicDialogFragment extends DialogFragment {

    private FragmentFilterComicDialogBinding binding;
    private TagViewModel tagViewModel;
    private FilterPrefManager filterPrefManager;
    private final List<String> selectedTagIds = new ArrayList<>();
    private final FilterListener listener;

    public interface FilterListener {
        void onFilterApplied(List<String> tagIds, String sort, String status);
    }

    public FilterComicDialogFragment(FilterListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), com.google.android.material.R.style.Base_Theme_AppCompat_Dialog);
        LayoutInflater inflater = getLayoutInflater();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_comic_dialog, null, false);
        builder.setView(binding.getRoot());

        tagViewModel = new ViewModelProvider(requireActivity()).get(TagViewModel.class);
        filterPrefManager = new FilterPrefManager(requireContext());

        setupSpinner();
        restoreSelections();
        observeTags();
        setupListeners();

        return builder.create();
    }

    private void setupSpinner() {
        List<String> sortOptions = Arrays.asList("Ngày cập nhật", "Lượt xem", "Đánh giá");
        binding.spinnerSort.setAdapter(new android.widget.ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                sortOptions
        ));
    }

    private void restoreSelections() {
        selectedTagIds.clear();
        selectedTagIds.addAll(filterPrefManager.getSavedTagIds());

        String savedSort = filterPrefManager.getSavedSort();
        String savedStatus = filterPrefManager.getSavedStatus();

        switch (savedSort) {
            case "Views": binding.spinnerSort.setSelection(1); break;
            case "Rating": binding.spinnerSort.setSelection(2); break;
            default: binding.spinnerSort.setSelection(0);
        }

        switch (savedStatus) {
            case "Hoàn thành": binding.radioGroupStatus.check(R.id.radioCompleted); break;
            case "Đang tiến hành": binding.radioGroupStatus.check(R.id.radioOngoing); break;
            default: binding.radioGroupStatus.check(R.id.radioAll);
        }
    }

    private void observeTags() {
        tagViewModel.getTags().observe(this, tags -> {
            binding.chipGroupTags.removeAllViews();
            List<String> selectedTagNames = new ArrayList<>();

            for (TagDto tag : tags) {
                Chip chip = new Chip(requireContext());
                chip.setText(tag.getName());
                chip.setTag(tag.getId());
                chip.setCheckable(true);

                boolean isChecked = selectedTagIds.contains(tag.getId());
                chip.setChecked(isChecked);
                updateChipStyle(chip, isChecked);

                if (isChecked) selectedTagNames.add(tag.getName());

                chip.setOnCheckedChangeListener((buttonView, isCheckedNow) -> {
                    String id = (String) buttonView.getTag();
                    if (isCheckedNow) {
                        if (selectedTagIds.size() >= 10) {
                            // Nếu đã đủ 10 tag, không cho chọn thêm và bỏ check lại
                            buttonView.setChecked(false);
                            Toast.makeText(getContext(), "Chỉ được chọn tối đa 10 thể loại", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!selectedTagIds.contains(id)) selectedTagIds.add(id);
                        }
                    } else {
                        selectedTagIds.remove(id);
                    }
                    updateChipStyle((Chip) buttonView, isCheckedNow);
                    updateSelectedTagsText();
                });

                binding.chipGroupTags.addView(chip);
            }

            updateSelectedTagsText();
        });
    }


    private void updateChipStyle(Chip chip, boolean isChecked) {
        if (isChecked) {
            chip.setChipBackgroundColorResource(R.color.purple_500); // hoặc màu đậm khác
            chip.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            chip.setChipBackgroundColorResource(R.color.chip_default_background); // màu nhạt
            chip.setTextColor(getResources().getColor(android.R.color.black));
        }
    }


    private void setupListeners() {
        binding.tvSelectedTags.setOnClickListener(v -> {
            if (binding.chipGroupTags.getVisibility() == View.GONE) {
                binding.chipGroupTags.setVisibility(View.VISIBLE);
            } else {
                binding.chipGroupTags.setVisibility(View.GONE);
            }
        });

        binding.btnApplyFilter.setOnClickListener(v -> {
            String sortField = "UpdatedAt";
            switch (binding.spinnerSort.getSelectedItemPosition()) {
                case 1: sortField = "Views"; break;
                case 2: sortField = "Rating"; break;
            }

            String statusField = "all";
            int checkedId = binding.radioGroupStatus.getCheckedRadioButtonId();
            if (checkedId == R.id.radioCompleted) statusField = "Hoàn thành";
            else if (checkedId == R.id.radioOngoing) statusField = "Đang tiến hành";

            filterPrefManager.saveFilters(selectedTagIds, sortField, statusField);
            listener.onFilterApplied(new ArrayList<>(selectedTagIds), sortField, statusField);
            dismiss();
        });

        binding.btnResetFilter.setOnClickListener(v -> {
            // Giá trị mặc định
            selectedTagIds.clear();
            String defaultSort = "UpdatedAt";
            String defaultStatus = "all";

            // Cập nhật UI
            binding.spinnerSort.setSelection(0); // "Ngày cập nhật"
            binding.radioGroupStatus.check(R.id.radioAll);

            for (int i = 0; i < binding.chipGroupTags.getChildCount(); i++) {
                View chipView = binding.chipGroupTags.getChildAt(i);
                if (chipView instanceof Chip) {
                    ((Chip) chipView).setChecked(false);
                }
            }

            binding.tvSelectedTags.setText("Chưa chọn");

            // (Tùy chọn) Toast hoặc thông báo
            // Toast.makeText(getContext(), "Đã đặt lại bộ lọc về mặc định", Toast.LENGTH_SHORT).show();
        });


        binding.btnCancelFilter.setOnClickListener(v -> dismiss());
    }

    private void updateSelectedTagsText() {
        List<String> tagNames = new ArrayList<>();
        for (int i = 0; i < binding.chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupTags.getChildAt(i);
            if (chip.isChecked()) {
                tagNames.add(chip.getText().toString());
            }
        }

        if (tagNames.isEmpty()) {
            binding.tvSelectedTags.setText("Chưa chọn");
        } else {
            binding.tvSelectedTags.setText(TextUtils.join(", ", tagNames));
        }
    }
}

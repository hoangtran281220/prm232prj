package com.example.prm232rj.ui.screen.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.prm232rj.R;
import com.example.prm232rj.ui.screen.Activities.ChangeProfileActivity;
import com.example.prm232rj.ui.screen.Activities.ConfirmEmailActivity;
import com.example.prm232rj.ui.screen.Activities.LoginActivity;
import com.example.prm232rj.ui.screen.Activities.NotificationActivity;
import com.example.prm232rj.ui.screen.Activities.UploadAvatarActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button uploadAvatar, confirmEmail, changeProfile, notification, btnLogout;
    private ImageView avatar;
    private TextView usernameText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences userPrefs;
        userPrefs = requireContext().getSharedPreferences("USER_PREF", getContext().MODE_PRIVATE);
        String email = userPrefs.getString("Email", null);
        String uid = userPrefs.getString("uid",null);
        // Nếu chưa đăng nhập -> Hiển thị fragment_login_prompt

        if (uid == null || uid.isEmpty()) {
            View view = inflater.inflate(R.layout.fragment_login_prompt, container, false);
            Button btnLogin = view.findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
            });
            return view;
        }

        // Nếu đã đăng nhập -> Inflate fragment_account như hiện tại
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        uploadAvatar = view.findViewById(R.id.uploadAvatar);
        confirmEmail = view.findViewById(R.id.confirmEmail);
        changeProfile = view.findViewById(R.id.changeProfile);
        notification = view.findViewById(R.id.notification);
        btnLogout = view.findViewById(R.id.btnLogout);
        avatar = view.findViewById(R.id.avatar);
        usernameText = view.findViewById(R.id.Username);

        String avatarUrl = userPrefs.getString("avatarUrl", null);
        usernameText.setText(email);

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(avatarUrl))
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(avatar);
        } else {
            avatar.setImageResource(R.drawable.logo);
        }

        uploadAvatar.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), UploadAvatarActivity.class)));

        confirmEmail.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), ConfirmEmailActivity.class)));

        changeProfile.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), ChangeProfileActivity.class)));

        notification.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), NotificationActivity.class)));

        btnLogout.setOnClickListener(v -> {
            userPrefs.edit().clear().apply();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

}
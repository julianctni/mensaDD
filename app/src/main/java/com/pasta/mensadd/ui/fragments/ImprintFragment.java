package com.pasta.mensadd.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.R;
import com.pasta.mensadd.ui.MainActivity;

import org.jetbrains.annotations.NotNull;

public class ImprintFragment extends Fragment implements View.OnClickListener {

    private final static String SUPPORT_MAIL_ADDRESS = "julianctni@gmail.com";
    private final static String SUPPORT_MAIL_SUBJECT = "Feedback mensaDD";
    private final static String URI_SCHEME_MAILTO = "mailto";
    private int mEasterCount = 0;
    private PreferenceService mPreferenceService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imprint, container, false);
        setHasOptionsMenu(true);
        TextView baconView = view.findViewById(R.id.imprintLicenseBacon);
        mPreferenceService = new PreferenceService(requireContext());
        if (mPreferenceService.isBaconFeatureEnabled())
            baconView.setVisibility(View.VISIBLE);
        TextView licenseView = view.findViewById(R.id.imprintLicense);

        licenseView.setMovementMethod(LinkMovementMethod.getInstance());
        licenseView.setText(Html
                .fromHtml(getString(R.string.imprint_license)));
        ImageView banner = view.findViewById(R.id.banner_imprint);
        banner.setOnClickListener(this);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
            activity.updateToolbar(-1, getString(R.string.pref_imprint));
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_imprint_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.feedback) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    URI_SCHEME_MAILTO, SUPPORT_MAIL_ADDRESS, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, SUPPORT_MAIL_SUBJECT);
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback_mail)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.banner_imprint && !mPreferenceService.isBaconFeatureEnabled()) {
            mEasterCount += 1;
            if (mEasterCount == 7) {
                Toast.makeText(getContext(), getString(R.string.toast_bacon), Toast.LENGTH_LONG).show();
                mPreferenceService.setBaconFeatureEnabled(true);
            }
        }
    }
}

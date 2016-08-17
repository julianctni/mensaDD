package com.pasta.mensadd.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasta.mensadd.R;

public class Imprintfragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_imprint, container, false);
        TextView licenseView = (TextView) v.findViewById(R.id.imprintLicense);

        licenseView.setMovementMethod(LinkMovementMethod.getInstance());
        licenseView.setText(Html
                .fromHtml(getString(R.string.imprint_license)));
        return v;
    }

}

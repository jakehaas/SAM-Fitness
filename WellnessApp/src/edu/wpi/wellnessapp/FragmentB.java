package edu.wpi.wellnessapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentB extends Fragment {

    private Button closeButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
	View view = inflater.inflate(R.layout.fragment_b, container, false);
	
	this.closeButton = (Button) view.findViewById(R.id.button1);
	this.closeButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		new Achievement(v, 0);
	    }
	});

	return view;
    }

}

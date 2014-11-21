package edu.wpi.wellnessapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class FragmentA extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_a, container, false);
	populateGraphView(view);

	return view;
    }

    private void populateGraphView(View view) {
	GraphViewSeries exampleSeries = new GraphViewSeries(
			new GraphViewData[] { new GraphViewData(1, 2.0d),
					      new GraphViewData(2, 1.5d), new GraphViewData(3, 2.5d),
					      new GraphViewData(4, 1.0d)});

	LineGraphView graphView = new LineGraphView(getActivity(), "Steps Taken\n");
	
	graphView.addSeries(exampleSeries);
	graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
	graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
	graphView.setHorizontalLabels(new String[] { "9/10", "9/15", "9/20", "9/25" });
	graphView.setVerticalLabels(new String[] { "10,000", "5,000", "0" });
	graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
	graphView.getGraphViewStyle().setTextSize(20);

	try {
	    LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph1);
	    layout.addView(graphView);
	} catch (NullPointerException e) {
	    // something to handle the NPE.
	}
    }

}

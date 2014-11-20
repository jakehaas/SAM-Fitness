package edu.wpi.wellnessapp;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.AAConfigChooser;
import com.threed.jpct.util.MemoryHelper;

public class StepFragment extends Fragment {

	private static StepFragment master = null;

	private GLSurfaceView mGLView;
	private Renderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private RGBColor back = new RGBColor(37, 37, 37);

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private Object3D cube = null;

	@SuppressWarnings("unused")
	private int fps = 0;

	private Light sun = null;

	private long startTime;

	private long endTime;

	private Context myContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		myContext = getActivity().getApplicationContext();
		View rootView = inflater.inflate(R.layout.fragment_a, container, false);
		
		populateGraphView(rootView);
		initGL(rootView, savedInstanceState);

		return rootView;
	}

	private void initGL(View rootView, Bundle savedInstanceState) {
		
		if (master != null) {
			copy(master);
		}
		

		super.onCreate(savedInstanceState);
		startTime = System.currentTimeMillis();
		mGLView = new ClearGLSurfaceView(myContext);

		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16,
						EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});
		
		mGLView.setEGLContextClientVersion(2);
		mGLView.setEGLConfigChooser(new AAConfigChooser(mGLView));

		renderer = new Renderer(myContext);
		mGLView.setRenderer(renderer);

		endTime = System.currentTimeMillis();
		Log.e("My App", "used time to load model: " + (endTime - startTime)
				/ 1000 + " s (" + (endTime - startTime) + " ms)");

		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.mainAvatar);

		layout.addView(mGLView);
	}
	
	private void copy(Object src) {
		try {
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			// throw new RuntimeException(e);
		}
	}

	protected boolean isFullscreenOpaque() {
		return true;
	}
	
	private void populateGraphView(View view) {
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
                new GraphViewData(1, 2.0d)
                , new GraphViewData(2, 1.5d)
                , new GraphViewData(3, 2.5d)
                , new GraphViewData(4, 1.0d)
        });
 
        LineGraphView graphView = new LineGraphView(
                getActivity() // context
                , "Steps Taken\n" // heading
        );
        graphView.addSeries(exampleSeries); // data
        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
        graphView.setHorizontalLabels(new String[] {"9/10", "9/15", "9/20", "9/25"});
        graphView.setVerticalLabels(new String[] {"10,000", "5,000", "0"});
        graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
        graphView.getGraphViewStyle().setTextSize(20);
        
        try {
        	
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph1);
            
            layout.addView(graphView);
        } catch (NullPointerException e) {
            // something to handle the NPE.
        }
    }

	class Renderer implements GLSurfaceView.Renderer {
		private long fpsTime = System.currentTimeMillis();
		Resources res = myContext.getResources();
		Context ctx;

		public Renderer(Context context) {
			ctx = context;
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if (fb != null) {
				fb.dispose();
			}

			fb = new FrameBuffer(w, h);

			if (master == null) {

				world = new World();
				world.setAmbientLight(30, 30, 30);

				sun = new Light(world);
				sun.setIntensity(250, 250, 250);

				cube = Primitives.getCube(10);
				cube.calcTextureWrapSpherical();
				// cube.setTexture("texture");
				cube.strip();
				cube.build();
				
				Avatar a = Avatar.getInstance();

				world.addObject(cube);

				Camera cam = world.getCamera();
				cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
				cam.lookAt(cube.getTransformedCenter());

				SimpleVector sv = new SimpleVector();
				sv.set(cube.getTransformedCenter());
				sv.y -= 100;
				sv.z -= 100;
				sun.setPosition(sv);

				MemoryHelper.compact();

				if (master == null) {
					Logger.log("Saving master Activity!");
					master = StepFragment.this;
				}
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			
		}

		public void onDrawFrame(GL10 gl) {
			if (touchTurn != 0) {
				cube.rotateY(touchTurn);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				cube.rotateX(touchTurnUp);
				touchTurnUp = 0;
			}

			fb.clear(back);
			world.renderScene(fb);
			world.draw(fb);

			fb.display();

			if (System.currentTimeMillis() - fpsTime >= 1000) {
				fps = 0;
				fpsTime = System.currentTimeMillis();
			}
			fps++;
		}

	}

	public class ClearGLSurfaceView extends GLSurfaceView {

		public ClearGLSurfaceView(Context context) {
			super(context);
		}

		public ClearGLSurfaceView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean onTouchEvent(MotionEvent me) {

			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				xpos = me.getX();
				ypos = me.getY();
				return true;
			}

			if (me.getAction() == MotionEvent.ACTION_UP) {
				xpos = -1;
				ypos = -1;
				touchTurn = 0;
				touchTurnUp = 0;
				return true;
			}

			if (me.getAction() == MotionEvent.ACTION_MOVE) {
				float xd = me.getX() - xpos;
				float yd = me.getY() - ypos;

				xpos = me.getX();
				ypos = me.getY();

				touchTurn = xd / 100f;
				touchTurnUp = yd / 100f;
				return true;
			}

			try {
				Thread.sleep(15);
			} catch (Exception e) {
				// Doesn't matter here...
			}

			return super.onTouchEvent(me);
		}
		
	}
}
/**
 * MainFragment.java
 * Wellness-App-MQP
 *
 * @version 1.0.0
 *
 * @author Jake Haas
 * @author Evan Safford
 * @author Nate Ford
 * @author Haley Andrews
 *
 * Copyright (c) 2014, 2015. Wellness-App-MQP. All Rights Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package edu.wpi.wellnessapp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.threed.jpct.Animation;
import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Mesh;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.AAConfigChooser;
import com.threed.jpct.util.MemoryHelper;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesIO;
import raft.jpct.bones.SkinClip;

public class MainFragment extends Fragment {
    private static MainFragment master = null;

    private GLSurfaceView mGLView;
    private Renderer renderer = null;
    private FrameBuffer fb = null;
    private World world = null;

    private AnimatedGroup avatar;
    private int animation = 1;
    private float animateSeconds = 0.0f;

    private long frameTime = System.currentTimeMillis();
    private long aggregatedTime = 0;
    private float speed = 1f;

    private static final int GRANULARITY = 25;

    private RGBColor back = new RGBColor(37, 37, 37);

    private float touchTurn = 0;
    private float touchTurnUp = 0;

    private float xPos = -1;
    private float yPos = -1;

    private Light pointLight = null;

    private int avatarStatus = 0;
    private boolean animationDone = false;
    private int animationLoopCount = 0;

    private boolean canShowSettingPopup = true;
    PopupWindow popupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button settingButton = (Button) rootView.findViewById(R.id.settings_button);
        settingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canShowSettingPopup) {
                    showSettingsPopup(v);
                    canShowSettingPopup = false;
                } else {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                        popupWindow = null;
                        canShowSettingPopup = true;
                    }
                }
            }
        });

        initGL(rootView, savedInstanceState);

        frameTime = System.currentTimeMillis();
        aggregatedTime = 0;

        return rootView;
    }


    public void showSettingsPopup(View anchorView) {
        LayoutInflater mInflater;
        Context context = anchorView.getContext().getApplicationContext();
        mInflater = LayoutInflater.from(context);

        final View popupView = mInflater.inflate(R.layout.settings_popup, null);
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);


        final Button setMoodConfirmButton = (Button) popupView.findViewById(R.id.set_mood_button);
        setMoodConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMoodConfirmButton.setText("Close!");
                setMoodConfirmButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        popupWindow = null;
                        canShowSettingPopup = true;
                    }
                });
            }
        });

    }
    
    
    /*
     * 
     * NEED TO IMPLEMENT PAUSE/RESUME
     * THIS OVERRIDES WILL BE NEEDED LATER
     * THIS IS TO HOPEFULLY FIX THE GL VIEW BEING
     * RE-CREATED SOMETIMES
     * 
     */
    
    /*
    @Override
    public void onPause() {
		Logger.log("onPause");
		super.onPause();
//		mGLView.onPause();

	}

	@Override
	public void onResume() {
		Logger.log("onResume");
		super.onResume();
//		mGLView.onResume();
		
		frameTime = System.currentTimeMillis();
		aggregatedTime = 0;

	}

	@Override
	public void onStop() {
		Logger.log("onStop");
		super.onStop();
	}
	
	*/

    private void loadGLResources() {
        Resources res = getResources();
        TextureManager.getInstance().flush();

        Texture texture = new Texture(res.openRawResource(R.raw.ninja_texture));
        texture.keepPixelData(true);
        TextureManager.getInstance().addTexture("ninja", texture);

        try {
            avatar = BonesIO.loadGroup(res.openRawResource(R.raw.animation_test));

            // After we load the resources, generate the animations
            createMeshKeyFrames();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (Animated3D a : avatar) {
            a.setTexture("ninja");
        }

    }

    private void createMeshKeyFrames() {
        Config.maxAnimationSubSequences = avatar.getSkinClipSequence()
                .getSize() + 1; // +1 for whole sequence

        int keyframeCount = 0;
        final float deltaTime = 0.2f; // max time between frames

        for (SkinClip clip : avatar.getSkinClipSequence()) {
            float clipTime = clip.getTime();
            int frames = (int) Math.ceil(clipTime / deltaTime) + 1;
            keyframeCount += frames;
        }

        Animation[] animations = new Animation[avatar.getSize()];
        for (int i = 0; i < avatar.getSize(); i++) {
            animations[i] = new Animation(keyframeCount);
            animations[i].setClampingMode(Animation.USE_CLAMPING);
        }

        int sequence = 0;
        for (SkinClip clip : avatar.getSkinClipSequence()) {
            float clipTime = clip.getTime();
            int frames = (int) Math.ceil(clipTime / deltaTime) + 1;
            float dIndex = 1f / (frames - 1);

            for (int i = 0; i < avatar.getSize(); i++) {
                animations[i].createSubSequence(clip.getName());
            }

            for (int i = 0; i < frames; i++) {
                avatar.animateSkin(dIndex * i, sequence + 1);

                for (int j = 0; j < avatar.getSize(); j++) {
                    Mesh keyframe = avatar.get(j).getMesh().cloneMesh(true);
                    keyframe.strip();
                    animations[j].addKeyFrame(keyframe);
                }
            }
            sequence++;
        }

        for (int i = 0; i < avatar.getSize(); i++) {
            avatar.get(i).setAnimationSequence(animations[i]);
        }

        avatar.get(0).getSkeletonPose().setToBindPose();
        avatar.get(0).getSkeletonPose().updateTransforms();
        avatar.applySkeletonPose();
        avatar.applyAnimation();

        Logger.log("created mesh keyframes, " + keyframeCount + "x"
                + avatar.getSize());
    }

    private void initGL(View rootView, Bundle savedInstanceState) {

        if (master != null) {
            copy(master);
        }

        super.onCreate(savedInstanceState);
        mGLView = new ClearGLSurfaceView(getActivity().getApplicationContext());

        mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_NONE};
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });

        mGLView.setEGLContextClientVersion(2);
        mGLView.setEGLConfigChooser(new AAConfigChooser(mGLView));

        renderer = new Renderer(getActivity().getApplicationContext());
        mGLView.setRenderer(renderer);

        LinearLayout layout = (LinearLayout) rootView
                .findViewById(R.id.mainAvatar);

        layout.addView(mGLView);

        loadGLResources();
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
            throw new RuntimeException(e);
        }
    }

    class Renderer implements GLSurfaceView.Renderer {

        public Renderer(Context context) {
        }

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            if (fb != null) {
                fb.dispose();
            }

            fb = new FrameBuffer(w, h);

            if (master == null) {

                world = new World();
                world.setAmbientLight(30, 30, 30);

                pointLight = new Light(world);
                pointLight.setIntensity(250, 250, 250);

                avatar.getRoot().rotateY(180);
                avatar.getRoot().translate(300, 0, 0);

                avatar.addToWorld(world);

                SimpleVector cameraLookAtVector = new SimpleVector();
                cameraLookAtVector.x = 0;
                cameraLookAtVector.y = -100;
                cameraLookAtVector.z = 0;

                Camera cam = world.getCamera();
                cam.moveCamera(Camera.CAMERA_MOVEOUT, -400.0f);
                cam.moveCamera(Camera.CAMERA_MOVEUP, 200.0f);
                cam.rotateCameraY(90.0f);
                cam.lookAt(cameraLookAtVector);

                SimpleVector lightPos = new SimpleVector(0, 0, 0);
                lightPos.y -= 100;
                lightPos.z += 100;
                pointLight.setPosition(lightPos);

                MemoryHelper.compact();

                if (master == null) {
                    Logger.log("Saving master Activity!");
                    master = MainFragment.this;
                }
            }
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        public void onDrawFrame(GL10 gl) {

            // If we are not playing the intro animation, allow
            // the user to rotate the avatar
            if (animation > 1) {
                if (touchTurn != 0) {
                    avatar.getRoot().rotateY(touchTurn);
                    touchTurn = 0;
                }

                if (touchTurnUp != 0) {
                    avatar.getRoot().rotateX(touchTurnUp);
                    touchTurnUp = 0;
                }
            }

            animateAvatar();

            fb.clear(back);
            world.renderScene(fb);
            world.draw(fb);

            fb.display();
        }

        private void animateAvatar()
        {
            switch (avatarStatus)
            {
                case 0:
                    if (Utils.getTotalScore() > 60) {
                        if (animationDone) {
                            avatarStatus = 1;
                            animationDone = false;
                        }
                        else {
                            playAvatarAnimationLooped(3, 5);
                            avatar.getRoot().translate(-10, 0, 0);
                        }

                    }
                    break;

                case 1:
                    if (Utils.getTotalScore() > 60) {
                        if (animationDone) {
                            avatarStatus = 2;
                            animationDone = false;
                        } else {
                            playAvatarAnimationSingle(1);
                        }
                    }
                    break;

                case 2:
                    if (Utils.getTotalScore() > 60) {
                        if (animationDone) {
                            avatarStatus = 3;
                            animationDone = false;
                        } else {
                            playAvatarAnimationSingle(2);
                        }
                    }
                    break;

                case 3:
                    if (Utils.getTotalScore() > 60) {
                        if (animationDone) {
                            avatarStatus = 3;
                            animationDone = false;
                        } else {
                            playAvatarAnimationSingle(4);
                        }
                    }
                    break;
            }

        }

        private void playAvatarAnimationSingle (int animationIndex)
        {
            doAvatarAnimation(animationIndex, false, 1);
        }

        private void playAvatarAnimationLooped (int animationIndex, int loopCount)
        {
            doAvatarAnimation(animationIndex, true, loopCount);
        }

        private void playAvatarAnimationRepeatForever (int animationIndex)
        {
            doAvatarAnimation(animationIndex, true, -1);
        }

        private void doAvatarAnimation(int animationIndex, boolean loop, int loopCount) {
            if (!animationDone) {
                animation = animationIndex;

                long now = System.currentTimeMillis();
                aggregatedTime += (now - frameTime);
                frameTime = now;

                if (aggregatedTime > 1000) {
                    aggregatedTime = 0;
                }

                while (aggregatedTime > GRANULARITY) {
                    aggregatedTime -= GRANULARITY;
                    animateSeconds += GRANULARITY * 0.001f * speed;
                }

                if (animation > 0
                        && avatar.getSkinClipSequence().getSize() >= animation) {
                    float clipTime = avatar.getSkinClipSequence()
                            .getClip(animation - 1).getTime();

                    if (animateSeconds > clipTime) {
                        animateSeconds = 0;

                        if (!loop) {
                            animationDone = true;
                        }
                        else
                        {
                            if (loopCount != -1) {
                                if (animationLoopCount >= loopCount) {
                                    animationDone = true;
                                } else {
                                    animationLoopCount++;
                                }
                            }
                        }
                    }

                    float index = animateSeconds / clipTime;

                    for (Animated3D a : avatar) {
                        a.animateSkin(index, animation);

                        if (!a.isAutoApplyAnimation()) {
                            a.applyAnimation();
                        }
                    }

                } else {
                    animateSeconds = 0.0f;
                }


            }
        }

    }

    public class ClearGLSurfaceView extends GLSurfaceView {

        public ClearGLSurfaceView(Context context) {
            super(context);
        }

        public ClearGLSurfaceView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                xPos = motionEvent.getX();
                yPos = motionEvent.getY();

                return true;
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                xPos = -1;
                yPos = -1;
                touchTurn = 0;
                touchTurnUp = 0;

                return true;
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                float xDelta = motionEvent.getX() - xPos;
                float yDelta = motionEvent.getY() - yPos;

                xPos = motionEvent.getX();
                yPos = motionEvent.getY();

                touchTurn = xDelta / 100f;
                touchTurnUp = yDelta / 100f;

                return true;
            }

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    performClick();
                    break;
                default:
                    break;
            }

            try {
                Thread.sleep(15);
            } catch (Exception e) {
                // Doesn't matter here...
            }

            return super.onTouchEvent(motionEvent);
        }

    }
}
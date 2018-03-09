package example.andy.jsmb;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import example.andy.jsmb.render.BackgroundRenderer;
import example.andy.jsmb.render.TriangleRenderer;
import example.andy.jsmb.render.TriangleTextureRenderer;

public class JSMBActivity extends Activity implements  GLSurfaceView.Renderer {

    private GLSurfaceView mSurfaceView;
    private final BackgroundRenderer mBackgroundRenderer = new BackgroundRenderer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (detectOpenGLES30() == false) {
            throw  new RuntimeException();
        };

        mSurfaceView = new GLSurfaceView(this);
        mSurfaceView.setEGLContextClientVersion(3);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        mSurfaceView.setRenderer(this);
        setContentView(mSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mBackgroundRenderer.createOnGlThread(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mBackgroundRenderer.draw();
    }

    private boolean detectOpenGLES30()
    {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x30000;
    }
}

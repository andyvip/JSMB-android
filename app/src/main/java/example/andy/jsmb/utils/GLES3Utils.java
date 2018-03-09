package example.andy.jsmb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.opengl.GLES30.*;

/**
 * Created by andy on 08/03/2018.
 */

public class GLES3Utils {
    private static String readShardeFromAsset(Context context, String fileName) {
        if (fileName == null) {
            Log.e("GLES3Utils", "readShardeFromAsset fail: fileName is null ");
            return null;
        }

        String shaderSource = null;
        InputStream is = null;
        byte[] buffer;

        try {
            is = context.getAssets().open(fileName);
            buffer = new byte[is.available()];
            is.read(buffer);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(buffer);

            os.close();
            is.close();

            shaderSource = os.toString();
        } catch (IOException ioe) {
            is = null;
            Log.e("GLES3Utils", "readShardeFromAsset fail:" + ioe);
        }

        return shaderSource;
    }


    public static int loadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        shader = glCreateShader(type);
        if (shader == 0) {
            Log.e("GLES3Utils", "glCreateShader fail:" + glGetShaderInfoLog(shader));
            return 0;
        }

        glShaderSource(shader, shaderSrc);
        glCompileShader(shader);
        glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("GLES3Utils", "glCompileShader fail:" + glGetShaderInfoLog(shader));
            glDeleteShader(shader);
            return 0;
        }

        return shader;
    }


    public static int loadProgram(String vertShaderSrc, String fragShaderSrc) {
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        vertexShader = loadShader(GL_VERTEX_SHADER, vertShaderSrc);
        if (vertexShader == 0) {
            return 0;
        }

        fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragShaderSrc);
        if (fragmentShader == 0) {
            glDeleteShader(vertexShader);
            return 0;
        }

        programObject = glCreateProgram();
        if (programObject == 0) {
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            return 0;
        }

        glAttachShader(programObject, vertexShader);
        glAttachShader(programObject, fragmentShader);
        glLinkProgram(programObject);
        glGetProgramiv(programObject, GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            Log.e("GLES3Utils", "Error linking program:" + glGetProgramInfoLog(programObject));
            glDeleteProgram(programObject);
            return 0;
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return programObject;
    }


    public static int loadProgramFromAsset(Context context, String vertexShaderFileName, String fragShaderFileName) {
        String vertShaderSrc = readShardeFromAsset(context, vertexShaderFileName);
        if (vertShaderSrc == null) {
            return 0;
        }

        String fragShaderSrc = readShardeFromAsset(context, fragShaderFileName);
        if (fragShaderSrc == null) {
            return 0;
        }

        return loadProgram(vertShaderSrc, fragShaderSrc);
    }

    public static int loadTextureFromAsset(Context context, String fileName)
    {
        Bitmap bitmap = null;
        InputStream is = null;
        int[] textureId = new int[1];

        try {
            is = context.getAssets().open(fileName);
        } catch (IOException ioe) {
            is = null;
            Log.e("GLES3Utils", "loadTextureFromAsset fail:" + ioe);
        }

        if (is == null) {
            return 0;
        }

        bitmap = BitmapFactory.decodeStream(is);

        glGenTextures(1, textureId, 0);
        glBindTexture(GL_TEXTURE_2D, textureId[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        GLUtils.texImage2D( GL_TEXTURE_2D, 0, bitmap, 0 );

//        GLES20.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR );
//        GLES20.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR );
//        GLES20.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE );
//        GLES20.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE );


        return textureId[0];
    }

    public static void checkGLError() {
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            Log.e("GLES3Utils", "gl error: " + error);
            throw new RuntimeException();
        }
    }
}

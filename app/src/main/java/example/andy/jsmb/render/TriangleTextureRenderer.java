package example.andy.jsmb.render;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import example.andy.jsmb.utils.GLES3Utils;

import static android.opengl.GLES30.*;


/**
 * This class renders the AR background from camera feed. It creates and hosts the texture
 * given to ARCore to be filled with the camera image.
 */
public class TriangleTextureRenderer {

    private int mTextureId;
    private int mProgramObject;
    private FloatBuffer mVertices;
    private FloatBuffer mTexCoords;

    private final float[] mVerticesData = {
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    };

    private final float[] mTexCoordsData = {
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
    };


    public TriangleTextureRenderer() {
        mVertices = ByteBuffer.allocateDirect ( mVerticesData.length * 4 )
            .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mVertices.put ( mVerticesData ).position ( 0 );

        mTexCoords = ByteBuffer.allocateDirect ( mTexCoordsData.length * 4 )
            .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mTexCoords.put ( mTexCoordsData ).position ( 0 );
    }


    public void createOnGlThread(Context context) {
        glClearColor ( 1.0f, 1.0f, 1.0f, 0.0f );
        mTextureId = GLES3Utils.loadTextureFromAsset(context, "textures/bg.jpg");
        mProgramObject = GLES3Utils.loadProgramFromAsset(context, "shaders/simple_texture_vertex.shader", "shaders/simple_texture_fragment.shader");
    }


    public void draw() {
        glClear ( GL_COLOR_BUFFER_BIT );
        glUseProgram ( mProgramObject );
        glBindTexture(GL_TEXTURE_2D, mTextureId);
        glVertexAttribPointer ( 0, 3, GL_FLOAT, false, 0, mVertices );
        glVertexAttribPointer ( 1, 2, GL_FLOAT, false, 0, mTexCoords );

        glEnableVertexAttribArray ( 0 );
        glEnableVertexAttribArray ( 1 );

        glDrawArrays ( GL_TRIANGLES, 0, 3);
    }
}

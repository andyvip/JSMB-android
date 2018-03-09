package example.andy.jsmb.render;

import android.content.Context;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import example.andy.jsmb.utils.GLES3Utils;

import static android.opengl.GLES30.*;


/**
 * This class renders the AR background from camera feed. It creates and hosts the texture
 * given to ARCore to be filled with the camera image.
 */
public class BackgroundRenderer {
    private FloatBuffer mVertices;
    private FloatBuffer mTexCoords;
    private ShortBuffer mIndices;

    private int mTextureId;
    private int mProgramObject;
    private int mSamplerLoc;



    private final float[] mVerticesData = {
        -1.0f, 1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
    };

    private final float[] mTexCoordsData = {
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
    };

    private final short[] mIndicesData = {
            0, 1, 2, 0, 2, 3
    };


    public BackgroundRenderer() {
        mVertices = ByteBuffer.allocateDirect ( mVerticesData.length * 4 )
            .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mVertices.put ( mVerticesData ).position ( 0 );

        mTexCoords = ByteBuffer.allocateDirect ( mTexCoordsData.length * 4 )
            .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mTexCoords.put ( mTexCoordsData ).position ( 0 );

        mIndices = ByteBuffer.allocateDirect ( mIndicesData.length * 2 )
            .order ( ByteOrder.nativeOrder() ).asShortBuffer();
        mIndices.put ( mIndicesData ).position ( 0 );
    }


    public void createOnGlThread(Context context) {
        glClearColor ( 1.0f, 1.0f, 1.0f, 0.0f );
        mTextureId = GLES3Utils.loadTextureFromAsset(context, "textures/bg.jpg");
        mProgramObject = GLES3Utils.loadProgramFromAsset(context, "shaders/simple_texture_vertex.shader", "shaders/simple_texture_fragment.shader");
        mSamplerLoc = GLES30.glGetUniformLocation(mProgramObject, "u_TextureUnit");

    }


    public void draw() {
        glClear ( GL_COLOR_BUFFER_BIT );
        glUseProgram ( mProgramObject );
        glVertexAttribPointer ( 0, 3, GL_FLOAT, false, 0, mVertices );
        glVertexAttribPointer ( 1, 2, GL_FLOAT, false, 0, mTexCoords );
        glEnableVertexAttribArray ( 0 );
        glEnableVertexAttribArray ( 1 );
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureId);
        glUniform1i(mSamplerLoc, 0);
        glDrawElements ( GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, mIndices );
    }
}

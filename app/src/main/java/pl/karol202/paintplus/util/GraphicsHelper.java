package pl.karol202.paintplus.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.*;
import android.os.Build;
import android.support.v8.renderscript.RenderScript;

import javax.microedition.khronos.opengles.GL10;

public class GraphicsHelper
{
	private class GLException extends RuntimeException
	{
		GLException(String message)
		{
			super(message);
		}
	}
	
	private static RenderScript renderScript;
	private static int maxTextureSize;
	
	private EGLDisplay display;
	private EGLSurface surface;
	private EGLContext context;
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public GraphicsHelper()
	{
		try
		{
			initGL();
			initVariables();
		}
		finally
		{
			stopGL();
		}
	}
	
	public static void init(Context context)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) new GraphicsHelper();
		else initSubstitutesForVariables();
		
		renderScript = RenderScript.create(context);
	}
	
	public static void destroy()
	{
		renderScript.destroy();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void initGL() throws GLException
	{
		display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
		int[] version = new int[2];
		EGL14.eglInitialize(display, version, 0, version, 1);
		int[] configAttr = { EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
							 EGL14.EGL_LEVEL, 0,
							 EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
							 EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
							 EGL14.EGL_NONE };
		EGLConfig[] configs = new EGLConfig[1];
		int[] numConfig = new int[1];
		EGL14.eglChooseConfig(display, configAttr, 0,
							  configs, 0, 1, numConfig, 0);
		if (numConfig[0] == 0) throw new GLException("Not found any configurations.");
		EGLConfig config = configs[0];
		int[] surfAttr = { EGL14.EGL_WIDTH, 64,
						   EGL14.EGL_HEIGHT, 64,
						   EGL14.EGL_NONE };
		surface = EGL14.eglCreatePbufferSurface(display, config, surfAttr, 0);
		int[] ctxAttrib = { EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
							EGL14.EGL_NONE
		};
		context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);
		EGL14.eglMakeCurrent(display, surface, surface, context);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void initVariables()
	{
		int[] array = new int[1];
		GLES20.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, array, 0);
		maxTextureSize = array[0];
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void stopGL()
	{
		EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
				EGL14.EGL_NO_CONTEXT);
		EGL14.eglDestroySurface(display, surface);
		EGL14.eglDestroyContext(display, context);
		EGL14.eglTerminate(display);
	}
	
	private static void initSubstitutesForVariables()
	{
		maxTextureSize = 2048;
	}
	
	public static RenderScript getRenderScript()
	{
		return renderScript;
	}
	
	public static int getMaxTextureSize()
	{
		return maxTextureSize;
	}
}
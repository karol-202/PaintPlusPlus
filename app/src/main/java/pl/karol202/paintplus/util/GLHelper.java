package pl.karol202.paintplus.util;

import android.opengl.*;

import javax.microedition.khronos.opengles.GL10;

public class GLHelper
{
	private class GLException extends Exception
	{
		public GLException(String message)
		{
			super(message);
		}
	}
	
	private EGLDisplay display;
	private EGLSurface surface;
	private EGLContext context;
	
	private int maxTextureSize;
	
	public GLHelper()
	{
		try
		{
			initGL();
			initVariables();
		}
		catch(GLException ex)
		{
			throw new RuntimeException("GLException: " + ex.getMessage());
		}
		finally
		{
			stopGL();
		}
	}
	
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
	
	private void initVariables()
	{
		int[] array = new int[1];
		GLES20.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, array, 0);
		maxTextureSize = array[0];
	}
	
	private void stopGL()
	{
		EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
				EGL14.EGL_NO_CONTEXT);
		EGL14.eglDestroySurface(display, surface);
		EGL14.eglDestroyContext(display, context);
		EGL14.eglTerminate(display);
	}
	
	public int getMaxTextureSize()
	{
		return maxTextureSize;
	}
}
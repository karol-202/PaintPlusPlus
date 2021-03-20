/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package pl.karol202.paintplus.util

import android.opengl.*
import javax.microedition.khronos.opengles.GL10

object GraphicsHelper
{
	private class GLException(message: String) : RuntimeException(message)

	@JvmStatic
	var maxTextureSize = 0
		private set

	private var display: EGLDisplay? = null
	private var surface: EGLSurface? = null
	private var context: EGLContext? = null

	init
	{
		try
		{
			initGL()
			initVariables()
		}
		finally
		{
			stopGL()
		}
	}

	private fun initGL()
	{
		display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
		val version = IntArray(2)
		EGL14.eglInitialize(display, version, 0, version, 1)
		val configAttr = intArrayOf(EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER, EGL14.EGL_LEVEL, 0, EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT, EGL14.EGL_NONE)
		val configs = arrayOfNulls<EGLConfig>(1)
		val numConfig = IntArray(1)
		EGL14.eglChooseConfig(display, configAttr, 0, configs, 0, 1, numConfig, 0)
		if(numConfig[0] == 0) throw GLException("Not found any configurations.")
		val config = configs[0]
		val surfAttr = intArrayOf(EGL14.EGL_WIDTH, 64, EGL14.EGL_HEIGHT, 64, EGL14.EGL_NONE)
		surface = EGL14.eglCreatePbufferSurface(display, config, surfAttr, 0)
		val ctxAttrib = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
		context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0)
		EGL14.eglMakeCurrent(display, surface, surface, context)
	}

	private fun initVariables()
	{
		val array = IntArray(1)
		GLES20.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, array, 0)
		maxTextureSize = array[0]
	}

	private fun stopGL()
	{
		EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
		EGL14.eglDestroySurface(display, surface)
		EGL14.eglDestroyContext(display, context)
		EGL14.eglTerminate(display)
	}
}

package com.arecmetafora.jsdroid;

/**
 * Interface implemented by JavaScript classes which want to be notified
 * when the object is being collected by the JavaScript garbage collector.
 */
public interface JavaScriptDisposable {

	/**
	 * Dispose all resources related with the JavaScript reference.
	 */
	void dispose();
}

/*
 * Expose debugger feature to be used outside WebKit
 */

#ifndef JSDebugger_h
#define JSDebugger_h

#include <JavaScriptCore/JavaScript.h>
#include <stdint.h>

class JSDebuggerCallback {

public:
    virtual ~JSDebuggerCallback() {}

    virtual void sourceParsed(JSContextRef ctx, const char *sourceUrl, size_t sourceID) = 0;
    virtual void handleBreakpointHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column) = 0;
    virtual void handleExceptionHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column, JSValueRef *exception) = 0;
    virtual void handleStepHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column) = 0;
};

#ifdef __cplusplus
extern "C" {
#endif

/*!
@function
@abstract Attaches a context to debugger.
@param ctx The JavaScript context.
@param callback The JavaScript debugger callback.
*/
JS_EXPORT void JSDebuggerAttach(JSContextRef ctx, JSDebuggerCallback* callback);

/*!
@function
@abstract Detaches a context from debugger.
@param ctx The JavaScript context.
*/
JS_EXPORT void JSDebuggerDetach(JSContextRef ctx);

/*!
@function
@abstract Checks if a context is attached to debugger.
@param ctx The JavaScript context.
@returns True if the context is already attached to the debugger, or false otherwise.
*/
JS_EXPORT bool JSDebuggerIsAttached(JSContextRef ctx);

/*!
@function
@abstract Adds a breakpoint to the debugger.
@param ctx The JavaScript context.
@param sourceID The source identifier.
@param line The breakpoint line.
@param column The breakpoint column.
@param condition The breakpoint condition.
@param hitCount Number of times the breakpoint should be hit before breaking execution.
@returns The breakpoint identifier.
*/
JS_EXPORT size_t JSDebuggerSetBreakpoint(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column,
                                         const char *condition, unsigned hitCount);

/*!
@function
@abstract Removes a breakpoint from the debugger.
@param ctx The JavaScript context.
@param breakpointID The identifier of the breakpoint to be removed.
*/
JS_EXPORT void JSDebuggerRemoveBreakpoint(JSContextRef ctx, size_t breakpointID);

/*!
@function
@abstract Clear all breakpoints.
@param ctx The JavaScript context.
*/
JS_EXPORT void JSDebuggerClearBreakpoints(JSContextRef ctx);

/*!
@function
@abstract Breaks the program execution.
@param ctx The JavaScript context.
*/
JS_EXPORT void JSDebuggerBreakProgram(JSContextRef ctx);

/*!
@function
@abstract Continue the program execution.
@param ctx The JavaScript context.
*/
JS_EXPORT void JSDebuggerContinueProgram(JSContextRef ctx);

/*!
@function
@abstract Proceed with the program execution, breaking into the inner statement (scope).
@param ctx The JavaScript context.
*/
JS_EXPORT void JSDebuggerStepIntoStatement(JSContextRef ctx);

/*!
@function
@abstract Proceed with the program execution, breaking into the next statement.
@param ctx The JavaScript context.
*/
JS_EXPORT void JSDebuggerStepOverStatement(JSContextRef ctx);

/*!
@function
@abstract Proceed with the program execution, exiting from the current statement (scope).
@param ctx The JavaScript context.
*/
JS_EXPORT void JSDebuggerStepOutOfFunction(JSContextRef ctx);

/*!
@function
@abstract Evaluates a JavaScript expression using the current debugger context.
@param ctx The JavaScript context.
@param script The JavaScript expression to be evaluted.
@param exception The pointer to the JavaScript exception, if any exception occurred during evaluation.
@returns The value of the JavaScript expression.
*/
JS_EXPORT JSValueRef JSDebuggerEvaluateScript(JSContextRef ctx, JSStringRef script, JSValueRef *exception);

/*!
@function
@abstract Gets the property names of the current debugger scope.
@param ctx The JavaScript context.
@returns The scope variables.
*/
JS_EXPORT JSPropertyNameArrayRef JSDebuggerGetPropertyNamesOfCurrentScope(JSContextRef ctx);

/*!
@function
@abstract Starts the profiler.
@param ctx The JavaScript context.
*/
JS_EXPORT void JSDebuggerStartProfiler(JSContextRef ctx);

/*!
@function
@abstract Stops the profiler
@param ctx The JavaScript context.
@returns The profiler result in a JSON format.
*/
JS_EXPORT const char* JSDebuggerStopProfiler(JSContextRef ctx);

#ifdef __cplusplus
}
#endif

#endif // JSDebugger_h

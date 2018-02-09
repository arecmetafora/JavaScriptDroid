
# JavaScriptDroid (jsDroid)

JavaScriptDroid (jsDroid) is a library used to interpret JavaScript inside your Android application.

It uses the JavaScriptCore engine, the same used by [WebKit](https://webkit.org/) based browsers.

Add this line to your build.gradle file to use it.

```javascript
compile 'com.arecmetafora:jsDroid:1.0.0'
```

# Usage

The `JavaScriptDroid` class contains almost all functionalities you will probably use.

The first thing you might want do is to register your Java class inside the JavaScript context, to provide extra information and receive callbacks when some JavaScript events occur. You can register a class by calling the method `registerClass`, as shown below:

```javascript
JavaScriptDroid.registerClass(MyClass.class);
```

Your class may probably have properties and methods. Each attribute and method of your class marked with `@JavaScriptMapped` annotation will also be available inside the JavaScript context.

```java
public class MyClass {
   
    @JavaScriptMapped
    String propertyP;

    @JavaScriptMapped
    String methodM(int param1, String param2) {
        return "Hello, JavaScriptDroid!";
    }
}
```

After defining and registering your class, you can evaluate a JavaScript, calling the method `evaluateScript`

```javascript
JavaScriptDroid.registerClass(MyClass.class);
JavaScriptDroid.registerClass(MyClass2.class);

String myJSLib = "..."; // Load it from a file perhaps
JavaScriptDroid.evaluateScript(myJSLib, "MyLib.js");

MyClass2 result = (MyClass2)JavaScriptDroid.evaluateScript("myLibFunc(new MyClass());");
```

## Renaming fields

If you want that your class, property or method be mapped with a different name than the Java class names, you can use the annotation parameter, specifying a different name, as below:

```java
@JavaScriptMapped(name = "AwesomeClass")
public class MyClass {

    @JavaScriptMapped(name = "coolProperty")
    String propertyP;

    @JavaScriptMapped(name = "unbelievableMethod")
    String methodM(int param1, String param2) {
        return "Hello, JavaScriptDroid!";
    }
}
```

## Type and nullability check

The API ensures that null values are not assigned to properties and method parameters of primitives types. Any attempt to do that will result in an exception being thrown (either `JavaScriptPropertyRequired` or `JavaScriptMethodParamRequired`).

If you want any other non-primitive field be non-nullable, you can mark it with the annotation `@NonNull`:

```java
public class MyClass {
    @NonNull
    @JavaScriptMapped
    String propertyP;
   
    @JavaScriptMapped
    String methodM(int param1, @NonNull String param2) {
        return "Hello, JavaScriptDroid!";
    }
}
```

To ensure proper type validation, the API also validates the JavaScript types against the mapped field types. If any violation is made an exception will be thrown (either `JavaScriptPropertyTypeInvalid` or `JavaScriptMethodParamTypeInvalid`).

Use the table below to check how native Java object are translated to JavaScript instances and vice-versa.

|Java|JavaScript|
|--|--|
| Primitive numbers (`byte`, `short`, `int`, `long`, `float` and `double`) | `number` |
| `boolean` | `boolean` |
| `String` | `string` |
| `GregorianCalendar`| `Date` |
| `Object[]` | `object` (array) |

## Exception handling

JavaScript errors thrown during your script execution will be thrown  normally, in a form of a `JavaScriptException`. This exception has a very useful method, `getJavaScriptStackTrace()` which returns the call stack at the moment of the exception. Use it to debug your code and find issues.

```java
try {
    JavaScriptDroid.evaluateScript("myLibFunc();");
} catch(JavaScriptException ex) {
    android.util.Log.e(LOG_TAG, ex.getMessage + "\n\n" + ex.getJavaScriptStackTrace());
}
```

I suggest to you to make your internal exceptions extend the `JavaScriptException` class, so you can have a better control of what is happening during your application life cycle.

```java
public MyBusinessError extends JavaScriptException {
    public MyBusinessError(String message) {
        // Do not forget to call the super constructor!
        super(message);
    }
}

public class MyClass {

    @JavaScriptMapped
    void methodM(int param1) throws MyBusinessError {
        if(param1 < 0) {
            throw new MyBusinessError("Param cannot be negative");
        }
    }
}

try {
    JavaScriptDroid.registerClass(MyClass.class);
    JavaScriptDroid.evaluateScript("new MyClass().methodM(-1);");
} catch(JavaScriptException ex) {
    //...
}
```

## Garbage collection

JavaScriptCore already executes a garbage collection from times to times. However, if you need to anticipate, you can force the garbage collection execution, by calling:

```javascript
JavaScriptDroid.garbageCollect();
```

If instances of your mapped object need to release resources when they are not used anymore, your class can implement the `JavaScriptDisposable` interface  so it can receive feedback when the object was collected by the JavaScript garbage collector.

```java
public class MyClass implements JavaScriptDisposable {
    ...

    @Override
    public void dispose() {
        // Release your resources here...
    }
}
```

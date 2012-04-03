## _Clojure Programming_, Chapter 9

### `gen-class`

This project contains examples related to using `gen-class` to generate
Java-style classes from within Clojure.


#### "Wrapping" a Clojure API with a Java-friendly class

The
[`com.clojurebook.imaging`](src/com/clojurebook/imaging.clj)
namespace defines a Clojure API for loading and resizing images.  It then uses `gen-class` to produce a Java class (`ResizeImage` in the default package) whose static utility methods delegate to the API's `resize-image` function.

You can see this in action by first building an uberjar containing all
of the code in the project, and its dependencies:

```
$ lein uberjar
```

Then you can choose to run either `ResizeImage` directly:

```
$ java -cp target/gen-class-1.0.0-standalone.jar ResizeImage clojure.png small.png 0.3
```

…or you can run a [Java shim class](src/ResizeClient.java) that calls
into `ResizeImage` to demonstrate that Java->Clojure interop:

```
$ java -cp target/gen-class-1.0.0-standalone.jar ResizeClient clojure.png java-small.png 0.3
```

#### A custom `Exception` type

The
[`com.clojurebook.CustomException`](src/com/clojurebook/CustomException.clj)
namespace defines a custom `Exception` subclass that has a couple of interesting properties:

* a `java.util.Map` value can be provided when constructing an instance
  of `CustomException`
* `CustomException` implements Clojure's `IDeref` interface, so
  instances can be dereferenced (i.e. `@e`) to easily obtain the
aforementioned `java.util.Map`.

The [`BatchJob`](src/BatchJob.java) class shows an example of using this
custom `Exception` type from Java.  Assuming you've built an uberjar
(`lein uberjar`), you can run `BatchJob`'s `main` method, which echos
the contents of the exception's info map:

```
$ java -cp target/gen-class-1.0.0-standalone.jar BatchJobError! Operation failed {"timestamp" 1333488510315, "customer-id" 89045, "priority" "critical", "jobId" "verify-billings"}
```





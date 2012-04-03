;-----
(import 'java.net.URL)
;= java.net.URL
(def cnn (URL. "http://cnn.com"))
;= #'user/cnn 
(.getHost cnn)
;= "cnn.com"
(slurp cnn)
;= "<html lang=\"en\"><head><title>CNN.com………"   


;-----
Double/MAX_VALUE
;= 1.7976931348623157E308
(Double/parseDouble "3.141592653589793")
;= 3.141592653589793


;-----
(defn decimal-to-hex
  [x]
  (-> x
    Integer/parseInt
    (Integer/toString 16)
    .toUpperCase))
;= #'user/decimal-to-hex
(decimal-to-hex "255")
;= "FF"


;-----
public String stringToHex (String x) {
    return Integer.toString(Integer.parseInt(x), 16).toUpperCase();
}


;-----
(import 'java.awt.Point)
;= java.awt.Point
(def pt (Point. 5 10))
;= #'user/pt
(.x pt)
;= 5
(set! (.x pt) -42)
;= -42
(.x pt)
;= -42


;-----
(let [alist (ArrayList.)]
  (.add alist 1)
  (.add alist 2)
  (.add alist 3)
  alist)


;-----
(doto (ArrayList.)
  (.add 1)
  (.add 2)
  (.add 3))


;-----
(doto graphics
  (.setBackground Color/white)
  (.setColor Color/black)
  (.scale 2 2)
  (.clearRect 0 0 500 500)
  (.drawRect 100 100 300 300))


;-----
// Java
public static Integer asInt (String s) {
    try {
        return Integer.parseInt(s);
    } catch (NumberFormatException e) {
        e.printStackTrace();
        return null;
    } finally {
        System.out.println("Attempted to parse as integer: " + s);
    }
}

# Ruby
def as_int (s)
    begin
        return Integer(s)
    rescue Exception => e
        puts e.backtrace
    ensure
        puts "Attempted to parse as integer: " + s
    end
end

; Clojure
(defn as-int
  [s]
  (try
    (Integer/parseInt s)
    (catch NumberFormatException e
      (.printStackTrace e))
    (finally
      (println "Attempted to parse as integer: " s))))


;-----
(throw (IllegalStateException. "I don't know what to do!"))
;= #<IllegalStateException java.lang.IllegalStateException: I don't know what to do!>


;-----
(throw "foo")
;= #<ClassCastException java.lang.ClassCastException:
;=   java.lang.String cannot be cast to java.lang.Throwable>


;-----
(File/createTempFile "clojureTempFile" ".txt")


;-----
public static void appendTo (File f, String text) throws IOException {
    Writer w = null;
    try {
        w = new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8");
        w.write(text);
        w.flush();
    } finally {
        if (w != null) w.close();
    }
}


;-----
public static void appendTo (File f, String text) throws IOException {
  try (Writer w = new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8")) {
      w.write(text);
      w.flush();
  }
}


;-----
(require '[clojure.java.io :as io])

(defn append-to
  [f text]
  (with-open [w (io/writer f :append true)]
    (doto w (.write text) .flush)))


;-----
(defn copy-files
  [from to]
  (with-open [in (FileInputStream. from)
              out (FileOutputStream. to)]
    (loop [buf (make-array Byte/TYPE 1024)]
      (let [len (.read in buf)]
        (when (pos? len)
          (.write out buf 0 len)
          (recur buf))))))


;-----
(defn length-of
  [^String text]
  (.length text))


;-----
(defn silly-function
  [v]
  (nil? v))


;-----
(defn accepts-anything
  [^java.util.List x]
  x)
;= #'user/accepts-anything
(accepts-anything (java.util.ArrayList.))
;= #<ArrayList []>
(accepts-anything 5)
;= 5
(accepts-anything false)
;= false


;-----
(defn capitalize
  [s]
  (-> s
    (.charAt 0)
    Character/toUpperCase
    (str (.substring s 1))))


;-----
(time (doseq [s (repeat 100000 "foo")]
        (capitalize s)))
; "Elapsed time: 5040.218 msecs"


;-----
(set! *warn-on-reflection* true)
;= true
(defn capitalize
  [s]
  (-> s
    (.charAt 0)
    Character/toUpperCase
    (str (.substring s 1))))
; Reflection warning, NO_SOURCE_PATH:27 - call to charAt can't be resolved.
; Reflection warning, NO_SOURCE_PATH:29 - call to toUpperCase can't be resolved.
; Reflection warning, NO_SOURCE_PATH:29 - call to substring can't be resolved.
;= #'user/capitalize


;-----
(defn fast-capitalize
  [^String s]
  (-> s
    (.charAt 0)
    Character/toUpperCase
    (str (.substring s 1))))


;-----
(time (doseq [s (repeat 100000 "foo")]
        (fast-capitalize s)))
; "Elapsed time: 154.889 msecs"


;-----
(defn split-name
  [user]
  (zipmap [:first :last]
    (.split (:name user) " ")))
;= #'user/split-name
; Reflection warning, NO_SOURCE_PATH:3 - call to split can't be resolved.
(split-name {:name "Chas Emerick"})
;= {:last "Emerick", :first "Chas"}


;-----
(defn split-name
  [user]
  (let [^String full-name (:name user)]
    (zipmap [:first :last]
      (.split full-name " "))))
;= #'user/split-name


;-----
(defn split-name
  [user]
  (zipmap [:first :last]
    (.split ^String (:name user) " ")))
;= #'user/split-name


;-----
(defn file-extension
  [^java.io.File f]
  (-> (re-seq #"\.(.+)" (.getName f))
    first
    second))


;-----
(.toUpperCase (file-extension (java.io.File. "image.png")))
; Reflection warning, NO_SOURCE_PATH:1 - reference to field toUpperCase can't be resolved.
;= "PNG"


;-----
(defn file-extension
  ^String [^java.io.File f]
  (-> (re-seq #"\.(.+)" (.getName f))
    first
    second))


;-----
(.toUpperCase (file-extension (java.io.File. "image.png")))
;= "PNG"


;-----
(def a "image.png")
;= #'user/a
(java.io.File. a)
; Reflection warning, NO_SOURCE_PATH:1 - call to java.io.File ctor can't be resolved.
;= #<File image.png>
(def ^String a "image.png")
;= #'user/a
(java.io.File. a)
;= #<File image.png>


;-----
(map #(Character/toUpperCase %) (.toCharArray "Clojure"))
;= (\C \L \O \J \U \R \E)


;-----
(defn lru-cache
  [max-size]
  (proxy [java.util.LinkedHashMap] [16 0.75 true]
    (removeEldestEntry [entry]
      (> (count this) max-size))))


;-----
(def cache (doto (lru-cache 5)
             (.put :a :b)))
;= #'user/cache
cache
;= #<LinkedHashMap$0 {:a=:b}>                                       
(doseq [[k v] (partition 2 (range 500))]
  (get cache :a)
  (.put cache k v))
;= nil
cache
;= #<LinkedHashMap$0 {492=493, 494=495, 496=497, :a=:b, 498=499}>


;-----
(ns com.clojurebook.imaging
  (:use [clojure.java.io :only (file)])
  (:import (java.awt Image Graphics2D)
           javax.imageio.ImageIO
           java.awt.image.BufferedImage
           java.awt.geom.AffineTransform))

(defn load-image
  [file-or-path]
  (-> file-or-path file ImageIO/read))

(defn resize-image
  ^BufferedImage [^Image original factor]
  (let [scaled (BufferedImage. (* factor (.getWidth original))
                               (* factor (.getHeight original))
                               (.getType original))]
    (.drawImage ^Graphics2D (.getGraphics scaled)
                original
                (AffineTransform/getScaleInstance factor factor)
                nil)
    scaled))

(gen-class
  :name ResizeImage
  :main true
  :methods [^:static [resizeFile [String String double] void]
            ^:static [resize [java.awt.Image double] java.awt.image.BufferedImage]])

(def ^:private -resize resize-image)

(defn- -resizeFile
  [path outpath factor]
  (ImageIO/write (-> path load-image (resize-image factor))
                 "png"
                 (file outpath)))

(defn -main
  [& [path outpath factor]]
  (when-not (and path outpath factor)
    (println "Usage: java -jar example-uberjar.jar ResizeImage [INFILE] [OUTFILE] [SCALE]")
    (System/exit 1))
  (-resizeFile path outpath (Double/parseDouble factor)))


;-----
java -cp gen-class-1.0.0-standalone.jar ResizeImage clojure.png resized.png 0.5


;-----
ResizeImage.resizeFile("clojure.png", "resized.png", 0.5);


;-----
(ns com.clojurebook.CustomException
  (:gen-class :extends RuntimeException
              :implements [clojure.lang.IDeref]
              :constructors {[java.util.Map String] [String]
                             [java.util.Map String Throwable] [String Throwable]}
              :init init                                                          
              :state info
              :methods [[getInfo [] java.util.Map]
                        [addInfo [Object Object] void]]))

(import 'com.clojurebook.CustomException)

(defn- -init
  ([info message]
    [[message] (atom (into {} info))])
  ([info message ex]
    [[message ex] (atom (into {} info))]))

(defn- -deref
  [^CustomException this]
  @(.info this))

(defn- -getInfo
  [this]
  @this)

(defn- -addInfo
  [^CustomException this key value]
  (swap! (.info this) assoc key value))


;-----
(import 'com.clojurebook.CustomException)
;= nil
(defn perform-operation
  [& [job priority :as args]]
  (throw (CustomException. {:arguments args} "Operation failed")))
;= #'user/perform-operation
(defn run-batch-job
  [customer-id]
  (doseq [[job priority] {:send-newsletter :low
                          :verify-billings :critical
                          :run-payroll :medium}]
    (try
      (perform-operation job priority)
      (catch CustomException e
        (swap! (.info e) merge {:customer-id customer-id
                                :timestamp (System/currentTimeMillis)})
        (throw e)))))
;= #'user/run-batch-job
(try
  (run-batch-job 89045)
  (catch CustomException e
    (println "Error!" (.getMessage) @e)))
; Error! Operation failed {:timestamp 1309935234556, :customer-id 89045,
;                          :arguments (:verify-billings :critical)}
;= nil


;-----
import com.clojurebook.CustomException;
import clojure.lang.PersistentHashMap;

public class BatchJob {
    private static void performOperation (String jobId, String priority) {
        throw new CustomException(PersistentHashMap.create("jobId", jobId,
                "priority", priority), "Operation failed");
    }
    
    private static void runBatchJob (int customerId) {
        try {
            performOperation("verify-billings", "critical");
        } catch (CustomException e) {
            e.addInfo("customer-id", customerId);
            e.addInfo("timestamp", System.currentTimeMillis());
            throw e;
        }
    }
    
    public static void main (String[] args) {
        try {
            runBatchJob(89045);
        } catch (CustomException e) {
            System.out.println("Error! " + e.getMessage() + " " + e.getInfo());
        }
    }
}


;-----
(ns com.clojurebook.annotations.junit
  (:import (org.junit Test Assert))
  (:gen-class
    :name com.clojurebook.annotations.JUnitTest
    :methods [[^{org.junit.Test true} simpleTest [] void]
              [^{org.junit.Test {:timeout 2000}} timeoutTest [] void]
              [^{org.junit.Test {:expected NullPointerException}}
                badException [] void]]))

(defn -simpleTest
  [this]
  (Assert/assertEquals (class this) com.clojurebook.annotations.JUnitTest))

(defn -badException
  [this]
  (Integer/parseInt (System/getProperty "nonexistent")))

(defn -timeoutTest
  [this]  
  (Thread/sleep 10000))


;-----
There were 2 failures:
1) timeoutTest(com.clojurebook.annotations.JUnitTest)
java.lang.Exception: test timed out after 2000 milliseconds
2) throwsWrongException(com.clojurebook.annotations.JUnitTest)
java.lang.Exception: Unexpected exception,
expected<java.lang.NullPointerException> but was<java.lang.NumberFormatException>


;-----
(ns com.clojurebook.annotations.jaxrs
  (:import (javax.ws.rs Path PathParam Produces GET)))

(definterface Greeting
  (greet [^String visitor-name]))

(deftype ^{Path "/greet/{visitorname}"} GreetingResource []
  Greeting
  (^{GET true
     Produces ["text/plain"]}
    greet
    [this ^{PathParam "visitorname"} visitor-name]
    (format "Hello %s!" visitor-name)))


;-----
(com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory/create
  "http://localhost:8080/"
  {"com.sun.jersey.config.property.packages" "com.clojurebook.annotations.jaxrs"})


;-----
(ns com.clojurebook.histogram)

(def keywords (map keyword '(a c a d b c a d c d k d a b b b c d e e e f a a a a)))


;-----
package com.clojurebook;

import java.util.ArrayList;
import java.util.Map;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public class JavaClojureInterop {
    private static IFn requireFn = RT.var("clojure.core", "require").fn();
    private static IFn randIntFn = RT.var("clojure.core", "rand-int").fn();          
    static {
        requireFn.invoke(Symbol.intern("com.clojurebook.histogram"));
    }
    
    private static IFn frequencies = RT.var("clojure.core", "frequencies").fn();
    private static Object keywords = RT.var("com.clojurebook.histogram",
            "keywords").deref();          
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String[] args) {
        Map<Keyword, Integer> sampleHistogram =          
            (Map<Keyword, Integer>)frequencies.invoke(keywords);
        System.out.println("Number of :a keywords in sample histogram: " +
                sampleHistogram.get(Keyword.intern("a")));
        System.out.println("Complete sample histogram: " + sampleHistogram);
        System.out.println();
        
        System.out.println("Histogram of chars in 'I left my heart in san fransisco': " +
                frequencies.invoke("I left my heart in San Fransisco".toLowerCase()));
        System.out.println();
        
        ArrayList randomInts = new ArrayList();
        for (int i = 0; i < 500; i++) randomInts.add(randIntFn.invoke(10));
        System.out.println("Histogram of 500 random ints [0,10): " +
                frequencies.invoke(randomInts));
    }
}


;-----
% java -cp target/java-clojure-interop-1.0.0-jar-with-dependencies.jar
       com.clojurebook.JavaClojureInterop
Number of :a keywords in sample histogram: 8
Complete sample histogram: {:a 8, :c 4, :d 5, :b 4, :k 1, :e 3, :f 1}

Frequences of chars in 'I left my heart in san fransisco':
{\space 6, \a 3, \c 1, \e 2, \f 2, \h 1, \i 3, \l 1, \m 1,
 \n 3, \o 1, \r 2, \s 3, \t 2, \y 1}

Frequences of 500 random ints [0,10):
{0 60, 1 61, 2 55, 3 46, 4 37, 5 45, 6 47, 7 52, 8 49, 9 48}


;-----
(ns com.clojurebook.classes)

(deftype Range
  [start end]
  Iterable
  (iterator [this]
    (.iterator (range start end))))

(defn string-range
  "Returns a Range instance based on start and end values provided as Strings
   in a list / vector / array."
  [[start end]]
  (Range. (Long/parseLong start) (Long/parseLong end)))

(defrecord OrderSummary
  [order-number total])


;-----
package com.clojurebook;

import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import com.clojurebook.classes.OrderSummary;
import com.clojurebook.classes.Range;

public class ClojureClassesInJava {
    private static IFn requireFn = RT.var("clojure.core", "require").fn();
    static {
        requireFn.invoke(Symbol.intern("com.clojurebook.classes"));
    }
    
    private static IFn stringRangeFn = RT.var("com.clojurebook.classes",
            "string-range").fn();
    
    public static void main(String[] args) {
        Range range = new Range(0, 5);
        System.out.print(range.start + "-" + range.end + ": ");
        for (Object i : range) System.out.print(i + " ");
        System.out.println();
        
        for (Object i : (Range)stringRangeFn.invoke(args))
            System.out.print(i + " ");
        System.out.println();
        
        OrderSummary summary = new OrderSummary(12345, "$19.45");
        System.out.println(String.format("order number: %s; order total: %s",
                summary.order_number, summary.total));
        System.out.println(summary.keySet());
        System.out.println(summary.values());
    }
}


;-----
% java -cp target/java-clojure-interop-1.0.0-jar-with-dependencies.jar 
     com.clojurebook.ClojureClassesInJava 5 10
0-5: 0 1 2 3 4 
5 6 7 8 9 
order number: 12345; order total: $19.45
#{:order-number :total}
(12345 "$19.45")


;-----
(ns com.clojurebook.protocol)

(defprotocol Talkable
  (speak [this]))

(extend-protocol Talkable
  String
  (speak [s] s)
  Object
  (speak [this]
    (str (-> this class .getName) "s can't talk!")))


;-----
package com.clojurebook;

import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import com.clojurebook.protocol.Talkable;

public class BitterTalkingDog implements Talkable {

    public Object speak() {
        return "You probably expect me to say 'woof!', don't you? Typical.";
    }
    
    Talkable mellow () {
        return new Talkable () {
            public Object speak() {
                return "It's a wonderful day, don't you think?";
            }
        };
    }
    
    public static void main(String[] args) {
        RT.var("clojure.core", "require").invoke(
            Symbol.intern("com.clojurebook.protocol"));
        IFn speakFn = RT.var("com.clojurebook.protocol", "speak").fn();
        
        BitterTalkingDog dog = new BitterTalkingDog();
        
        System.out.println(speakFn.invoke(5));                                   
        System.out.println(speakFn.invoke(                                      
            "A man may die, nations may rise and fall, but an idea lives on."));
        System.out.println(dog.speak());                                         
        System.out.println(speakFn.invoke(dog.mellow()));                        
    }
}


;-----
% java com.clojurebook.BitterTalkingDog
java.lang.Integers can't talk!
A man may die, nations may rise and fall, but an idea lives on.
You probably expect me to say 'woof!', don't you? Typical.
It's a wonderful day, don't you think?



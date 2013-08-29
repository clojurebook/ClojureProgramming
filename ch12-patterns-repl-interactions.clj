;-----
(defn- update-status*
  [service-name service-endpoint-url request-data-fn]
  (fn [new-status]
    (log (format "Updating status @ %s to %s" service-name new-status))
    (let [http-request-data (request-data-fn new-status)
          connection (-> service-endpoint-url java.net.URL. .openConnection)]
      ;; ...set request method, parameters, body on `connection`
      ;; ...perform actual request
      ;; ...return result based on HTTP response status
      )))

(def update-facebook-status (update-status* "Facebook" "http://facebook.com/apis/..."
                              (fn [status]
                                {:params {:_a "update_status"
                                          :_t status}
                                 :method "GET"})))

(def update-twitter-status ...)
(def update-google-status ...)


;-----
interface IDog {
    public String bark();
}

class Chihuahua implements IDog {
    public String bark() {
        return "Yip!";
    }
}

class Mastiff implements IDog {
    public String bark() {
        return "Woof!";
    }
}

class PetStore {
    private IDog dog;
    public PetStore() {
        this.dog = new Mastiff();
    }

    public IDog getDog() {
        return dog;
    }
}

static class MyApp {
    public static void main(String[] args) {
        PetStore store = new PetStore();
        System.out.println(store.getDog().bark());
    }
}


;-----
class PetStore {
    private IDog dog;
    public PetStore(IDog dog) {
        this.dog = dog;
    }

    public IDog getDog() {
        return dog;
    }
}

class MyApp {
    public static void main(String[] args) {
        PetStore store = new PetStore(new Chihuahua());
        System.out.println(store.getDog().bark());
    }
}


;-----
(defprotocol Bark
  (bark [this]))

(defrecord Chihuahua []
  Bark
  (bark [this] "Yip!"))

(defrecord Mastiff []
  Bark
  (bark [this] "Woof!"))


;-----
(defrecord PetStore [dog])

(defn main
  [dog]
  (let [store (PetStore. dog)]
    (println (bark (:dog store)))))

(main (Chihuahua.))
;= Yip!

(main (Mastiff.))
;= Woof!


;-----
(extend-protocol Bark
  java.util.Map
  (bark [this]
    (or (:bark this)
        (get this "bark"))))


;-----
(main (doto (java.util.HashMap.)
        (.put "bark" "Ouah!")))
;= Ouah!

(main {:bark "Wan-wan!"})
;= Wan wan!


;-----
{:dog #user.Chihuahua{:weight 12, :price "$84.50"}}


;-----
(defn configured-petstore
  []
  (-> "petstore-config.clj"
    slurp
    read-string
    map->PetStore))


;-----
(configured-petstore)
;= #user.PetStore{:dog #user.Chihuahua{:weight 12, :price "$84.50"}}


;-----
interface ISorter {
    public sort (int[] numbers);
}

class QuickSort implements ISorter {
    public sort (int[] numbers) { ... }
}

class MergeSort implements ISorter {
    public sort (int[] numbers) { ... }
}

class Sorter {
    private ISorter sorter;
    public Sorter (ISorter sorter) {
        this.sorter = sorter;
    }

    public execute (int[] numbers) {
        sorter.sort(numbers);
    }
}

class App {
    public ISorter chooseSorter () {
        if (...) {
            return new QuickSort();
        } else {
            return new MergeSort();
        }
    }
    public static void main(String[] args) {
        int[] numbers = {5,1,4,2,3};

        Sorter s = new Sorter(chooseSorter());

        s.execute(numbers);

        //... now use sorted numbers
    }
}


;-----
(defn quicksort [numbers] ...)

(defn mergesort [numbers] ...)

(defn choose-sorter
  []
  (if ...
    quicksort
    mergesort))

(defn main
  []
  (let [numbers [...]]
    ((choose-sorter) numbers)))


;-----
((comp reverse sort) [2 1 3])
;= (3 2 1)


;-----
abstract class Processor {
    protected Processor next;
    public addToChain(Processor p) {
        next = p;
    }
    public runChain(data) {
        Boolean continue = this.process(data);
        if(continue and next != null) {
            next.runChain(data);
        }
    }
    abstract public boolean process(String data);
}

class FooProcessor extends Processor {
    public boolean process(String data) {
        System.out.println("FOO says pass...");
        return true;
    }
}

class BarProcessor extends Processor {
    public boolean process(String data) {
        System.out.println("BAR " + data + " and let's stop here");
        return false;
    }
}

class BazProcessor extends Processor {
    public boolean process(String data) {
        System.out.println("BAZ?");
        return true;
    }
}

Processor chain = new FooProcessor().addToChain(new BarProcessor).addToChain(new BazProcessor);
chain.run("data123");


;-----
(defn foo [data]
  (println "FOO passes")
  true)

(defn bar [data]
  (println "BAR" data "and let's stop here")
  false)

(defn baz [data]
  (println "BAZ?")
  true)

(defn wrap [f1 f2]
  (fn [data]
    (when (f1 data)
      (f2 data))))

(def chain (reduce wrap [foo bar baz]))


;-----
(defn my-app
  [request]
  {:status 200
   :headers {"Content-type" "text/html"}
   :body (format "<html><body>You requested: %s</body></html>"
           (:uri request))})


;-----
(defn wrap-logger
  [handler]
  (fn [request]
    (println (:uri request))
    (handler request)))


;-----
(require '[ring.middleware cookies session])

(def my-app (-> my-app
                wrap-cookies
                wrap-session
                wrap-logger))


;-----
public class Foo
    public void expensiveComputation () {
        long start = System.currentTimeMillis();
        try {
            // do computation
        } catch (Exception e) {
            // log error
        } finally {
            long stop = System.currentTimeMillis();
            System.out.println("Run time: " + (stop - start) + "ms");
        }
    }
}


;-----
public class AspectJExample {
    public void longRunningMethod () {
        System.out.println("Starting long-running method"); 
        try {
            Thread.sleep((long)(1000 + Math.random() * 2000));
        } catch (InterruptedException e) {
        }
    }
}


;-----
public aspect Timing {
    pointcut profiledMethods(): call(* AspectJExample.* (..));
    
    long time;
    
    before(): profiledMethods() {
        time = System.currentTimeMillis();
    }
    
    after(): profiledMethods() {
        System.out.println("Call to " + thisJoinPoint.getSignature() +
                " took " + (System.currentTimeMillis() - time) + "ms");
    }
}


;-----
Starting long-running method
Call to void com.clojurebook.AspectJExample.longRunningMethod() took 1599ms


;-----
(defn time-it [f & args]
  (let [start (System/currentTimeMillis)]
    (try
      (apply f args)
      (finally
       (println "Run time: " (- (System/currentTimeMillis) start) "ms")))))


;-----
(require 'robert.hooke)

(defn foo [x y]                     
  (Thread/sleep (rand-int 1000))
  (+ x y))

(robert.hooke/add-hook #'foo time-it)


;-----
(foo 1 2)
; Run time:  772 ms
;= 3


;-----
(robert.hooke/with-hooks-disabled foo (foo 1 2))
;= 3

(robert.hooke/remove-hook #'foo time-it)
;= #<user$foo user$foo@4f13f501>
(foo 1 2)
;= 3


;-----
(require 'clojure.set)
;= nil
(doseq [var (->> (ns-publics 'clojure.set)
                 (map val))]
  (robert.hooke/add-hook var time-it))
;= nil
(clojure.set/intersection (set (range 100000))
                          (set (range -100000 10)))
; Run time:  97 ms
;= #{0 1 2 3 4 5 6 7 8 9}



(ns com.clojurebook.CustomException
  (:gen-class :extends RuntimeException
              :implements [clojure.lang.IDeref]
              :constructors {[java.util.Map String] [String]
                             [java.util.Map String Throwable] [String Throwable]}
              :state info
              :init init
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
(ns com.clojurebook.annotations.junit
  (:import (org.junit Test Assert))
  (:gen-class
    :name com.clojurebook.annotations.JUnitTest
    :methods [[^{org.junit.Test true} simpleTest [] void]
              [^{org.junit.Test {:timeout 2000}} timeoutTest [] void]
              [^{org.junit.Test {:expected NullPointerException}} badException []
              void]]))

(defn -simpleTest
  [this]
  (Assert/assertEquals (class this) com.clojurebook.annotations.JUnitTest))

(defn -badException
  [this]
  (Integer/parseInt (System/getProperty "nonexistent")))

(defn -timeoutTest
  [this]
  (Thread/sleep 10000))

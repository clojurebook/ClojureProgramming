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
    (println "Usage: java -jar example-uberjar.jar ResizeImage [INFILE] [OUTFILE] [SCALE FACTOR]")
    (System/exit 1))
  (-resizeFile path outpath (Double/parseDouble factor)))
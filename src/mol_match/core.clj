(ns mol-match.core
  (:require [mol-match.matcher :as mm]
            [clojure.string :as s]
            [clojure.math.numeric-tower :as math]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(defn delimit [delimiter x y] (str x delimiter y))

(defn tab-sep-line [row newline]
  (str (reduce #(delimit "\t" % %2) row) newline))

(defn write-file [out-file list-of-lists]
  (spit out-file "" :append false)
  (with-open [wrt (io/writer out-file)]
    (doseq [list list-of-lists] (doseq [item list] (.write wrt (tab-sep-line item "\n"))))))

(defn write-stdout [list-of-lists]
  (doseq [list list-of-lists] (doseq [item list] (println (tab-sep-line item nil)))))

(defn -main
  [seq-length file-name]
  (try
    (println "mol-match: matching sequences of length:" seq-length)
    (let [result (mm/match-sequences seq-length)]
      (write-file file-name  result)
      (write-stdout result))
    (catch Exception e
      (println (str "Error: "  (.toString e) " (did you enter a number?)"))
      (println (.printStackTrace e)))))

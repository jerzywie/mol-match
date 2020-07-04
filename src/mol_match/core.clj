(ns mol-match.core
  (:require [clojure.string :as s]
            [clojure.math.numeric-tower :as math]
            [clojure.java.io :as io])
  (:gen-class))

(def carbonyl-group \C)
(def amide-group \N)

(def top-a (apply str [amide-group \x carbonyl-group]))
(def top-b (apply str [amide-group \x \x carbonyl-group]))

(def bot-a (s/reverse top-a))
(def bot-b (s/reverse top-b))

(def group-map {:top {:a top-a :b top-b} :bot {:a bot-a :b bot-b}})


(defn letter-to-group
  [tb letter]
  (cond
    (= letter \A) (get-in group-map [tb :a])
    (= letter \B) (get-in group-map [tb :b])))

(defn digit-to-letter
  [d]
  (cond
    (= d \0) \A
    (= d \1) \B))

(defn make-seq
  [tb letter-seq]
  (->> letter-seq
       seq
       (map (partial letter-to-group tb))
       (apply str)))

(defn matched-pair?
  [t b]
  (or
   (and (= t \C) (= b \N))
   (and (= t \N) (= b \C))))

(defn count-matches-helper
  [top-seq bot-seq]
  (let [even-matches (take-nth 2 (map matched-pair? top-seq bot-seq))
        match-count (count (filter true? even-matches))]
    match-count))

(defn count-matches
  [letter-seqa letter-seqb]
  ;(println "count-matches" letter-seqa letter-seqb)
  (let [top-seq (make-seq :top letter-seqa)
        bot-seq (make-seq :bot letter-seqb)]
    (count-matches-helper top-seq bot-seq)))

(defn make-binary-seq
  [length digits]
  (let [binary-digits (Integer/toBinaryString digits)
        bd-with-zeros (flatten (conj (seq binary-digits) (repeat length \0)))]
    (take-last length bd-with-zeros)))

(defn binary->letter-seq
  [length digits]
  (let [bd (make-binary-seq length digits)]
    (map digit-to-letter bd)))

(defn match-sequences
  [seq-length-str]
  (let [seq-length (Integer/valueOf seq-length-str)
        seq-max (math/expt 2 seq-length)
        afn (fn [m]
              (let [letter-seqa (binary->letter-seq seq-length m)
                    bfn (fn [n]
                          (let [letter-seqb (binary->letter-seq seq-length n)]
                            [(apply str letter-seqa) (apply str letter-seqb) (count-matches letter-seqa letter-seqb) (if (= m n) 1 0)]))]
                (map bfn (range seq-max))))]
    (map afn (range seq-max))))

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
    (let [result (match-sequences seq-length)]
      (write-file file-name  result)
      (write-stdout result))
    (catch Exception e
      (println (str "Error: "  (.toString e) " (did you enter a number?)"))
      (println (.printStackTrace e)))))

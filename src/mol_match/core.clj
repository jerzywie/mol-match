(ns mol-match.core
  (:require [mol-match.matcher :as mm]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def cli-options
  [;; First three strings describe a short-option, long-option with optional
   ;; example argument description, and a description. All three are optional
   ;; and positional.
   ["-l" "--seq-length LENGTH" "Sequence length."
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-v" "--verbose" "Verbose mode - output to console as well as file."]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Mol-match"
        "Usage: java -jar mol-match.jar [options] output-file"
        ""
        "Options:"
        options-summary
        ""
        "Argument: output-file to contain results in tab-separated format"]
       (s/join \newline)
       (println)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (s/join \newline errors)))

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
  [& args]
  (try
    (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
      (cond
        (:help options) (usage summary)
        (not= (count arguments) 1) (usage summary)
        (some? errors) (println (error-msg errors))
        :else (let [seq-length-str (:seq-length options)
                    verbose? (:verbose options)
                    result (mm/match-sequences seq-length-str)]
                (write-file (first arguments) result)
                (if verbose? (write-stdout result)))))
    (catch Exception e
      (println (str "Error: "  (.toString e) " (did you enter a number?)"))
      (println (.printStackTrace e)))))

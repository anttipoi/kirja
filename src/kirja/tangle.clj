(ns kirja.tangle
  (:use [kirja.common :only [source-files
                             chunk-stream
                             output-file]])
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]))

(defn- tangle-file
  [source-file output-file]
  (println (str "tangling... " (.getName source-file) " into " (.getName output-file)))
  (with-open [in-reader (io/reader source-file)
              out-writer (io/writer output-file)]
    (let [code-chunks (filter (fn [[kind data]] (= kind :code))
                              (chunk-stream (line-seq in-reader)))
          code (str/join "\n" code-chunks)]
      (.write out-writer code))))

(defn tangle
  [source-dir-name output-dir-name]
  (println (str "tangle in " source-dir-name))
  (let [source-dir (File. source-dir-name)
        output-dir (File. output-dir-name)]
    (for [source-f (source-files source-dir)]
      (tangle-file source-f (output-file source-f source-dir output-dir ".clj")))))


  
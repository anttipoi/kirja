(ns kirja.tangle
  (:use [kirja.common :only [source-files
                             chunk-stream
                             output-file
                             fixed-point]])
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]))

(defn body-to-str
  [body]
  (str (str/join "\n" body)
       "\n"))

(defn escape-backslash
  [text]
  (doall (str/replace text "\\" "\\\\")))

(defn expand-includes
  [chunks-map text]
  (str/replace text #"(?m)^<<include (.*)>>" (fn [match] (if (= 1 (count match))
                                                      (first match)
                                                      (let [{body :body} (get chunks-map (second match))]
                                                        (escape-backslash (body-to-str body)))))))

(defn expand-chunk
  "Given chunk and a mapping from chunk names to chunks, return the expanded
code contents for chunk." 
  [{body :body} chunks-map]
  (fixed-point (partial expand-includes chunks-map)
                    (body-to-str body)))
  

(defn collect-code-chunks
  "given a seq of chunks (doc or code), return a map mapping code chunk names to
code chunks. Ignore doc chunks."
  [chunks]
  (into {} (filter identity
                   (map (fn [{name :name :as ch}]
                          (when name [name ch]))
                        chunks))))

(defn- tangle-file
  [source-file output-file]
  (println (str "tangling... " (.getName source-file) " into " (.getName output-file)))
  (with-open [in-reader (io/reader source-file)
              out-writer (io/writer output-file)]
    (let [code-chunks (collect-code-chunks (chunk-stream (line-seq in-reader)))
          main-chunk (get code-chunks "main")
          code (expand-chunk main-chunk code-chunks)]
      (.write out-writer code))))

(defn tangle
  [source-dir-name output-dir-name]
  (println (str "tangle in " source-dir-name))
  (let [source-dir (File. source-dir-name)
        output-dir (File. output-dir-name)]
    (for [source-f (source-files source-dir)]
      (tangle-file source-f (output-file source-f source-dir output-dir ".clj")))))



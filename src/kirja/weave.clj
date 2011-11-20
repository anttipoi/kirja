(ns kirja.weave
  (:use [kirja.common :only [source-files
                             chunk-stream]])
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]
           [com.petebevin.markdown MarkdownProcessor]))

(defn chunk-to-md
  [[kind chunk-string]]
  (if (= kind :doc)
    chunk-string
    (str "    " (str/replace chunk-string #"\n" "\n    "))))

(defn convert-code-chunks
  [chunks]
  (map chunk-to-md chunks))

(defn- weave-file
  [source-file output-file]
  (println (str "weaving... " (.getName source-file) " into " (.getName output-file)))
  (with-open [in-reader (io/reader source-file)
              out-writer (io/writer output-file)]
    (let [chunks (chunk-stream (line-seq in-reader))
          doc-converted (str/join "\n" (convert-code-chunks chunks))
          mdp (MarkdownProcessor.)]
      (.write out-writer (.markdown mdp doc-converted)))))

(defn output-file
  "returns output file corresponding to source file"
  [^File source-file ^File source-dir ^File output-dir ^String suffix]
  (let [source-path (.getAbsolutePath source-file)
        source-dir-path (.getAbsolutePath source-dir)
        output-dir-path (.getAbsolutePath output-dir)
        output-path (-> source-path
                        (str/replace source-dir-path output-dir-path)
                        (str/replace #"\.[^\.]*$" suffix))
        output-file (File. output-path)
        parent-dir (.getParentFile output-file)]
    (.mkdirs parent-dir)
    output-file))

(defn weave
  [source-dir-name output-dir-name]
  (println (str "weave in " source-dir-name))
  (let [source-dir (File. source-dir-name)
        output-dir (File. output-dir-name)]
    (for [source-f (source-files source-dir)]
      (weave-file source-f (output-file source-f source-dir output-dir ".html")))))


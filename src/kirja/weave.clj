(ns kirja.weave
  (:use [kirja.common :only [source-files
                             chunk-stream
                             output-file]])
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]
           [com.petebevin.markdown MarkdownProcessor]))

(defn chunk-to-md
  [{:keys [type name body]}]
  (if (= type :doc)
    (str/join "\n" body)
    (str/join "\n" (concat
                    [(str "    <<" name ">>")]
                    (map #(str "    " %) body)
                    ["    <<end>>"]))))

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


(defn weave
  [source-dir-name output-dir-name]
  (println (str "weave in " source-dir-name))
  (let [source-dir (File. source-dir-name)
        output-dir (File. output-dir-name)]
    (for [source-f (source-files source-dir)]
      (weave-file source-f (output-file source-f source-dir output-dir ".html")))))


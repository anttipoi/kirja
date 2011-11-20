(ns kirja.common
  (:require [clojure.string :as str])
  (:import [java.io File]))

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

(defn is-our-src?
  [^File f]
  (and (.isFile f)
       (.endsWith (.getName f) ".krj")))

(defn source-files
  [^File dir]
  (filter is-our-src? (file-seq dir)))

(defn take-while-and-1
  "like take-while but includes also the first item where pred becomes false"
  [pred coll]
  (concat
   (take-while pred coll)
   [(first (drop-while pred coll))]))


(defprotocol State
  "Parse state"
  (modeTag [this] "returns state discriminator")
  (parse [this lines] "parses lines. Returns pair: lines belonging to this chunk and remaining lines."))

(deftype CodeState []
  State
  (modeTag [this] :code)
  (parse [this lines]
    (let [lines-upto-end (take-while-and-1 #(not (re-matches #"<<end>>" %)) lines)]
      [lines-upto-end (drop (count lines-upto-end) lines)])))

(deftype DocState []
  State
  (modeTag [this] :doc)
  (parse [this lines]
    (let [lines-until-code (take-while #(not (re-matches #"<<.*>>" %)) lines)]
      [lines-until-code (drop (count lines-until-code) lines)])))

;; signal state for End-of-file
(deftype EofState  
  []
  State
  (modeTag [this] :eof)
  (parse [this lines]
         [[] []]))
  
(defn get-state
  "which mode starts with line? Code blocks start with <<C...>>, all other contents indicate doc mode."
  [line]
  (cond (nil? line) (EofState.)
        (re-matches #"^<<C.*>>" line) (CodeState.)
        true (DocState.)))

(defn chunk-for
  [mode lines]
  [mode (str/join "\n" lines)])

(defn chunk-stream
  [lines]
  (let [state (get-state (first lines))
        [chunk-lines remaining-lines] (.parse state lines)
        mode (.modeTag state)]
    (when (not= mode :eof)
      (lazy-seq (cons
                 (chunk-for mode chunk-lines)
                 (chunk-stream remaining-lines))))))



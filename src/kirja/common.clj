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

(defprotocol State
  "Parse state"
  (modeTag [this] "returns state discriminator")
  (parse [this lines] "parses lines. Returns pair: parsed chunk and remaining lines."))

(deftype CodeState []
  State
  (modeTag [this] :code)
  (parse [this lines]
    (let [chunk-name (nth (re-matches #"<<(.*)>>" (first lines)) 1)
          chunk-lines (take-while #(not (re-matches #"<<end>>" %)) (rest lines))
          code-chunk {:type :code
                      :name chunk-name
                      :body chunk-lines}
          remaining-lines (drop (+ 2 (count chunk-lines)) lines)]
      [code-chunk remaining-lines])))

(deftype DocState []
  State
  (modeTag [this] :doc)
  (parse [this lines]
    (let [lines-until-code (take-while #(not (re-matches #"<<.*>>" %)) lines)
          doc-chunk {:type :doc
                     :body lines-until-code}
          remaining-lines (drop (count lines-until-code) lines)]
      [doc-chunk remaining-lines])))

;; signal state for End-of-file
(deftype EofState  
  []
  State
  (modeTag [this] :eof)
  (parse [this lines]
         [[] []]))

(defn get-state
  "which mode starts with line? Code blocks start with <<name>>, all other contents indicate doc mode."
  [line]
  (cond (nil? line) (EofState.)
        (re-matches #"^<<.*>>" line) (CodeState.)
        true (DocState.)))

(defn chunk-stream
  [lines]
  (let [state (get-state (first lines))
        [chunk remaining-lines] (.parse state lines)
        mode (.modeTag state)]
    (if (= mode :eof)
      []
      (lazy-seq (cons
                 chunk
                 (chunk-stream remaining-lines))))))

(defn first-repetition
  "return first immediately repeating element in sequence."
  [seq]
  (first (first (filter (fn [[x y]] (= x y))
                        (partition 2 1 seq)))))
   
(defn fixed-point
  "invoke f on the input, then repeatedly on the result. Stop when fixed point
is reached: the result no longer changes"
  [f input]
  (first-repetition (iterate f input)))


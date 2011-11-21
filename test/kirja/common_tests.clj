(ns kirja.common-tests
  (:use clojure.test
        kirja.common
        midje.sweet))

(deftest fixed-point-for-eventually-repeating-function
  (let [test-f (fn [x] (min 10 (+ 1 x)))]
    (fact
     (fixed-point test-f 0) => 10)))

(deftest parse-code-chunk-works
  (fact
   (parse-code-chunk ["<<name>>" "1" "3" "<<end>>"]) => {:type :code
                                                         :name "name"
                                                         :body ["1" "3"]}))
   
(deftest chunk-stream-test
  (let [code-1 ["<<c1>>" "line1" "line2" "<<end>>"]
        code-2 ["<<c2>>" "line21" "<<end>>"]
        doc-1 ["# doc chunk" "" "data"]
        code-chunk-1 {:type :code
                      :name "c1"
                      :body ["line1" "line2"]}
        code-chunk-2 {:type :code
                      :name "c2"
                      :body ["line21"]}
        doc-chunk-1 {:type :doc
                     :body ["# doc chunk" "" "data"]}]
    (facts
     (chunk-stream []) => []
     (chunk-stream code-1) => [code-chunk-1]
     (chunk-stream doc-1) => [doc-chunk-1]
     (chunk-stream (concat code-1 doc-1 code-2)) => [code-chunk-1 doc-chunk-1 code-chunk-2])))
    
        
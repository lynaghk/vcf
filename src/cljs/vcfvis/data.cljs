(ns vcfvis.data
  (:use-macros [c2.util :only [pp p]])
  (:use [c2.util :only [clj->js]]
        [cljs.reader :only [read-string]]))

(defn load-metrics [file-urls callback]
  (when (seq file-urls)
    (.get js/jQuery "/api/metrics"
          (clj->js {:file-urls file-urls})
          (fn [d]
            (let [res (read-string d)]
              (callback res))))))


(defn available-files [callback]
  (.get js/jQuery "/api/files"
        (fn [d]
          (let [res (read-string d)]
            (callback res)))))

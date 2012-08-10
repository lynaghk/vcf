(ns vcfvis.data
  (:use-macros [c2.util :only [pp p]]
               [reflex.macros :only [constrain!]])
  (:use [c2.util :only [clj->js]]
        [cljs.reader :only [read-string]]))

(defn load-metrics [file-urls callback]
  (if-not (seq file-urls)
    (callback []) ;;empty result
    (.get js/jQuery "/api/metrics"
          (clj->js {:file-urls file-urls})
          (fn [d]
            (let [res (read-string d)]
              (callback res))))))

(defn load-context [callback]
  (.get js/jQuery "/api/context"
        (fn [d]
          (let [res (read-string d)]
            (callback res)))))


(def !analysis-status
  "File analysis status---are analyses running, completed, &c.?
   Keyed by filename."
  (atom {}))

(defn update-status! [filename status]
  (swap! !analysis-status assoc-in [filename] status))

(defn reset-statuses! []
  (reset! !analysis-status {}))

(defn filter-analysis [opts]
  (let [{:keys [file-url]} opts]
    (update-status! file-url :running)
    (.post js/jQuery "/api/filter" (clj->js opts)
           (fn [d]
             (let [res (read-string d)]
               (update-status! file-url :completed))))))

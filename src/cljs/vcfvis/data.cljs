(ns vcfvis.data
  (:use-macros [c2.util :only [pp p]]
               [reflex.macros :only [constrain!]])
  (:use [c2.util :only [clj->js]]
        [cljs.reader :only [read-string]])
  (:require [vcfvis.core :as core]))


(def num-bins 100)

(defn expand-metric [metric-id]
  (if-let [m (-> @core/!context :metrics metric-id)]
    (merge {:id metric-id} m)
    (throw (str "No metric information for metric-id: " metric-id))))

(defn prep-vcf-json [vcf-json]
  (let [info (read-string (aget vcf-json "clj"))
        cf (js/crossfilter (aget vcf-json "raw"))]
    (merge (update-in info [:available-metrics] #(set (map expand-metric %)))
           {:cf (into {:crossfilter cf}
                      (for [metric (info :available-metrics)]
                        (let [[start end] (get-in @core/!context [:metrics metric :range])
                              bin-width (/ (- end start) num-bins)
                              dim (.dimension cf #(aget % metric))
                              binned (.group dim (fn [x]
                                                   (+ start (* bin-width
                                                               ;;take the min to catch any roundoff into the last bin
                                                               (min (Math/floor (/ (- x start) bin-width))
                                                                    (dec num-bins))))))]
                          [metric {:bin-width bin-width
                                   :dimension dim
                                   :binned binned}])))})))



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

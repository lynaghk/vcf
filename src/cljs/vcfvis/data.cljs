(ns vcfvis.data
  (:use-macros [c2.util :only [pp p]]
               [reflex.macros :only [constrain!]])
  (:use [c2.util :only [clj->js]]
        [cljs.reader :only [read-string]])
  (:require [vcfvis.core :as core]
            [vcfvis.ui :as ui]
            [c2.scale :as scale]
            [c2.ticks :as ticks]))


(def num-bins 100)

(defn expand-metric
  "Adds bin-width, filter extent atom, and x-scale with tick marks to a metric."
  [metric]
  (if (metric :range)
    (assoc metric
      :bin-width (let [[start end] (metric :range)]
                   (/ (- end start) num-bins))
      :!filter-extent (atom nil)
      :scale-x (let [{:keys [ticks]} (ticks/search (metric :range)
                                                   :clamp? true :length ui/hist-width)
                     x (scale/linear :domain (metric :range)
                                     :range [0 ui/hist-width])]
                 (assoc x :ticks ticks)))
    (throw (str "Metric doesn't have range: " (pr metric)))))

(defn prep-context [context]
  (update-in context [:metrics]
             #(reduce (fn [res [id m]]
                        (assoc res id
                               (-> m
                                   expand-metric
                                   (assoc :id id))))
                      {} %)))

(defn prep-vcf-json [vcf-json]
  (let [core-metrics (@core/!context :metrics)
        info (-> (read-string (aget vcf-json "clj"))
                 ;;Expand metric-ids to full metric maps available in context (a join, basically)
                 (update-in [:available-metrics]
                            #(reduce (fn [ms m]
                                       (if-let [metric (core-metrics m)]
                                         (conj ms metric)
                                         (do (p (str "Don't know how to deal with metric: '" m "', dropping."))
                                             ms)))
                                     #{} %)))

        cf (js/crossfilter (aget vcf-json "raw"))]

    (assoc info
      :cf (into {:crossfilter cf
                 :all (.groupAll cf)}
                (for [{:keys [id range bin-width]} (info :available-metrics)]
                  (let [[start end] range
                        dim (.dimension cf #(aget % id))
                        binned (.group dim (fn [x]
                                             (+ start (* bin-width
                                                         ;;take the min to catch any roundoff into the last bin
                                                         (min (Math/floor (/ (- x start) bin-width))
                                                              (dec num-bins))))))]
                    [id {:bin-width bin-width
                         :dimension dim
                         :binned binned}]))))))
















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

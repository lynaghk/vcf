(ns vcfvis.core
  (:use-macros [c2.util :only [pp p interval]]
               [reflex.macros :only [computed-observable constrain!]]
               [dubstep.macros :only [publish! subscribe!]])
  (:use [c2.util :only [clj->js]])
  (:require [clojure.set :as set]
            [reflex.core :as reflex]
            [dubstep.pubsub :as dubstep.pubsub]))

;;;;;;;;;;;;;;;;;;;;
;;From server

(def !context
  "Background info from server; includes descriptions and defaults for different metrics, user-specific info like available files, &c."
  (atom {}))

(def !vcfs
  "VCFs currently under analysis."
  (atom []))

;;;;;;;;;;;;;;;;;;;;
;;Derived

(def !available-files
  "User's files available on the server."
  (computed-observable
   (get @!context :files [])))

;;Y U No haz identity, clojure.set/intersection!?!?!
(defn intersection
  ([] #{})
  ([& args] (apply set/intersection args)))

(def !shared-metrics
  "Metrics"
  (computed-observable
   ;;TODO, order metrics by relevance/usefulness to biologists.
   (reduce intersection (map :available-metrics @!vcfs))))

;;;;;;;;;;;;;;;;;;;;
;;From UI

(def !metric
  "The metric to be displayed on the main histogram."
  (atom nil))

(defn select-metric! [metric]
  (reset! !metric metric))

(constrain! ;;Make sure that the selected metric is always one of the shared metrics
 (let [shared @!shared-metrics]
   (when (seq shared)
     (when-not (some #{@!metric} shared)
       (select-metric! (first shared))))))

(def !visible-metrics
  "Metrics that should be shown with mini-histograms the UI"
  (atom #{}
        :validator
        (fn [ms]
          (if (contains? ms @!metric)
            (throw (js/Error. "Selected metric doesn't need a mini-histogram."))
            true))))

(constrain!
 (let [m @!metric]
   (when (contains? @!visible-metrics m)
     (swap! !visible-metrics disj m))))

(defn visible-metric? [m]
  (contains? @!visible-metrics m))

(defn toggle-visible-metric! [m]
  (swap! !visible-metrics
         (if (visible-metric? m) disj conj)
         m))

(defn update-metric! [m extent]
  (let [vcf (first @!vcfs)] ;;TODO, faceting
    ;;Update the crossfilter for each VCF
    (.filter (get-in vcf [:cf (m :id) :dimension])
             (clj->js extent))

    ;;Save extent in metric's atom
    (reset! (m :!filter-extent) extent)

    (publish! {:filter-updated m})))





(defn current-filters []
  (reduce (fn [filters m]
            (if-let [extent @(m :!filter-extent)]
              (assoc filters (m :id) extent)
              filters))
          {} @!shared-metrics))

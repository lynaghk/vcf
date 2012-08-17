(ns vcf.scratch
  (:use-macros [c2.util :only [p pp]]
               [dubstep.macros :only [publish! subscribe!]])
  (:use [c2.util :only [clj->js]]
        [c2.core :only [unify]]
        [c2.maths :only [extent]])
  (:require [c2.dom :as dom]
            [c2.scale :as scale]
            [clojure.string :as str]
            [singult.core :as singult]
            [vcfvis.double-range :as double-range]))

(set! *print-fn* (fn [x] (p x)))

(def ^:dynamic *cf* nil)


(defn rounder [accuracy]
  (fn [x]
    (* (Math/floor (/ x accuracy))
       accuracy)))

(defn view* [name]
  [:div.hist-view
   [:h2 name]
   [:div.hist]
   [:div.range]])


(defn hist-view! [$container name
                  & {:keys [bin-width]
                     :or {bin-width 1}}]

  (let [$container (dom/append! $container (view* name))
        dimension (.dimension *cf* #(aget % name))
        xs (.group dimension (rounder bin-width))
        scale-x (scale/linear :domain [(.-key (first (.all xs)))
                                       (.-key (last (.all xs)))]
                              :range [0 (js/parseFloat (dom/style $container :width))])]

    ;;Range selector
    (let [$tt (dom/select ".range" $container)
          [min max] (:domain scale-x)
          cb (fn [extent]
               (pp {:extent-updated name :extent extent})
               (publish!  {:extent-updated name :extent extent}))]
      (doto (double-range/init! $tt cb)
        (.setMinimum min) (.setMaximum max)
        (.setStep bin-width) (.setMinExtent bin-width) (.setBlockIncrement bin-width)
        (.setValueAndExtent min (- max min))))

    ;;update filter dimension whenever extent changes
    (subscribe! {:extent-updated grr :extent extent}
                (when (= grr name)
                  (p (str "updating " name))
                  (.filter dimension (clj->js extent))
                  (publish! {:filter-updated dimension})))

    ;;Crossfilter-powered histogram
    (let [[_ width] (:range scale-x)
          height 200
          ;;y-axis is free---counts are always scaled to fit it
          scale-y (scale/linear :range [0 height])
          $hist (dom/append! (dom/select ".hist" $container)
                             [:svg.hist {:width width :height height}
                              [:g.bars]])]

      (subscribe! {:filter-updated _}
                  (let [scale-y (assoc scale-y :domain  [0 (.-value (first (.top xs 1)))])]
                    (singult/merge! (dom/select ".bars" $hist)
                                    [:g.bars
                                     (unify (.all xs)
                                            (fn [d]
                                              (let [x (.-key d), count (.-value d)
                                                    h (scale-y count)]
                                                [:rect.bar {:x (scale-x x)
                                                            :y (- height h)
                                                            :width (- (scale-x (+ x bin-width))
                                                                      (scale-x x))
                                                            :height h}]))
                                            ;;Mutable JavaScript objects muck up singult semantics; always force updates
                                            :force-update? true)]))))
    nil))






(.getJSON js/$ "big.json"
          (fn [ds]
            (set! *cf* (js/crossfilter ds))

            (let [scale (scale/linear :domain [0 100]
                                      :range [0 600])]

              (hist-view! "body" "MQ" :bin-width 1)
              (hist-view! "body" "QD" :bin-width 5)
              #_(doseq  [dim ["AD" "DP" "HaplotypeScore"; "MQ" "PL" "QD" "QUAL" "ReadPosEndDist"
                              ]]
                  (hist-view! "body" dim scale
                              :bin-width 100))
              )))
(defn grr [x]
  (subscribe! {:x x} (p x)))

(grr "abc")
(grr "def")

(publish! {:x "abc"})

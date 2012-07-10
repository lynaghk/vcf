(ns vcfvis.histogram
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [computed-observable constrain!]]
               [clojure.core.match.js :only [match]])
  (:use [c2.core :only [unify]]
        [c2.maths :only [irange extent]])
  (:require [vcfvis.core :as core]
            [vcfvis.double-range :as double-range]
            [c2.dom :as dom]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [c2.ticks :as ticks]))

(def margin 20)

;;width and height of data frame
(def height 200)
(def width 900)

(defn histograms* [vcfs x-scale]
  (let [metric-id (@core/!metric :id)
        metrics (map #(core/vcf-metric % metric-id) vcfs)
        max-val (apply max (flatten (map :vals metrics)))
        y (scale/linear :domain [0 max-val]
                        :range [0 height])
        ;;The bin width is the same for a given metric across all samples
        bin-width (:bin-width (core/vcf-metric (first vcfs) metric-id))
        dx (- (x-scale bin-width) (x-scale 0))]

    [:div.span12
     (unify (map vector vcfs (repeat metric-id))
            (fn [[vcf metric-id]]
              [:div.histogram
               [:span.label (vcf :filename)]
               [:svg {:width (+ width (* 2 margin)) :height (+ height (* 1.1 margin))}
                [:g {:transform (svg/translate [margin margin])}
                 [:g.distribution {:transform (str (svg/translate [0 height])
                                                   (svg/scale [1 -1]))}
                  (map-indexed (fn [idx v]
                                 [:rect {:x (* dx idx) :width dx
                                         :height (y v)}])
                               ((core/vcf-metric vcf metric-id) :vals))]]]]))]))

(bind! "#histograms"
       (let [vcfs @core/!vcfs]
         (when (seq vcfs)
           (let [metric-extent (get-in (core/vcf-metric (first vcfs)
                                                        (@core/!metric :id))
                                       [:x-scale :domain])
                 {:keys [extent ticks]} (ticks/search metric-extent
                                                      :length width)
                 x (scale/linear :domain extent
                                 :range [0 width])]

             [:div.row#histograms

              ;;histogram distributions
              (histograms* vcfs x)

              ;;x-axis
              [:div.span12
               [:div.axis.abscissa
                [:svg {:width (+ width (* 2 margin)) :height 20}
                 [:g {:transform (svg/translate [margin 2])}
                  (svg/axis x ticks :orientation :bottom)]]]]]))))


;;Range selector
(-> (dom/select "#range-selector")
    (dom/style :width width)
    (double-range/init! #(pp %)))
